import pytest

from stalker_backend.ContentProvider.OrganizationContentProvider import OrganizationContentProvider
from .config_test import (get_payload_organization_model, test_client, init_db, get_payload_place_model,
                          init_db_organization,
                          get_organization_token, get_admin_token)
from sqlalchemy_utils import drop_database
from stalker_backend.Utils.AuthUtils import check_admin_permission
from stalker_backend.Models.Organization import Organization
import hashlib


def test_fail_get_organization_for_missing_token(test_client):
    response = test_client.get('/organizations')
    assert response.status_code == 400
    assert response.get_json()['message'] == 'Missing Organization-Token in header request', response.get_data()


def test_fail_get_organization_for_wrong_token(test_client):
    response = test_client.get('/organizations', headers={'Organization-Token': 'TOKEN_FALSO'})
    assert response.status_code == 400
    assert response.get_json()['message'] == 'Token not found', response.get_data()


def test_get_organizations(test_client, init_db, init_db_organization):
    organization_token = get_organization_token()
    response = test_client.get('/organizations', headers={'Organization-Token': organization_token})
    assert response.status_code == 200
    assert response.headers['req_code'] == '0'
    organizations = response.get_json()['organizations']
    assert len(organizations) == 3
    assert organizations[0]['id'] == 1
    assert organizations[0]['name'] == "Organizzazione1"
    assert len(organizations[0]['places']) == 3
    assert organizations[0]['places'][0]['name'] == "Luogo1"
    assert organizations[0]['places'][1]['name'] == "Luogo2"
    assert organizations[0]['places'][2]['name'] == "Luogo3"

    assert organizations[1]['id'] == 2

    assert organizations[2]['id'] == 3


def test_get_single_organization(test_client, init_db, init_db_organization):
    response = test_client.get('/organizations/1',
                               headers={'Authorization': get_admin_token(test_client, 'o_a@gmail.com')})
    assert response.status_code == 200, response.get_json()
    organization = response.get_json()

    assert organization['id'] == 1
    assert organization['name'] == "Organizzazione1"


def test_create_new_organization(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 'o_a@gmail.com')

    response = test_client.post("/organizations",
                                json=get_payload_organization_model(),
                                headers={'Authorization': admin_token})

    assert response.status_code == 200, response.get_json()
    assert response.headers['req_code'] == '1'
    organization_created = response.get_json()['organization']
    assert check_admin_permission(2, Organization.query.get(organization_created['id']))
    assert organization_created['name'] == "Nuova organizazione"
    assert organization_created['image_url'] == "image.url"
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
    admin_token = get_admin_token(test_client, 'o_a@gmail.com')

    modified_payload = get_payload_organization_model()
    modified_payload['email'] = "marco.ferrati@gmail.com"
    response = test_client.put("/organizations/1",
                               json=modified_payload,
                               headers={'Authorization': admin_token})
    assert response.status_code == 200
    assert response.headers['req_code'] == '3'
    modified_organization = response.get_json()
    assert modified_organization['email'] == "marco.ferrati@gmail.com"


def test_edit_organization_name(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 'o_a@gmail.com')
    organization_token = get_organization_token()

    create_o_res = test_client.post("/organizations",
                                    json=get_payload_organization_model(),
                                    headers={'Authorization': admin_token})
    assert create_o_res.status_code == 200, create_o_res.get_json()
    created_o = create_o_res.get_json()['organization']
    assert created_o['name'] == "Nuova organizazione"
    assert created_o['id'] == 4
    assert check_admin_permission(2, Organization.query.get(created_o['id']))
    old_name = created_o['name']

    create_p_res = test_client.post("/organizations/{}/places".format(created_o['id']),
                                    json=get_payload_place_model(),
                                    headers={'Authorization': admin_token})
    assert create_p_res.status_code == 200, create_p_res.get_json()
    create_p = create_p_res.get_json()['place']
    assert len(create_p['coordinates']) == 4
    assert create_p['id'] == 1

    check_p_res = test_client.get("/organizations/{}/places".format(created_o['id']),
                                  headers={'Organization-Token': organization_token})
    check_ps = check_p_res.get_json()
    assert len(check_ps['places']) == 1

    modified_payload = get_payload_organization_model()
    modified_payload['name'] = "Nome organizzazione modificato"
    modified_res = test_client.put("/organizations/{}".format(created_o['id']),
                                   json=modified_payload,
                                   headers={'Authorization': admin_token})
    assert modified_res.status_code == 200, modified_res.get_json()
    modified_o = modified_res.get_json()
    assert modified_o['name'] == "Nome organizzazione modificato"

    check_migration_res = test_client.get("/organizations/{}/places".format(modified_o['id']),
                                          headers={'Organization-Token': organization_token})
    migrated_ps = check_migration_res.get_json()
    assert len(migrated_ps['places']) == 1

    drop_database("sqlite:///{}.sqlite".format(hashlib.sha256(old_name.encode('utf-8')).hexdigest()))


def test_delete_organization(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 'o_a@gmail.com', 'password')
    response = test_client.delete("/organizations/1",
                                  headers={'Authorization': admin_token})
    assert response.status_code == 200
    assert response.headers['req_code'] == '4'


def test_edit_organization_without_changing_name_using_organization_content_provider(test_client, init_db,
                                                                                     init_db_organization):
    content_provider = OrganizationContentProvider('Organizzazione1')
    assert content_provider.changed_organization_name('Organizzazione1', 'Organizzazione1') is None


def test_delete_twice_same_organization(test_client, init_db, init_db_organization):
    content_provider = OrganizationContentProvider('Organizzazione1')
    assert content_provider.delete_organization()
    assert not content_provider.delete_organization()
