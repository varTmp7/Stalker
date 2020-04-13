from ldap3 import Server, Connection
from .UserAuthMethods import UserAuthMethod
import json


class LDAPUserAuth(UserAuthMethod):
    _url: str = None
    _port: str = None
    _common_name: str = None
    _domain_component: str = None

    _server: Server = None

    def __init__(self, url, port, common_name, domain_component):
        self._url = url
        self._port = port
        self._common_name = common_name
        self._domain_component = domain_component
        self._server = Server(f'{url}:{port}')

    def login(self, user, password) -> dict:
        with Connection(self._server,
                        f'cn={user},{self._common_name},{self._domain_component}',
                        password, auto_bind=True) as connection:
            connection.search(f'cn={user},{self._common_name},{self._domain_component}',
                              '(objectclass=top)',
                              attributes={'givenName', 'sn', 'uid', 'uidNumber'})
            attribute = json.loads(connection.entries[0].entry_to_json()).get('attributes')
        return {
            'name': attribute['givenName'][0] if attribute['givenName'] else None,
            'surname': attribute['sn'][0] if attribute['sn'] else None,
            'username': attribute['uid'][0] if attribute['uid'] else None,
            'uid_number': attribute['uidNumber'][0] if attribute['uidNumber'] else None
        }
