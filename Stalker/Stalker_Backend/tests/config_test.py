import pytest
import os
from os import path

from sqlalchemy_utils import drop_database

from stalker_backend import create_app, db_alchemy
from stalker_backend.Models import Organization, Place, Track
from stalker_backend.ContentProvider import OrganizationContentProvider
from datetime import datetime

payload_organization_model = {
    "name": "Nuova organizazione",
    "address": "Via salcazzo, 9",
    "city": "Città",
    "region": "Regione",
    "postal_code": "35010",
    "nation": "Italy",
    "phone_number": "+391234567890",
    "email": "org@organizzazione.it",
    "type": "BOTH"
}

payload_place_model = {
    "coordinates": [
        {
            "latitude": 1.1,
            "longitude": 1.2
        },
        {
            "latitude": 2.1,
            "longitude": 2.2
        },
        {
            "latitude": 3.1,
            "longitude": 3.2
        },
        {
            "latitude": 4.1,
            "longitude": 4.2
        }
    ],
    "organization_id": 1,
    "name": "nuovo luogo",
    "num_max_people": 100
}

payload_track_model_entered = {
    "entered": True,
    "uid_number": 1234,
    "username": "mferrati",
    "name": "Marco",
    "surname": "Ferrati",
    'date_time': datetime.now().strftime("%Y-%m-%dT%H:%M:%S"),
}

payload_track_model_exited = {
    "entered": False,
    "uid_number": 1234,
    "username": "mferrati",
    "name": "Marco",
    "surname": "Ferrati",
    'date_time': datetime.now().strftime("%Y-%m-%dT%H:%M:%S"),
}


@pytest.fixture
def test_client():
    os.environ['NOT_K8S'] = "1"
    flask_app = create_app(test_config={
        'TESTING': True,
        'FLASK_DEBUG': True,
        'SECRET_KEY': 'dev',
        'SQLALCHEMY_DATABASE_URI': "sqlite:///TESTDB.sqlite",
        'SQLALCHEMY_ECHO': False,
        'SQLALCHEMY_TRACK_MODIFICATIONS': False,
    })
    testing_client = flask_app.test_client()

    # Establish an application context before running the tests.
    ctx = flask_app.app_context()
    ctx.push()

    yield testing_client  # this is where the testing happens!

    ctx.pop()


@pytest.fixture
def init_db():
    for file in os.listdir('.'):
        if path.splitext(file)[1] == ".sqlite":
            os.remove(file)

    os.environ['NOT_K8S'] = "1"
    db_alchemy.drop_all()
    db_alchemy.create_all()
    organization_1 = Organization.Organization("Organizzazione1",
                                               "Indirizzo organizzazione 1",
                                               "Città organizzazione 1",
                                               "Regione organizzazione 1",
                                               "35010",
                                               "Nazione organizzazione 1",
                                               "+391234567890",
                                               "organizzazione@org1.it",
                                               "PUBLIC")
    organization_2 = Organization.Organization("Organizzazione2",
                                               "Indirizzo organizzazione 2",
                                               "Città organizzazione 2",
                                               "Regione organizzazione 2",
                                               "35010",
                                               "Nazione organizzazione 2",
                                               "+391234567890",
                                               "organizzazione@org2.it",
                                               "PRIVATE",
                                               "ldap://url.to.ldap.server",
                                               123,
                                               "users.account",
                                               "org.organizzazione.2")

    organization_3 = Organization.Organization("Organizzazione3",
                                               "Indirizzo organizzazione 3",
                                               "Città organizzazione 3",
                                               "Regione organizzazione 3",
                                               "35010",
                                               "Nazione organizzazione 3",
                                               "+391234567890",
                                               "organizzazione@org3.it",
                                               "PRIVATE",
                                               "ldap://url.to.ldap.server_3",
                                               123,
                                               "users.account",
                                               "org.organizzazione.3")

    db_alchemy.session.add(organization_1)
    db_alchemy.session.add(organization_2)
    db_alchemy.session.add(organization_3)

    db_alchemy.session.commit()

    yield db_alchemy

    db_alchemy.drop_all()


@pytest.fixture
def init_db_organization():
    content_provider_organization_1 = OrganizationContentProvider.OrganizationContentProvider("Organizzazione1")

    place_1_1 = Place.Place("Luogo1",
                            1.1,
                            1.2,
                            2.1,
                            2.2,
                            3.1,
                            3.2,
                            4.1,
                            4.2,
                            100,
                            1)

    place_2_1 = Place.Place("Luogo2",
                            10.1,
                            10.2,
                            20.1,
                            20.2,
                            30.1,
                            30.2,
                            40.1,
                            40.2,
                            100,
                            1)

    place_3_1 = Place.Place("Luogo3",
                            100.1,
                            100.2,
                            200.1,
                            200.2,
                            300.1,
                            300.2,
                            400.1,
                            400.2,
                            100,
                            1)

    content_provider_organization_1.create_new_place(place_1_1)
    content_provider_organization_1.create_new_place(place_2_1)
    content_provider_organization_1.create_new_place(place_3_1)

    track_1_1 = Track.Track(True, 1000, 'mferrati', 'Marco', 'Ferrati', datetime.now(), 1)
    track_2_1 = Track.Track(False, 1000, 'mferrati', 'Marco', 'Ferrati', datetime.now(), 1)

    content_provider_organization_1.add_new_track(track_1_1, True, 1)
    content_provider_organization_1.add_new_track(track_2_1, False, 1)

    yield content_provider_organization_1

    content_provider_organization_1.delete_organization()
    drop_database(content_provider_organization_1.db_url)
