from stalker_backend.Utils.UserAuth.UserAuthMethods.LDAPUserAuth import LDAPUserAuth
from stalker_backend.Utils.UserAuth.UserAuthMethods.UserAuthMethods import UserAuthMethod


class UserAuth:
    _type: str = None

    def __init__(self, auth_type):
        self._type = auth_type

    def get_user_auth(self, **kwargs) -> UserAuthMethod:
        if self._type == 'ldapv3':
            if not kwargs.get('url') or not kwargs.get('port') or not kwargs.get('cn') or not kwargs.get('dn'):
                raise TypeError('Missing parameter to connect to ldap server')

            return LDAPUserAuth(kwargs.get('url'), kwargs.get('port'), kwargs.get('cn'), kwargs.get('dn'))
        else:
            raise NotImplementedError("Auth methods not implemented yet")
