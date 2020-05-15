from flask_restful import reqparse

auth_parser = reqparse.RequestParser()

auth_parser.add_argument('auth_type', choices=('ldapv3', ))
auth_parser.add_argument('username', required=True)
auth_parser.add_argument('password', required=True)
