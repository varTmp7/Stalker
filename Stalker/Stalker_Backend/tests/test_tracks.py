from .config_test import test_client, init_db, init_db_organization, get_payload_track_model_entered, get_admin_token, \
    get_organization_token

from datetime import datetime


def test_get_tracks(test_client, init_db, init_db_organization):
    response = test_client.get("/organizations/1/places/1/tracks",
                               headers={'Authorization': get_admin_token(test_client, 'o_a@gmail.com')})
    assert response.status_code == 200, response.get_json()
    assert response.headers['req_code'] == '10'
    tracks = response.get_json()['tracks']
    assert len(tracks) == 2

    assert tracks[0]['id'] == 1
    assert tracks[0]['entered']
    assert tracks[1]['id'] == 2
    assert not tracks[1]['entered']


def test_add_tracks(test_client, init_db, init_db_organization):
    response = test_client.post("/organizations/1/places/1/tracks",
                                json=get_payload_track_model_entered(),
                                headers={'Organization-Token': get_organization_token()})
    assert response.status_code == 200, response.get_json()
    assert response.headers['req_code'] == '11'

    response_check = test_client.get("/organizations/1/places/1/tracks",
                                     headers={'Authorization': get_admin_token(test_client, 'o_a@gmail.com')})
    assert response_check.status_code == 200, response_check.get_json()
    assert response_check.headers['req_code'] == '10'
    tracks = response_check.get_json()['tracks']
    assert len(tracks) == 3, tracks
    assert tracks[2]['id'] == 3
    assert tracks[2]['entered']

    response_count_check = test_client.get("/organizations/1/places/1",
                                           headers={'Organization-Token': get_organization_token()})
    assert response_count_check.status_code == 200, response_count_check.get_json()
    place_info = response_count_check.get_json()
    assert place_info['number_of_people'] == 1


def test_add_track_with_missing_username_and_password(test_client, init_db, init_db_organization):
    wrong_payload = get_payload_track_model_entered()
    del wrong_payload['username']
    response = test_client.post("/organizations/1/places/1/tracks",
                                json=wrong_payload,
                                headers={'Organization-Token': get_organization_token()})

    assert response.status_code == 400, response.get_json()
    assert response.get_json()['message'] == 'Missing username or password in request'


def test_add_track_with_wrong_username_and_password(test_client, init_db, init_db_organization):
    wrong_payload = get_payload_track_model_entered()
    wrong_payload['username'] = 'wrong_user'
    response = test_client.post("/organizations/1/places/1/tracks",
                                json=wrong_payload,
                                headers={'Organization-Token': get_organization_token()})

    assert response.status_code == 403, response.get_json()
    assert response.get_json()['message'] == 'Impossible to add new track, auth_type, user and password not recognized'


def test_add_track_with_wrong_auht_type(test_client, init_db, init_db_organization):
    wrong_payload = get_payload_track_model_entered()
    wrong_payload['auth_type'] = 'wrong_authtype'
    response = test_client.post("/organizations/1/places/1/tracks",
                                json=wrong_payload,
                                headers={'Organization-Token': get_organization_token()})

    assert response.status_code == 400, response.get_json()
    assert response.get_json()['message']['auth_type'] == 'wrong_authtype is not a valid choice'


def test_add_track_not_authenticated(test_client, init_db, init_db_organization):
    response = test_client.post("/organizations/1/places/1/tracks",
                                json={'entered': True,
                                      'authenticated': False,
                                      'date_time': datetime.now().strftime("%Y-%m-%dT%H:%M:%S")},
                                headers={'Organization-Token': get_organization_token()})
    assert response.status_code == 200, response.get_json()
    assert response.headers['req_code'] == '11'


def test_get_all_tracks_of_an_organization(test_client, init_db, init_db_organization):
    response = test_client.get("/organizations/1/tracks",
                               headers={'Authorization': get_admin_token(test_client, 'o_a@gmail.com')})

    assert response.status_code == 200, response.get_json()
