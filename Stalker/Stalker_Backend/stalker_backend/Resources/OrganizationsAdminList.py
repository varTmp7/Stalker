from flask import jsonify
from flask_jwt_extended import jwt_required, get_jwt_identity
from flask_restful import Resource

from stalker_backend.Utils.AuthUtils import watcher_admin_required
from stalker_backend.Models.Admin import Admin


class OrganizationsAdminList(Resource):

    @jwt_required
    def get(self):
        admin_representation = get_jwt_identity()

        return jsonify(organizations=[organization.to_dict() for organization in
                                      Admin.query.get(admin_representation['id']).organizations])
