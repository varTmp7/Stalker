from stalker_backend.Models.Admin import Admin
from .config_test import (init_db, test_client, get_admin_token, init_db_organization, get_payload_admin_manager,
                          get_payload_organization_model, get_admin_token_for_organization)
from time import sleep


def test_get_admins_for_first_organization(test_client, init_db, init_db_organization):
    response = test_client.get('/organizations/1/admins',
                               headers={'Authorization': get_admin_token(test_client, 'o_a@gmail.com')})
    assert response.status_code == 200, response.get_json()

    admins = response.get_json()['admins']
    assert len(admins) == 1
    assert admins[0]['name'] == 'Owner'
    assert admins[0]['surname'] == 'Admin'
    assert admins[0]['email'] == 'o_a@gmail.com'
    assert admins[0]['role'] == 'owner'


def test_admin_creation(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 'o_a@gmail.com')
    response = test_client.post('/organizations/1/admins',
                                json=get_payload_admin_manager(),
                                headers={'Authorization': admin_token})
    assert response.status_code == 200, response.get_json()
    admin_created = response.get_json()
    assert admin_created['name'] == 'Manager'
    assert admin_created['surname'] == 'Admin'
    assert admin_created['email'] == 'm_a@gmail.com'
    assert admin_created['role'] == 'manager'

    response_admins = test_client.get('/organizations/1/admins',
                                      headers={'Authorization': admin_token})
    assert response_admins.status_code == 200, response.get_json()

    admins = response_admins.get_json()['admins']
    assert len(admins) == 2
    assert admins[0]['name'] == 'Owner'
    assert admins[0]['surname'] == 'Admin'
    assert admins[0]['email'] == 'o_a@gmail.com'
    assert admins[0]['role'] == 'owner'

    assert admins[1]['name'] == 'Manager'
    assert admins[1]['surname'] == 'Admin'
    assert admins[1]['email'] == 'm_a@gmail.com'
    assert admins[1]['role'] == 'manager'

    new_admin_token = get_admin_token_for_organization(test_client,
                                                       get_admin_token(test_client,
                                                                       get_payload_admin_manager()['email']),
                                                       1)

    organization_detail_response = test_client.get('/organizations/1',
                                                   headers={'Authorization': new_admin_token})

    assert organization_detail_response.status_code == 200


def test_create_admin_without_organization_id(test_client, init_db, init_db_organization):
    wrong_payload = get_payload_admin_manager()
    wrong_payload['organization_id'] = None
    admin_token = get_admin_token(test_client, 'o_a@gmail.com')
    response = test_client.post('/organizations/1/admins',
                                json=wrong_payload,
                                headers={'Authorization': admin_token})
    assert response.status_code == 400, response.get_json()


def test_create_admin_already_connected_to_organization(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 'o_a@gmail.com')
    first_creation_response = test_client.post('/organizations/1/admins',
                                               json=get_payload_admin_manager(),
                                               headers={'Authorization': admin_token})
    assert first_creation_response.status_code == 200

    second_creation_response = test_client.post('/organizations/1/admins',
                                                json=get_payload_admin_manager(),
                                                headers={'Authorization': admin_token})
    assert second_creation_response.status_code == 409


def test_system_admin_tries_to_create_admin_for_an_organization(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 's_a@gmail.com')
    response = test_client.post('/organizations/1/admins',
                                json=get_payload_admin_manager(),
                                headers={'Authorization': admin_token})
    assert response.status_code == 403, response.get_json()


def test_system_admin_tries_to_edit_an_organization(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 's_a@gmail.com')
    response = test_client.put('/organizations/1',
                               json=get_payload_organization_model(),
                               headers={'Authorization': admin_token})
    assert response.status_code == 403, response.get_json()


def test_system_admin_tries_to_get_info_of_an_organization(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 's_a@gmail.com')
    response = test_client.get('/organizations/1',
                               headers={'Authorization': admin_token})
    assert response.status_code == 403, response.get_json()


