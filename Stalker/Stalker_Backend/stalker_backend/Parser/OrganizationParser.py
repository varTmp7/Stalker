from flask_restful import reqparse

organization_parser = reqparse.RequestParser()

organization_parser.add_argument('name', required=True, trim=True)
organization_parser.add_argument('address', required=True, trim=True)
organization_parser.add_argument('city', required=True, trim=True)
organization_parser.add_argument('region', required=True, trim=True)
organization_parser.add_argument('postal_code', required=True, trim=True)
organization_parser.add_argument('nation', required=True, trim=True)
organization_parser.add_argument('phone_number', required=True, trim=True)
organization_parser.add_argument('email', required=True, trim=True)
organization_parser.add_argument('type', required=True, choices=('PUBLIC', 'PRIVATE', 'BOTH'))
organization_parser.add_argument('ldap_url', required=False, trim=True, nullable=True)
organization_parser.add_argument('ldap_port', required=False, type=int, nullable=True)
organization_parser.add_argument('ldap_domain_component', required=False, trim=True, nullable=True)
organization_parser.add_argument('ldap_common_name', required=False, trim=True, nullable=True )
