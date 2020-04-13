import pytest
import os
from os import path

from sqlalchemy_utils import drop_database

from stalker_backend import create_app, db_alchemy
from stalker_backend.Models import Organization, Place, Track, Admin, OrganizationsAdmins
from stalker_backend.ContentProvider import OrganizationContentProvider
from datetime import datetime


def get_payload_organization_model():
    return {
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


def get_payload_place_model():
    return {
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


def get_payload_track_model_entered():
    return {
        "authenticated": True,
        "entered": True,
        'date_time': datetime.now().strftime("%Y-%m-%dT%H:%M:%S"),
        "auth_type": "ldapv3",
        "username": "bob",
        "password": "password"
    }


def get_payload_track_model_exited():
    return {
        "authenticated": True,
        "entered": False,
        'date_time': datetime.now().strftime("%Y-%m-%dT%H:%M:%S"),
        "auth_type": "ldapv3",
        "username": "bob",
        "password": "password"
    }


def get_payload_admin_manager():
    return {
        "name": "Manager",
        "surname": "Admin",
        "email": "m_a@gmail.com",
        "role": "MANAGER",
        "organization_id": 1
    }


def login(client, email, password):
    return client.post('/login', data={'email': email, 'password': password}, follow_redirects=True)


def get_admin_token(client, email, password='password'):
    return 'Bearer ' + client.post('/login',
                                   data={'email': email, 'password': password},
                                   follow_redirects=True).get_json()['access_token']


def get_admin_token_for_organization(client, old_token, organization_id):
    return 'Bearer ' + client.get('/select-organization?organization_id={}'.format(organization_id),
                                  headers={'Authorization': old_token}).get_json()['access_token']


def get_organization_token():
    return Organization.Organization.query.get(1).token


@pytest.fixture
def test_client():
    flask_app = create_app(test_config={
        'TESTING': True,
        'FLASK_DEBUG': True,
        'SECRET_KEY': 'dev',
        'SQLALCHEMY_DATABASE_URI': "sqlite:///TESTDB.sqlite",
        'SQLALCHEMY_ECHO': False,
        'SQLALCHEMY_TRACK_MODIFICATIONS': False,
        'MAIL_SERVER': "smtp.gmail.com",
        'MAIL_PORT': 465,
        'MAIL_USE_SSL': True,
        'MAIL_DEFAULT_SENDER': "vartmp7@gmail.com",
        'MAIL_USERNAME': "vartmp7@gmail.com",
        'MAIL_PASSWORD': os.environ["EMAIL_PASSWORD"],
        'MAIL_SUPPRESS_SEND': os.environ["MAIL_SUPPRESS_SEND"]
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

    db_alchemy.drop_all()
    db_alchemy.create_all()
    organization_1 = Organization.Organization({'name': "Organizzazione1",
                                                'address': "Indirizzo organizzazione 1",
                                                'city': "Città organizzazione 1",
                                                'region': "Regione organizzazione 1",
                                                'postal_code': "35010",
                                                'nation': "Nazione organizzazione 1",
                                                'phone_number': "+391234567890",
                                                'email': "organizzazione@org1.it",
                                                'type': "PRIVATE",
                                                'ldap_url': "localhost",
                                                'ldap_port': 389,
                                                'ldap_common_name': "cn=users,cn=accounts",
                                                'ldap_domain_component': "dc=daf,dc=test,dc=it"})

    organization_2 = Organization.Organization({'name': "Organizzazione2",
                                                'address': "Indirizzo organizzazione 2",
                                                'city': "Città organizzazione 2",
                                                'region': "Regione organizzazione 2",
                                                'postal_code': "35010",
                                                'nation': "Nazione organizzazione 2",
                                                'phone_number': "+391234567890",
                                                'email': "organizzazione@org2.it",
                                                'type': "PUBLIC",
                                                })

    organization_3 = Organization.Organization({'name': "Organizzazione3",
                                                'address': "Indirizzo organizzazione 3",
                                                'city': "Città organizzazione 3",
                                                'region': "Regione organizzazione 3",
                                                'postal_code': "35010",
                                                'nation': "Nazione organizzazione 3",
                                                'phone_number': "+391234567890",
                                                'email': "organizzazione@org3.it",
                                                'type': "PRIVATE",
                                                'ldap_url': "localhost",
                                                'ldap_port': 389,
                                                'ldap_common_name': "cn=users,cn=accounts",
                                                'ldap_domain_component': "dc=daf,dc=test,dc=it"})

    db_alchemy.session.add(organization_1)
    db_alchemy.session.add(organization_2)
    db_alchemy.session.add(organization_3)

    system_admin = Admin.Admin({'email': 's_a@gmail.com',
                                'password': 'password',
                                'name': 'System',
                                'surname': 'Admin'},
                               is_owner=False,
                               is_system_admin=True)
    owner_admin = Admin.Admin({'email': 'o_a@gmail.com',
                               'password': 'password',
                               'name': 'Owner',
                               'surname': 'Admin'},
                              is_owner=True,
                              is_system_admin=False)

    db_alchemy.session.add(system_admin)
    db_alchemy.session.add(owner_admin)

    db_alchemy.session.commit()

    connection_1 = OrganizationsAdmins.OrganizationsAdmins(organization_1.id,
                                                           owner_admin.id,
                                                           OrganizationsAdmins.Role.OWNER)
    connection_2 = OrganizationsAdmins.OrganizationsAdmins(organization_2.id,
                                                           owner_admin.id,
                                                           OrganizationsAdmins.Role.OWNER)
    connection_3 = OrganizationsAdmins.OrganizationsAdmins(organization_3.id,
                                                           owner_admin.id,
                                                           OrganizationsAdmins.Role.OWNER)

    db_alchemy.session.add(connection_1)
    db_alchemy.session.add(connection_2)
    db_alchemy.session.add(connection_3)

    db_alchemy.session.commit()

    yield db_alchemy

    db_alchemy.drop_all()


@pytest.fixture
def init_db_organization():
    content_provider_organization_1 = OrganizationContentProvider.OrganizationContentProvider("Organizzazione1")

    place_1_1 = Place.Place({
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
        "name": "Luogo1",
        "num_max_people": 100
    })

    place_2_1 = Place.Place({
        "coordinates": [
            {
                "latitude": 10.1,
                "longitude": 10.2
            },
            {
                "latitude": 20.1,
                "longitude": 20.2
            },
            {
                "latitude": 30.1,
                "longitude": 30.2
            },
            {
                "latitude": 40.1,
                "longitude": 40.2
            }
        ],
        "organization_id": 1,
        "name": "Luogo2",
        "num_max_people": 100
    })

    place_3_1 = Place.Place({
        "coordinates": [
            {
                "latitude": 100.1,
                "longitude": 100.2
            },
            {
                "latitude": 200.1,
                "longitude": 200.2
            },
            {
                "latitude": 300.1,
                "longitude": 300.2
            },
            {
                "latitude": 400.1,
                "longitude": 400.2
            }
        ],
        "organization_id": 1,
        "name": "Luogo3",
        "num_max_people": 100
    })

    place_1_2 = Place.Place({
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
        "name": "Luogo1",
        "num_max_people": 100
    })

    content_provider_organization_1.create_new_place(place_1_1)
    content_provider_organization_1.create_new_place(place_2_1)
    content_provider_organization_1.create_new_place(place_3_1)

    track_1_1 = Track.Track({'entered': True,
                             'uid_number': 1000,
                             'username': 'mferrati',
                             'name': 'Marco',
                             'surname': 'Ferrati',
                             'date_time': datetime(1970, 1, 1, 0, 0, 0),
                             'place_id': 1})
    track_2_1 = Track.Track({'entered': False,
                             'uid_number': 1000,
                             'username': 'mferrati',
                             'name': 'Marco',
                             'surname': 'Ferrati',
                             'date_time': datetime(1970, 1, 1, 1, 1, 11),
                             'place_id': 1})

    content_provider_organization_1.add_new_track(track_1_1, True, 1)
    content_provider_organization_1.add_new_track(track_2_1, False, 1)

    yield content_provider_organization_1

    content_provider_organization_1.delete_organization()
    drop_database(content_provider_organization_1.db_url)
