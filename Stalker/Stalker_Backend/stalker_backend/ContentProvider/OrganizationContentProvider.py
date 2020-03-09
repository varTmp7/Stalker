import hashlib
import os

from flask_restful import abort
from sqlalchemy import create_engine
from sqlalchemy.orm import sessionmaker, Query, make_transient
from sqlalchemy_utils import database_exists, create_database
from rethinkdb import RethinkDB
from rethinkdb.errors import ReqlDriverError

from stalker_backend.Models import Place, Track
from stalker_backend.config import POSTGRES_USER, POSTGRES_PASSWORD, RETHINK_URL


class ExtendedQuery(Query):
    def get_or_404(self, ident, description=None):
        """Like :meth:`get` but aborts with 404 if not found instead of returning ``None``."""

        rv = self.get(ident)
        if rv is None:
            abort(404, description=description)
        return rv


Session = sessionmaker()


class OrganizationContentProvider:
    db_url = None
    db = None
    session = None
    name_hash = None

    rethink_connection = RethinkDB()
    __rethink_db = None
    rethink_organization = None  # Tabella dell'organizzazione alla quale viene riferito l'OrganizationContentProvider

    def __init__(self, organization_name):
        self.name_hash = hashlib.sha256(organization_name.encode('utf-8')).hexdigest()
        if os.environ.get('NOT_K8S'):
            self.db_url = 'sqlite:///{}.sqlite'.format(self.name_hash)
        else:
            self.db_url = "postgres://{}:{}@postgres:5432/{}".format(POSTGRES_USER, POSTGRES_PASSWORD, self.name_hash)
        self.db = create_engine(self.db_url)

        if not database_exists(self.db.url):
            create_database(self.db.url)

        Place.Place.metadata.create_all(self.db)
        Track.Track.metadata.create_all(self.db)
        self.session = Session(bind=self.db, query_cls=ExtendedQuery)

        try:
            self.rethink_connection.connect(RETHINK_URL, 28015).repl()
        except (ConnectionRefusedError, ReqlDriverError):
            abort(500, description="Failed to connect to RethinkDB")

        # assumiamo per vero che il DB 'stalker_organizations' esista in quanto creato a priori (vedere __init__.py)
        self.__rethink_db = self.rethink_connection.db('stalker_organizations')

        if self.name_hash not in self.__rethink_db.table_list().run():
            self.__rethink_db.table_create(self.name_hash).run()

        self.rethink_organization = self.__rethink_db.table(self.name_hash)

    def __del__(self):
        self.session.close()

    def get_numeber_of_people(self, place_id):
        return self.rethink_organization.get(place_id)

    def add_new_track(self, track, entered, place_id):
        if track:
            self.session.add(track)
            self.session.commit()

        if entered == True:  # Una persona entra nel luogo idicato
            self.rethink_organization.get(place_id).update(
                {'number_of_people': self.rethink_connection.row['number_of_people'] + 1}).run()
        else:
            pass  # Una persona esce dal luogo indicato
            self.rethink_organization.get(place_id).update(
                {'number_of_people': self.rethink_connection.row['number_of_people'] - 1}).run()

    def create_new_place(self, place):
        self.session.add(place)
        self.session.commit()
        self.rethink_organization.insert({'id': str(place.id), 'number_of_people': 0}).run()

    def delete_place(self, place):
        self.rethink_organization.get(place.id).delete().run()
        self.session.delete(place)
        self.session.commit()

    def delete_organization(self):
        self.__rethink_db.table_drop(self.name_hash).run()

    def changed_organization_name(self, old_name, new_name):
        if old_name == new_name:
            return

        # Update of name_hash property
        new_name_hash = hashlib.sha256(new_name.encode('utf-8')).hexdigest()

        if os.environ.get('NOT_K8S'):
            new_db_url = 'sqlite:///{}.sqlite'.format(new_name_hash)
        else:
            new_db_url = "postgres://{}:{}@postgres:5432/{}".format(POSTGRES_USER, POSTGRES_PASSWORD, new_name_hash)
        new_db = create_engine(new_db_url)

        if not database_exists(new_db.url):
            create_database(new_db.url)

        Place.Place.metadata.create_all(new_db)
        Track.Track.metadata.create_all(new_db)
        new_session = Session(bind=new_db)

        for place in self.session.query(Place.Place).all():
            self.session.expunge(place)
            make_transient(place)
            new_session.add(place)

        for track in self.session.query(Track.Track).all():
            self.session.expunge(track)
            make_transient(track)
            new_session.add(track)

        new_session.commit()

        self.rethink_organization.config().update({'name': new_name_hash}).run()
