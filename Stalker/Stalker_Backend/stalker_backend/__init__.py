from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_restful import Api
from os import abort
from . import config
from sqlalchemy_utils import database_exists, create_database
from rethinkdb import RethinkDB
from rethinkdb.errors import ReqlDriverError
from flask_cors import CORS

db_alchemy = SQLAlchemy()
rethink_db = RethinkDB()


def create_app(env=None, response=None, test_config=None):
    # create and configure the app
    app: Flask = Flask(__name__, instance_relative_config=True)
    if not test_config:
        # print("Setting up from object")
        app.config.from_object(config.Config)
    else:
        # print("Setting up from map")
        app.config.from_mapping(test_config)

    try:
        rethink_db.connect(config.RETHINK_URL, 28015).repl()  # Connessione al database RethinkDB
    except (ConnectionRefusedError, ReqlDriverError):
        print("Impossible collegarsi a RethinkDB.")
        print("URL: {}:28015".format(config.RETHINK_URL))
        print("Inizializzazione server annullata")
        abort()

    if 'stalker_organizations' not in rethink_db.db_list().run():
        rethink_db.db_create('stalker_organizations').run()

    CORS(app)

    db_alchemy.init_app(app)
    api = Api()

    with app.app_context():
        from .Resources import OrganizationList, OrganizationItem, PlaceList, PlaceItem, TrackListForPlace, \
            TrackListForOrganization
        api.add_resource(OrganizationList.OrganizationList,
                         '/organizations')
        api.add_resource(OrganizationItem.OrganizationItem,
                         '/organizations/<organization_id>')
        api.add_resource(PlaceList.PlaceList,
                         '/organizations/<organization_id>/places')
        api.add_resource(PlaceItem.PlaceItem,
                         '/organizations/<organization_id>/places/<place_id>')
        api.add_resource(TrackListForPlace.TrackListForPlace,
                         '/organizations/<organization_id>/places/<place_id>/tracks')
        api.add_resource(TrackListForOrganization.TrackListForOrganization,
                         '/organizations/<organization_id>/tracks')

        api.init_app(app)

        if not database_exists(db_alchemy.engine.url):
            create_database(db_alchemy.engine.url)

        from .Models import Organization
        # db_alchemy.drop_all()
        db_alchemy.create_all()

        print(app.url_map)
        return app
