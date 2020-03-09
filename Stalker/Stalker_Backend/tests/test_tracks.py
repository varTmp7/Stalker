from .config_test import test_client, init_db, init_db_organization, payload_track_model_entered


def test_get_tracks(test_client, init_db, init_db_organization):
    response = test_client.get("/organizations/1/places/1/tracks")
    assert response.status_code == 200
    assert response.headers['req_code'] == '10'
    tracks = response.get_json()['tracks']
    assert len(tracks) == 2

    assert tracks[0]['id'] == 1
    assert tracks[0]['entered'] == True
    assert tracks[1]['id'] == 2
    assert tracks[1]['entered'] == False


def test_add_tracks(test_client, init_db, init_db_organization):
    response = test_client.post("/organizations/1/places/1/tracks", json=payload_track_model_entered)
    assert response.status_code == 200
    assert response.headers['req_code'] == '11'

    response_check = test_client.get("/organizations/1/places/1/tracks")
    assert response_check.status_code == 200
    assert response_check.headers['req_code'] == '10'
    tracks = response_check.get_json()['tracks']
    assert len(tracks) == 3
    assert tracks[2]['id'] == 3
    assert tracks[2]['entered'] == True

    response_count_check = test_client.get("/organizations/1/places/1")
    assert response_count_check.status_code == 200
    place_info = response_count_check.get_json()
    assert place_info['number_of_people'] == 1
