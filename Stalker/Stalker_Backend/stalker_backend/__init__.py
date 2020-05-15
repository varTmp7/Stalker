from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_restful import Api
from sys import exit

from sqlalchemy_utils import database_exists, create_database

from . import config
from rethinkdb import RethinkDB
from rethinkdb.errors import ReqlDriverError
from flask_cors import CORS
from flask_jwt_extended import JWTManager
from flask_mail import Mail

db_alchemy = SQLAlchemy()
rethink_db = RethinkDB()
jwt = JWTManager()
mail = Mail()
cors = CORS()


def _register_blueprints(app):
    from .Routes import Auth, Others
    app.register_blueprint(Auth.auth_routes)
    app.register_blueprint(Others.other_routes)


def _register_api():
    api = Api()
    from stalker_backend.Resources.OrganizationList import OrganizationList
    from stalker_backend.Resources.OrganizationItem import OrganizationItem
    from stalker_backend.Resources.PlaceList import PlaceList
    from stalker_backend.Resources.PlaceItem import PlaceItem
    from stalker_backend.Resources.TrackListForPlace import TrackListForPlace
    from stalker_backend.Resources.TrackListForOrganization import TrackListForOrganization
    from stalker_backend.Resources.OrganizationAdminsList import OrganizationAdminList
    from stalker_backend.Resources.AdminList import AdminList
    from stalker_backend.Resources.ApprovePlaceList import ApprovePlaceList
    from stalker_backend.Resources.OrganizationAdminsEditRoleAndDelete import OrganizationAdminsEditRoleAndDelete
    from stalker_backend.Resources.AdminItem import AdminItem
    from stalker_backend.Resources.OrganizationsAdminList import OrganizationsAdminList
    from stalker_backend.Resources.ImageList import ImageList

    from .Resources.ResourceClass.OrganizationResource import OrganizationResource
    from .Resources.ResourceClass.PlaceResource import PlaceResource

    organization_resource = OrganizationResource()
    place_resource = PlaceResource(organization_resource)

    api.add_resource(OrganizationList,
                     '/organizations')
    api.add_resource(OrganizationItem,
                     '/organizations/<organization_id>',
                     resource_class_kwargs={'organization_resource': organization_resource})
    api.add_resource(PlaceList,
                     '/organizations/<organization_id>/places',
                     resource_class_kwargs={'organization_resource': organization_resource})
    api.add_resource(PlaceItem,
                     '/organizations/<organization_id>/places/<place_id>',
                     resource_class_kwargs={'place_resource': place_resource})
    api.add_resource(TrackListForPlace,
                     '/organizations/<organization_id>/places/<place_id>/tracks',
                     resource_class_kwargs={'place_resource': place_resource})
    api.add_resource(TrackListForOrganization,
                     '/organizations/<organization_id>/tracks',
                     resource_class_kwargs={'organization_resource': organization_resource})
    api.add_resource(OrganizationAdminList,
                     '/organizations/<organization_id>/admins',
                     resource_class_kwargs={'organization_resource': organization_resource})
    api.add_resource(OrganizationAdminsEditRoleAndDelete,
                     '/organizations/<organization_id>/admins/<admin_id>',
                     resource_class_kwargs={'organization_resource': organization_resource})
    api.add_resource(AdminList,
                     '/admins')
    api.add_resource(AdminItem,
                     '/admins/<admin_id>')
    api.add_resource(ApprovePlaceList,
                     '/place-to-approve')
    api.add_resource(OrganizationsAdminList, '/organizations-admin')
    api.add_resource(ImageList, '/images')
    return api


def _check_database():
    try:
        rethink_db.connect(config.get_rethink_url(), 28015).repl()  # Connessione al database RethinkDB
    except (ConnectionRefusedError, ReqlDriverError):
        print("Impossible collegarsi a RethinkDB.")
        print("URL: {}:28015".format(config.get_rethink_url()))
        print("Inizializzazione server annullata")
        exit(4)


def _initialize_database():
    if config.DATABASE_NAME not in rethink_db.db_list().run():
        rethink_db.db_create(config.DATABASE_NAME).run()

    if not database_exists(db_alchemy.engine.url):
        create_database(db_alchemy.engine.url)

    from .Models import Organization, Admin, OrganizationsAdmins
    db_alchemy.create_all()

    try:
        db_alchemy.session.add(Admin.Admin({'email': 'admin@gmail.com',
                                            'password': 'password',
                                            'name': 'System',
                                            'surname': 'Admin'}, False, True))
        db_alchemy.session.commit()
    except Exception:
        print("Admin already created")


def _initialize_extension(app):
    cors.init_app(app, supports_credentials=True)
    db_alchemy.init_app(app)
    jwt.init_app(app)
    mail.init_app(app)


def create_app(test_config=None):
    # create and configure the app
    app: Flask = Flask(__name__, instance_relative_config=True)
    if not test_config:
        app.config.from_object(config.Config)
    else:
        app.config.from_mapping(test_config)

    _check_database()
    _initialize_extension(app)
    with app.app_context():
        api = _register_api()
        api.init_app(app)
        _initialize_database()
        _register_blueprints(app)
        return app
