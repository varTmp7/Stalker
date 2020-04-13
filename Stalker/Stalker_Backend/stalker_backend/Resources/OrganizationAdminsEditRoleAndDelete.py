from flask_jwt_extended import jwt_required
from flask_restful import Resource
from flask import Response

from stalker_backend.Models.OrganizationsAdmins import OrganizationsAdmins, db
from stalker_backend.Resources.ResourceClass.OrganizationResource import OrganizationResource
from stalker_backend.Utils.AuthUtils import owner_admin_required
from stalker_backend.Parser.AdminParser import edit_role_parser


class OrganizationAdminsEditRoleAndDelete(Resource):
    _organization_resource: OrganizationResource = None

    def __init__(self, organization_resource):
        self._organization_resource = organization_resource

    @jwt_required
    @owner_admin_required
    def put(self, organization_id, admin_id):
        new_role = edit_role_parser.parse_args()['role']

        organization = self._organization_resource.get_organization(organization_id)[0]

        rel_organization_admin = OrganizationsAdmins.query. \
            filter(OrganizationsAdmins.admin_id == admin_id,
                   OrganizationsAdmins.organization_id == organization.id).first()

        rel_organization_admin.edit_role(new_role)

        return Response(status=200)

    @jwt_required
    @owner_admin_required
    def delete(self, organization_id, admin_id):
        relationship = OrganizationsAdmins.query.filter_by(admin_id=admin_id, organization_id=organization_id).first()

        db.session.delete(relationship)
        db.session.commit()

        return Response(status=200)
