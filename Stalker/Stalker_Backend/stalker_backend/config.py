import os

POSTGRES_USER = os.environ.get('POSTGRES_USER')
POSTGRES_PASSWORD = os.environ.get('POSTGRES_PASSWORD')

if os.environ.get('NOT_K8S'):
    DATABASE_URL = "sqlite:///example.sqlite"
    RETHINK_URL = "localhost"
else:
    DATABASE_URL = f'postgres://{POSTGRES_USER}:{POSTGRES_PASSWORD}@postgres:5432/stalker-organizations'
    RETHINK_URL = 'rethink'


class Config:
    """Set Flask configuration vars from .env file."""

    # General
    TESTING = True  # os.environ["TESTING"]
    FLASK_DEBUG = True  # os.environ["FLASK_DEBUG"]
    SECRET_KEY = "dev"  # change before production

    # Database
    # SQLALCHEMY_DATABASE_URI = "sqlite:///example.sqlite"  # os.environ["SQLALCHEMY_DATABASE_URI"]
    SQLALCHEMY_DATABASE_URI = DATABASE_URL
    SQLALCHEMY_ECHO = False  # os.environ["SQLALCHEMY_ECHO"]
    SQLALCHEMY_TRACK_MODIFICATIONS = False  # os.environ["SQLALCHEMY_TRACK_MODIFICATIONS"]