def test_system_admin_creation(test_client, init_db, init_db_organization):
    response = test_client.post('/admins',
                                json={'name': 'New System',
                                      'surname': 'Admin',
                                      'email': 'new_s_a@gmail.com',
                                      'role': 'SYSTEM',
                                      'is_system_admin': True})
    assert response.status_code == 200, response.get_json()


def test_owner_admin_creation(test_client, init_db, init_db_organization):
    response = test_client.post('/admins',
                                json={'name': 'New Owner',
                                      'surname': 'Admin',
                                      'email': 'new_o_a@gmail.com',
                                      'role': 'OWNER',
                                      'is_owner': True})
    assert response.status_code == 200, response.get_json()


def test_admin_creation_with_missing_system_and_admin(test_client, init_db, init_db_organization):
    response = test_client.post('/admins',
                                json={'name': 'New Owner',
                                      'surname': 'Admin',
                                      'email': 'new_o_a@gmail.com',
                                      'role': 'OWNER'})
    assert response.status_code == 400, response.get_json()
    assert response.get_json()['message'] == "Missing is_owner or is_system_admin"


def test_admin_creation_with_same_value_for_system_and_admin(test_client, init_db, init_db_organization):
    response = test_client.post('/admins',
                                json={'name': 'New Owner',
                                      'surname': 'Admin',
                                      'email': 'new_o_a@gmail.com',
                                      'role': 'OWNER',
                                      'is_system_admin': True,
                                      'is_owner': True})
    assert response.status_code == 400, response.get_json()
    assert response.get_json()[
               'message'] == "Wrong value for is_owner and is_system_admin. You can only set one of them"


def test_system_admin_creation_with_email_that_already_exist(test_client, init_db, init_db_organization):
    response = test_client.post('/admins',
                                json={'name': 'New System',
                                      'surname': 'Admin',
                                      'email': 's_a@gmail.com',
                                      'role': 'SYSTEM',
                                      'is_system_admin': True})
    assert response.status_code == 400, response.get_json()
    assert response.get_json()['message'] == "Admin email already exist"


def test_admin_creation_with_wrong_value_for_system_and_admin(test_client, init_db, init_db_organization):
    response = test_client.post('/admins',
                                json={'name': 'New Owner',
                                      'surname': 'Admin',
                                      'email': 'new_o_a@gmail.com',
                                      'role': 'OWNER',
                                      'is_system_admin': 1,
                                      'is_owner': 'pere'})
    assert response.status_code == 400, response.get_json()
    assert response.get_json()[
               'message'] == "Wrong value for is_owner and is_system_admin. You can only set one of them"


def test_admin_try_to_act_on_an_organization_in_which_cant_act(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 'o_a@gmail.com')
    response = test_client.post('/organizations/1/admins',
                                json=get_payload_admin_manager(),
                                headers={'Authorization': admin_token})
    assert response.status_code == 200, response.get_json()

    new_admin_token = get_admin_token_for_organization(test_client,
                                                       get_admin_token(test_client,
                                                                       get_payload_admin_manager()['email']),
                                                       1)

    unauthorized_response = test_client.get('/organizations/2',
                                            headers={'Authorization': new_admin_token})

    assert unauthorized_response.status_code == 403, unauthorized_response.status_code
    assert unauthorized_response.get_json()['message'] == "Admin not allowed to act on this organization"


def test_admin_try_to_select_an_organization_on_which_it_cant_act(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 'o_a@gmail.com')
    response = test_client.post('/organizations/1/admins',
                                json=get_payload_admin_manager(),
                                headers={'Authorization': admin_token})
    assert response.status_code == 200, response.get_json()

    old_admin_token = get_admin_token(test_client, get_payload_admin_manager()['email'])

    unauthorized_response = test_client.get('/select-organization?organization_id=2',
                                            headers={'Authorization': old_admin_token})

    assert unauthorized_response.status_code == 403, unauthorized_response.status_code


def test_admin_try_to_select_organization_without_passing_organization_id(test_client, init_db, init_db_organization):
    response = test_client.get('/select-organization',
                               headers={'Authorization': get_admin_token(test_client,
                                                                         'o_a@gmail.com')})
    assert response.status_code == 400


