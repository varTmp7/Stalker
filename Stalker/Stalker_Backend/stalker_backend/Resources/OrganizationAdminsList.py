import hashlib

from flask_jwt_extended import jwt_required
from flask_restful import Resource

from stalker_backend.Resources.ResourceClass.OrganizationResource import OrganizationResource
from flask import abort, jsonify

from ..Parser.AdminParser import admin_parser

from ..Models import Admin, OrganizationsAdmins
from ..Models.Organization import Organization
from stalker_backend.utils import send_email_to_new_admins
from stalker_backend.Utils.AuthUtils import generate_password
from stalker_backend.Utils.AuthUtils import owner_admin_required

class OrganizationAdminList(Resource):
    _organization_resource: OrganizationResource = None

    def __init__(self, organization_resource: OrganizationResource):
        self._organization_resource = organization_resource

    @jwt_required
    @owner_admin_required
    def get(self, organization_id):
        organization, admin_representation, content_provider = self._organization_resource.get_organization(
            organization_id)
        response = jsonify({'admins': [admin.to_dict(organization_id) for admin in organization.admins]})
        response.headers['req_code'] = 12
        response.status_code = 200
        return response

    @staticmethod
    @jwt_required
    @owner_admin_required
    def post(organization_id):
        admin = admin_parser.parse_args()
        if admin['organization_id'] is None:
            abort(400, description="Missing organization_id")
        if admin['email'] not in [existed_admins.email for existed_admins in Admin.Admin.query.all()]:
            # Admin non ancora esistente => va creato
            generated_password = generate_password()
            hashed_password = hashlib.sha512(generated_password.encode('utf-8')).hexdigest()
            admin['password'] = hashed_password
            new_admin = Admin.Admin(admin, False, False)
            Admin.db.session.add(new_admin)
            Admin.db.session.commit()
            send_email_to_new_admins(admin['email'], generated_password, admin['name'], admin['surname'])
        # Creo la relazione con l'organizzazione indicata
        if admin['email'] in [already_connected_admin.email for already_connected_admin in
                              Organization.query.get(organization_id).admins]:
            abort(409, description="Admin already connected to this organization")

        admin_in_db = Admin.Admin.query.filter(Admin.Admin.email == admin['email']).first()
        new_relationship = OrganizationsAdmins.OrganizationsAdmins(organization_id, admin_in_db.id, admin['role'])
        OrganizationsAdmins.db.session.add(new_relationship)
        OrganizationsAdmins.db.session.commit()

        response = jsonify(admin_in_db.to_dict(organization_id))
        response.status_code = 200
        response.headers['req_code'] = 13
        return response
