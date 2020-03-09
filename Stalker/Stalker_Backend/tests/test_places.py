import pytest

from .config_test import test_client, init_db, payload_place_model, init_db_organization


def test_get_places(test_client, init_db, init_db_organization):
    response = test_client.get('/organizations/1/places')
    assert response.status_code == 200
    assert response.headers['req_code'] == '5'
    places = response.get_json()['places']
    assert len(places) == 3

    assert places[0]['id'] == 1
    assert places[0]['name'] == "Luogo1"
    assert len(places[0]['coordinates']) == 4

    assert places[1]['id'] == 2
    assert places[1]['name'] == "Luogo2"
    assert len(places[1]['coordinates']) == 4

    assert places[2]['id'] == 3
    assert places[2]['name'] == "Luogo3"
    assert len(places[2]['coordinates']) == 4


def test_create_new_place(test_client, init_db, init_db_organization):
    response = test_client.post('/organizations/1/places', json=payload_place_model)
    assert response.status_code == 200
    assert response.headers['req_code'] == '6'
    place = response.get_json()['place']

    assert place['id'] == 4
    assert place['approved'] == False
    assert place['name'] == "nuovo luogo"
    assert len(place['coordinates']) == 4
    assert place['coordinates'][0]['latitude'] == 1.1
    assert place['coordinates'][0]['longitude'] == 1.2
    assert place['coordinates'][1]['latitude'] == 2.1
    assert place['coordinates'][1]['longitude'] == 2.2
    assert place['coordinates'][2]['latitude'] == 3.1
    assert place['coordinates'][2]['longitude'] == 3.2
    assert place['coordinates'][3]['latitude'] == 4.1
    assert place['coordinates'][3]['longitude'] == 4.2
    assert place['num_max_people'] == 100

    check_p_res = test_client.get("/organizations/1/places")
    check_ps = check_p_res.get_json()
    assert len(check_ps['places']) == 4


def test_edit_place(test_client, init_db, init_db_organization):
    modified_payload = payload_place_model
    modified_payload['name'] = "nome luogo modificato"
    response = test_client.put('/organizations/1/places/1', json=modified_payload)
    assert response.status_code == 200
    assert response.headers['req_code'] == '8'
    modified_place = response.get_json()
    assert modified_place['name'] == "nome luogo modificato"
    assert modified_place['approved'] == False


def test_delete_place(test_client, init_db, init_db_organization):
    response = test_client.delete('/organizations/1/places/1')
    assert response.status_code == 200
