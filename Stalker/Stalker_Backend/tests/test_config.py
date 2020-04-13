import pytest
from stalker_backend.config import get_db_url
from stalker_backend import create_app


def test_postgres_configuration(monkeypatch):
    monkeypatch.setenv('DATABASE_TYPE', 'postgres://')
    monkeypatch.setenv('BASE_POSTGRES_URL', 'localhost:5432')
    monkeypatch.setenv('POSTGRES_USER', 'postgres')
    monkeypatch.setenv('POSTGRES_PASSWORD', 'password')

    assert get_db_url('test') == 'postgres://postgres:password@localhost:5432/test'


def test_missing_postgres_info(monkeypatch):
    monkeypatch.setenv('DATABASE_TYPE', 'postgres://')
    monkeypatch.setenv('POSTGRES_USER', 'postgres')
    monkeypatch.setenv('POSTGRES_PASSWORD', 'password')

    with pytest.raises(SystemExit) as pytest_wrapped_e:
        get_db_url('test')
    assert pytest_wrapped_e.type == SystemExit
    assert pytest_wrapped_e.value.code == 3


def test_wrong_database_type(monkeypatch):
    monkeypatch.setenv('DATABASE_TYPE', 'wrong_db_type')

    with pytest.raises(SystemExit) as pytest_wrapped_e:
        get_db_url('test')
    assert pytest_wrapped_e.type == SystemExit
    assert pytest_wrapped_e.value.code == 2


def test_missing_database_type(monkeypatch):
    monkeypatch.delenv('DATABASE_TYPE', 'postgres://')

    with pytest.raises(SystemExit) as pytest_wrapped_e:
        get_db_url('test')
    assert pytest_wrapped_e.type == SystemExit
    assert pytest_wrapped_e.value.code == 1


def test_wrong_rethink_url(monkeypatch):
    monkeypatch.setenv('RETHINK_URL', 'wrong_url')

    with pytest.raises(SystemExit) as pytest_wrapped_e:
        create_app()
    assert pytest_wrapped_e.type == SystemExit
    assert pytest_wrapped_e.value.code == 4
