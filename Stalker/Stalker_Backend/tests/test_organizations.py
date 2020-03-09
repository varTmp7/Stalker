import pytest

from .config_test import payload_organization_model, test_client, init_db, payload_place_model, init_db_organization
from sqlalchemy_utils import drop_database
import hashlib


def test_get_organizations(test_client, init_db, init_db_organization):
    response = test_client.get('/organizations')
    assert response.status_code == 200
    assert response.headers['req_code'] == '0'
    organizations = response.get_json()['organizations']
    assert len(organizations) == 3
    assert organizations[0]['id'] == 1
    assert organizations[0]['name'] == "Organizzazione1"

    assert organizations[1]['id'] == 2

    assert organizations[2]['id'] == 3


def test_create_new_organization(test_client, init_db, init_db_organization):
    response = test_client.post("/organizations", json=payload_organization_model)
    assert response.status_code == 200
    assert response.headers['req_code'] == '1'
    organization_created = response.get_json()['organization']
    assert organization_created['name'] == "Nuova organizazione"
    assert organization_created['address'] == "Via salcazzo, 9"
    assert organization_created['city'] == "Città"
    assert organization_created['region'] == "Regione"
    assert organization_created['postal_code'] == "35010"
    assert organization_created['nation'] == "Italy"
    assert organization_created['phone_number'] == "+391234567890"
    assert organization_created['email'] == "org@organizzazione.it"
    assert organization_created['type'] == "both"
    assert organization_created['id'] == 4
    assert organization_created['ldap_url'] is None
    assert organization_created['ldap_port'] is None
    assert organization_created['ldap_common_name'] is None
    assert organization_created['ldap_domain_component'] is None


def test_edit_organization(test_client, init_db, init_db_organization):
    modified_payload = payload_organization_model
    modified_payload['email'] = "marco.ferrati@gmail.com"
    response = test_client.put("/organizations/1", json=modified_payload)
    assert response.status_code == 200
    assert response.headers['req_code'] == '3'
    modified_organization = response.get_json()
    assert modified_organization['email'] == "marco.ferrati@gmail.com"


def test_edit_organization_name(test_client, init_db, init_db_organization):
    create_o_res = test_client.post("/organizations", json=payload_organization_model)
    assert create_o_res.status_code == 200
    created_o = create_o_res.get_json()['organization']
    assert created_o['name'] == "Nuova organizazione"
    assert created_o['id'] == 4
    old_name = created_o['name']

    create_p_res = test_client.post("/organizations/{}/places".format(created_o['id']), json=payload_place_model)
    assert create_p_res.status_code == 200
    create_p = create_p_res.get_json()['place']
    assert len(create_p['coordinates']) == 4
    assert create_p['id'] == 1

    check_p_res = test_client.get("/organizations/{}/places".format(created_o['id']))
    check_ps = check_p_res.get_json()
    assert len(check_ps['places']) == 1

    modified_payload = payload_organization_model
    modified_payload['name'] = "Nome organizzazione modificato"
    modified_res = test_client.put("/organizations/{}".format(created_o['id']), json=modified_payload)
    assert modified_res.status_code == 200
    modified_o = modified_res.get_json()
    assert modified_o['name'] == "Nome organizzazione modificato"

    check_migration_res = test_client.get("/organizations/{}/places".format(modified_o['id']))
    migrated_ps = check_migration_res.get_json()
    assert len(migrated_ps['places']) == 1

    drop_database("sqlite:///{}.sqlite".format(hashlib.sha256(old_name.encode('utf-8')).hexdigest()))


def test_delete_organization(test_client, init_db, init_db_organization):
    response = test_client.delete("/organizations/1")
    assert response.status_code == 200
    assert response.headers['req_code'] == '4'