def test_admin_edit_role_in_watcher_for_an_organization(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 'o_a@gmail.com')

    response = test_client.post('/organizations/1/admins',
                                json=get_payload_admin_manager(),
                                headers={'Authorization': admin_token})
    assert response.status_code == 200, response.get_json()

    change_response = test_client.put('/organizations/1/admins/3',
                                      json={'role': 'WATCHER'},
                                      headers={'Authorization': admin_token})

    assert change_response.status_code == 200

    check_response = test_client.get('/organizations/1/admins',
                                     headers={'Authorization': admin_token})

    assert check_response.status_code == 200, check_response.get_json()
    assert check_response.get_json()['admins'][1]['role'] == 'watcher'


def test_admin_edit_role_in_manager_for_an_organization(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 'o_a@gmail.com')

    payload = get_payload_admin_manager()
    payload['role'] = 'WATCHER'

    response = test_client.post('/organizations/1/admins',
                                json=payload,
                                headers={'Authorization': admin_token})
    assert response.status_code == 200, response.get_json()

    change_response = test_client.put('/organizations/1/admins/3',
                                      json={'role': 'MANAGER'},
                                      headers={'Authorization': admin_token})

    assert change_response.status_code == 200

    check_response = test_client.get('/organizations/1/admins',
                                     headers={'Authorization': admin_token})

    assert check_response.status_code == 200, check_response.get_json()
    assert check_response.get_json()['admins'][1]['role'] == 'manager'


def test_admin_get_his_own_info(test_client, init_db, init_db_organization):
    response = test_client.get('/admins/1',
                               headers={'Authorization': get_admin_token(test_client, 's_a@gmail.com')})

    assert response.status_code == 200, response.get_json()

    admin = response.get_json()
    assert admin['email'] == 's_a@gmail.com'


def test_admin_edit_his_info(test_client, init_db, init_db_organization):
    edited_admin = get_payload_admin_manager()
    response = test_client.put('/admins/1',
                               json=edited_admin,
                               headers={'Authorization': get_admin_token(test_client, 's_a@gmail.com')})

    assert response.status_code == 200
    admin = response.get_json()
    assert admin['email'] == edited_admin['email']
    assert admin['name'] == edited_admin['name']
    assert admin['surname'] == edited_admin['surname']


def test_admin_edit_password(test_client, init_db, init_db_organization):
    edited_admin = get_payload_admin_manager()
    edited_admin['password'] = 'new_password'
    response = test_client.put('/admins/1',
                               json=edited_admin,
                               headers={'Authorization': get_admin_token(test_client, 's_a@gmail.com')})

    assert response.status_code == 200


def test_admin_try_to_get_other_admin_info(test_client, init_db, init_db_organization):
    response = test_client.get('/admins/2',
                               headers={'Authorization': get_admin_token(test_client, 's_a@gmail.com')})

    assert response.status_code == 403


def test_admin_try_to_edit_other_admin_info(test_client, init_db, init_db_organization):
    edited_admin = get_payload_admin_manager()
    response = test_client.put('/admins/2',
                               json=edited_admin,
                               headers={'Authorization': get_admin_token(test_client, 's_a@gmail.com')})

    assert response.status_code == 403


def test_to_dict_functions_that_when_organization_id_is_none_and_is_not_system_or_owner_so_role_is_none(test_client,
                                                                                                        init_db,
                                                                                                        init_db_organization):
    admin_token = get_admin_token(test_client, 'o_a@gmail.com')
    response = test_client.post('/organizations/1/admins',
                                json=get_payload_admin_manager(),
                                headers={'Authorization': admin_token})
    assert response.status_code == 200, response.get_json()

    assert Admin.query.get(response.get_json()['id']).to_dict(None)['role'] == 'other'


def test_admin_owner_delete_an_admin_from_his_organization(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 'o_a@gmail.com')
    response = test_client.post('/organizations/1/admins',
                                json=get_payload_admin_manager(),
                                headers={'Authorization': admin_token})
    assert response.status_code == 200, response.get_json()
    response_delete = test_client.delete('/organizations/1/admins/3',
                                         headers={'Authorization': admin_token})

    assert response_delete.status_code == 200, response_delete.get_data()
