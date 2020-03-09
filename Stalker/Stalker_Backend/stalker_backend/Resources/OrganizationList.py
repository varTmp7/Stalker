from flask import jsonify
from flask_restful import Resource

from ..Parser.OrganizationParser import organization_parser
from ..Models import Organization


class OrganizationList(Resource):
    @staticmethod
    def get():
        organizations = []
        for organization in Organization.Organization.query.all():
            organizations.append(organization.to_dict())
        response = jsonify({'organizations': organizations})
        response.status_code = 200
        response.headers['req_code'] = 0
        return response

    @staticmethod
    def post():
        args = organization_parser.parse_args()

        new_organization = Organization.Organization(args['name'],
                                                     args['address'],
                                                     args['city'],
                                                     args['region'],
                                                     args['postal_code'],
                                                     args['nation'],
                                                     args['phone_number'],
                                                     args['email'],
                                                     args['type'],
                                                     args['ldap_url'],
                                                     args['ldap_port'],
                                                     args['ldap_domain_component'],
                                                     args['ldap_common_name'], )
        Organization.db.session.add(new_organization)
        Organization.db.session.commit()

        # Return organization created
        created_organization = Organization.Organization.query.get(new_organization.id).to_dict()
        response = jsonify({'organization': created_organization})
        response.status_code = 200
        response.headers['req_code'] = 1
        return response
