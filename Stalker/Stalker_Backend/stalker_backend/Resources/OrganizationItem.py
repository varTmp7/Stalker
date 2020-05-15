from flask import jsonify, Response
from flask_restful import Resource

from stalker_backend.Resources.ResourceClass.OrganizationResource import OrganizationResource

from ..Parser.OrganizationParser import organization_parser
from ..Models import Organization, Admin

from flask_jwt_extended import jwt_required
from stalker_backend.Utils.AuthUtils import watcher_admin_required, manager_admin_required, owner_admin_required


class OrganizationItem(Resource):
    _organization_resource: OrganizationResource = None

    def __init__(self, organization_resource):
        self._organization_resource = organization_resource

    @jwt_required
    @watcher_admin_required
    def get(self, organization_id):
        organization = self._organization_resource.get_organization(organization_id)[0]

        organization_dict = organization.to_dict()
        organization_dict['token'] = organization.token
        response = jsonify(organization_dict)

        response.status_code = 200
        response.headers['req_code'] = 2
        return response

    @jwt_required
    @manager_admin_required
    def put(self, organization_id):
        organization_edited = organization_parser.parse_args()
        organization, admin_representation, content_provider = self._organization_resource.get_organization(
            organization_id)

        content_provider.changed_organization_name(organization.name, organization_edited['name'])

        organization.edit(organization_edited)
        Organization.db.session.commit()

        # return the modified organization
        modified_organization = Organization.Organization.query.get(organization.id).to_dict()
        response = jsonify(modified_organization)
        response.status_code = 200
        response.headers['req_code'] = 3
        return response

    @jwt_required
    @owner_admin_required
    def delete(self, organization_id):
        organization, admin_representation, content_provider = self._organization_resource.get_organization(
            organization_id)

        Organization.db.session.delete(organization)
        Organization.db.session.commit()

        response = Response()
        response.status_code = 200
        response.headers['req_code'] = 4

        # Update admin max quota
        admin = Admin.Admin.query.get(admin_representation['id'])
        admin.max_quota_organizations += 1
        Admin.db.session.commit()
        return response

