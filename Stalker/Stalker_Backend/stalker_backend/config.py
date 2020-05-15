from datetime import timedelta
from sys import exit
from os import environ

ALLOWED_EXTENSIONS = {'png', 'jpg', 'jpeg'}
DATABASE_NAME = 'stalker_organizations_0_5'

def get_rethink_url():
    return environ.get('RETHINK_URL')  # "localhost"


def get_db_url(db_name: str):
    database_type = environ.get('DATABASE_TYPE')
    base_postgres_url = environ.get('BASE_POSTGRES_URL')  # "sqlite:///example.sqlite"
    postgres_user = environ.get('POSTGRES_USER')
    postgres_password = environ.get('POSTGRES_PASSWORD')

    if database_type:
        if database_type == 'sqlite:///':
            # Database di tipo SQLite3
            return database_type + db_name + '.sqlite'
        elif database_type == 'postgres://':
            # Database di tipo PostgreSQL
            if base_postgres_url and postgres_user and postgres_password:
                return database_type + postgres_user + ':' + postgres_password + '@' + base_postgres_url + '/' + db_name
            else:
                print("BASE_POSTGRES_URL, POSTGRES_USER or POSTGRES_PASSWORD missing")
                print('''You have used postgres:// as DATABASE_TYPE but you don't set BASE_POSTGRES_URL, 
                        POSTGRES_USER or POSTGRES_PASSWORD''')
                exit(3)
        else:
            print("Database type not supported, use sqlite:/// or postgres://")
            exit(2)
    else:
        print("DATABASE_TYPE is empty")
        print("set it to sqlite:/// or postgres://")
        exit(1)


''' f'postgres://{POSTGRES_USER}:{POSTGRES_PASSWORD}@localhost:5432' 
#f'postgres://postgres:BB16xh7KJRyc@postgresql-1-postgresql-svc:5432/stalker-organizations'''


class Config:
    # General
    TESTING = environ["TESTING"]
    FLASK_DEBUG = environ["FLASK_DEBUG"]
    SECRET_KEY = "dev"  # change before production
    # Database
    SQLALCHEMY_DATABASE_URI = get_db_url(DATABASE_NAME)
    SQLALCHEMY_ECHO = False
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    # Flask-Mail
    MAIL_SERVER = "smtp.gmail.com"
    MAIL_PORT = 465
    MAIL_USE_SSL = True
    MAIL_DEFAULT_SENDER = "vartmp7@gmail.com"
    MAIL_USERNAME = "vartmp7@gmail.com"
    MAIL_PASSWORD = environ["EMAIL_PASSWORD"]
    MAIL_SUPPRESS_SEND = environ["MAIL_SUPPRESS_SEND"]
    # JWT
    JWT_REFRESH_TOKEN_EXPIRES = timedelta(hours=1)
