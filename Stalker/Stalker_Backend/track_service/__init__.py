from flask import Flask
from flask_sqlalchemy import SQLAlchemy
from flask_restful import Api
from flask_cors import CORS
from flask_jwt_extended import JWTManager
from track_service import config

db_alchemy = SQLAlchemy()
jwt = JWTManager()


def _register_api():
    api = Api()
    from track_service.TracksList import TrackList
    api.add_resource(TrackList, '/organizations/<organization_id>/places/<place_id>/tracks')
    return api


def _initialize_database():
    from track_service import Track
    db_alchemy.create_all()


def _initialize_extension(app):
    CORS(app)
    db_alchemy.init_app(app)
    jwt.init_app(app)


def create_app(test_config=None):
    app: Flask = Flask(__name__, instance_relative_config=True)
    if not test_config:
        app.config.from_object(config.Config)
    else:
        app.config.from_mapping(test_config)

    _initialize_extension(app)
    with app.app_context():
        api = _register_api()
        api.init_app(app)
        _initialize_database()
        return app
