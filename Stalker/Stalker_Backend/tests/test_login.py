from .config_test import login, init_db, test_client


def test_login_as_system_administrator(test_client, init_db):
    response = login(test_client, 's_a@gmail.com', 'password')

    assert response.status_code == 200

    token = response.get_json()
    print(token)
    assert token['access_token'] is not None, token


def test_login_as_owner_admin(test_client, init_db):
    response = login(test_client, 'o_a@gmail.com', 'password')

    assert response.status_code == 200

    token = response.get_json()
    print(token)
    assert token['access_token'] is not None, token


def test_login_with_wrong_credentials(test_client, init_db):
    response = login(test_client, 'not_email@gmail.com', 'password')

    assert response.status_code == 404
