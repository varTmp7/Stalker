from .config_test import test_client, init_db, init_db_organization, get_admin_token, get_organization_token


def test_approve_ok(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 's_a@gmail.com')
    response = test_client.post('/organizations/1/places/1/approve',
                                json=({'approved': True}),
                                headers={'Authorization': admin_token})
    assert response.status_code == 200, response.get_json()

    response_place_approved = test_client.get('/organizations/1/places/1',
                                              headers={'Authorization': admin_token,
                                                       'Organization-Token': get_organization_token()})
    assert response_place_approved.status_code == 200, response_place_approved.get_json()
    assert response_place_approved.get_json()['approved']


def test_approve_with_wrong_json(test_client, init_db):
    admin_token = get_admin_token(test_client, 's_a@gmail.com')
    response = test_client.post('/organizations/1/places/1/approve',
                                json=({'approved': 1}),
                                headers={'Authorization': admin_token})
    print(response.get_data())
    assert response.status_code == 400, response.get_json()


def test_approve_with_not_system_admin(test_client, init_db):
    owner_token = get_admin_token(test_client, 'o_a@gmail.com')
    response = test_client.post('/organizations/1/places/1/approve',
                                json=({'approved': True}),
                                headers={'Authorization': owner_token})
    assert response.status_code == 403, response.get_json()


def test_get_place_to_approve(test_client, init_db, init_db_organization):
    admin_token = get_admin_token(test_client, 's_a@gmail.com')
    response = test_client.get('/place-to-approve',
                               headers={'Authorization': admin_token})
    assert response.status_code == 200, response.get_json()

    print(response.get_json())
    places = []
    for org in response.get_json()['organizations']:
        places += org['places']
    assert len(places) == 3
