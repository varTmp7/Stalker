from flask_restful import reqparse
from datetime import datetime

track_parser = reqparse.RequestParser()

track_parser.add_argument('entered', type=bool, required=True)
track_parser.add_argument('authenticated', type=bool, required=True)
track_parser.add_argument('date_time', type=lambda x: datetime.strptime(x, '%Y-%m-%dT%H:%M:%S'), required=True)
track_parser.add_argument('auth_type', choices=('ldapv3', ))
track_parser.add_argument('username', required=False)
track_parser.add_argument('password', required=False)
track_parser.add_argument('ldap_url', required=False)
track_parser.add_argument('ldap_port', required=False)
track_parser.add_argument('ldap_common_name', required=False)
track_parser.add_argument('ldap_domain_name', required=False)
