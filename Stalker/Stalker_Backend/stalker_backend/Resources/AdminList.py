from flask import abort, jsonify
from flask_jwt_extended import jwt_required
from flask_restful import Resource
from ..Parser.AdminParser import admin_parser
from stalker_backend.utils import send_email_to_new_admins
from stalker_backend.Utils.AuthUtils import generate_password, system_admin_required
import hashlib
from ..Models import Admin


class AdminList(Resource):
    @jwt_required
    @system_admin_required
    def get(self):
        return jsonify(admins=[admin.to_dict(None) for admin in Admin.Admin.query.filter_by(is_system_admin=False).all()])

    @staticmethod
    def post():
        # TODO: al momento viene lasciato privo di autenticazione per motivi di test
        # Questo metodo serve solo a creare admin owner e di sistema
        admin = admin_parser.parse_args()
        if admin['is_system_admin'] is None and admin['is_owner'] is None:
            abort(400, description="Missing is_owner or is_system_admin")

        if admin['is_system_admin'] == admin['is_owner']:
            abort(400, description="Wrong value for is_owner and is_system_admin. You can only set one of them")

        if admin['email'] not in [existed_admins.email for existed_admins in Admin.Admin.query.all()]:
            # Admin non ancora esistente => va creato
            new_admin = None
            generated_password = generate_password()
            hashed_password = hashlib.sha512(generated_password.encode('utf-8')).hexdigest()
            admin['password'] = hashed_password
            if admin['is_system_admin']:
                new_admin = Admin.Admin(admin,
                                        is_owner=False,
                                        is_system_admin=True)
            elif admin['is_owner']:
                new_admin = Admin.Admin(admin,
                                        is_owner=True,
                                        is_system_admin=False)

            Admin.db.session.add(new_admin)
            Admin.db.session.commit()
            send_email_to_new_admins(admin['email'], generated_password, admin['name'], admin['surname'])

            return 200
        else:
            abort(400, description='Admin email already exist')
