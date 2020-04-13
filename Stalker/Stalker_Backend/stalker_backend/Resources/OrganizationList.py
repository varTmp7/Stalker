from flask import jsonify
from flask_restful import Resource

from .ResourceClass.PlaceResource import PlaceResource
from ..ContentProvider.OrganizationContentProvider import OrganizationContentProvider
from ..Models.Place import Place
from ..Parser.OrganizationParser import organization_parser
from ..Models import Organization, OrganizationsAdmins

from flask_jwt_extended import jwt_required, get_jwt_identity
from stalker_backend.Utils.AuthUtils import owner_admin_required, organization_token_required


class OrganizationList(Resource):

    @organization_token_required
    def get(self):
        organizations = []
        for array_id, organization in enumerate(Organization.Organization.query.all()):
            organizations.append(organization.to_dict())
            content_provider = OrganizationContentProvider(organization.name)
            organizations[array_id]['places'] = [place.to_dict() for place in content_provider.session.query(Place).all()]
        response = jsonify({'organizations': organizations})
        response.status_code = 200
        response.headers['req_code'] = 0
        return response

    @jwt_required
    @owner_admin_required
    def post(self):
        organization = organization_parser.parse_args()
        admin_representation = get_jwt_identity()
        new_organization = Organization.Organization(organization)
        Organization.db.session.add(new_organization)
        Organization.db.session.commit()

        new_connection_between_org_and_admin = OrganizationsAdmins.OrganizationsAdmins(new_organization.id,
                                                                                       admin_representation['id'],
                                                                                       OrganizationsAdmins.Role.OWNER)
        OrganizationsAdmins.db.session.add(new_connection_between_org_and_admin)
        OrganizationsAdmins.db.session.commit()
        # Return organization created
        created_organization = Organization.Organization.query.get(new_organization.id).to_dict()
        response = jsonify({'organization': created_organization})
        response.status_code = 200
        response.headers['req_code'] = 1
        return response
