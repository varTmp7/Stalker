from .config_test import test_client


def test_index_route(test_client):
    response = test_client.get('/')
    assert response.status_code == 200
    assert response.get_data() == b'Hi, this is stalker-backend'
