# File usato da pytest per la sua configurazione interna
from os import environ
from base64 import b64decode

def pytest_configure():
    environ['FLASK_APP'] = 'stalker_backend'
    environ['FLASK_ENV'] = 'development'
    environ['RETHINK_URL'] = 'localhost'
    environ['DATABASE_TYPE'] = 'sqlite:///'
    environ['TESTING'] = "True"
    environ['FLASK_DEBUG'] = "True"
    environ['EMAIL_PASSWORD'] = b64decode('VHVsbGlvUnVsZWdnaWE=').decode('utf-8')
    environ['MAIL_SUPPRESS_SEND'] = "True"


def pytest_unconfigure():
    del environ['FLASK_APP']
    del environ['FLASK_ENV']
    del environ['RETHINK_URL']
    del environ['DATABASE_TYPE']
    del environ['TESTING']
    del environ['FLASK_DEBUG']
    del environ['EMAIL_PASSWORD']
    del environ['MAIL_SUPPRESS_SEND']