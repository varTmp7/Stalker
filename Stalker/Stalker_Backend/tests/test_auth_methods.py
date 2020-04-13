import pytest

from stalker_backend.Utils.UserAuth import UserAuth


def test_user_auth_with_wrong_auth_type():
    with pytest.raises(NotImplementedError) as pytest_wrapped_e:
        UserAuth('wrong_auth_type').get_user_auth()

    assert pytest_wrapped_e.type == NotImplementedError


def test_ldap_user_auth_with_wrong_parameter():
    with pytest.raises(TypeError) as pytest_wrapped_e:
        UserAuth('ldapv3').get_user_auth(url='only_url')

    assert pytest_wrapped_e.type == TypeError
