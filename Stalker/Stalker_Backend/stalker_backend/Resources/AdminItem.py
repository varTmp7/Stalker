from flask import jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from flask_restful import Resource, abort
import hashlib

from stalker_backend.Models.Admin import Admin
from stalker_backend.Parser.AdminParser import admin_parser


class AdminItem(Resource):

    def check_admin_identity(self, admin_id):
        return str(get_jwt_identity()['id']) == admin_id

    @jwt_required
    def get(self, admin_id):
        if self.check_admin_identity(admin_id):
            return jsonify(Admin.query.get(admin_id).to_dict(None))
        else:
            abort(403)

    @jwt_required
    def put(self, admin_id):
        if self.check_admin_identity(admin_id):
            edited_admin = admin_parser.parse_args()
            if edited_admin['password']:
                hashed_password = hashlib.sha512(edited_admin['password'].encode('utf-8')).hexdigest()
                edited_admin['password'] = hashed_password
            Admin.query.get(admin_id).edit(edited_admin)

            return jsonify(Admin.query.get(admin_id).to_dict(None))
        else:
            abort(403)
