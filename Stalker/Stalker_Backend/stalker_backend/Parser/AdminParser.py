from flask_restful import reqparse

admin_parser = reqparse.RequestParser()
admin_parser.add_argument('email', required=True)
admin_parser.add_argument('name', required=True)
admin_parser.add_argument('surname', required=True)
admin_parser.add_argument('role', required=True, choices=('SYSTEM', 'OWNER', 'MANAGER', 'WATCHER'))
admin_parser.add_argument('organization_id', required=False, type=int)
admin_parser.add_argument('is_system_admin', required=False, type=bool)
admin_parser.add_argument('is_owner', required=False, type=bool)
admin_parser.add_argument('password', required=False)

edit_role_parser = reqparse.RequestParser()
edit_role_parser.add_argument('role', required=True, choices=('MANAGER', 'WATCHER'))
