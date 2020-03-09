from flask import jsonify, Response
from flask_restful import Resource

from ..Parser.OrganizationParser import organization_parser
from ..Models import Organization
from ..ContentProvider import OrganizationContentProvider


class OrganizationItem(Resource):

    @staticmethod
    def get(organization_id):
        organization = Organization.Organization.query.get(organization_id)
        if not organization:
            response = jsonify({'error': 'organizzazione non trovata'})
            response.status_code = 404
            response.headers['req_code'] = 2
            return response

        response = jsonify(organization.to_dict())
        response.status_code = 200
        response.headers['req_code'] = 2
        return response

    @staticmethod
    def put(organization_id):
        args = organization_parser.parse_args()
        organization = Organization.Organization.query.get(organization_id)
        if not organization:
            response = jsonify({'error': 'organizzazione non trovata'})
            response.status_code = 404
            response.headers['req_code'] = 3
            return response

        OrganizationContentProvider.OrganizationContentProvider(organization.name).changed_organization_name(organization.name, args['name'])

        organization.edit(args['name'],
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
                          args['ldap_common_name'],
                          )
        Organization.db.session.commit()

        # return the modified organization
        modified_organization = Organization.Organization.query.get(organization.id).to_dict()
        response = jsonify(modified_organization)
        response.status_code = 200
        response.headers['req_code'] = 3
        return response

    @staticmethod
    def delete(organization_id):
        organization = Organization.Organization.query.get(organization_id)
        if not organization:
            response = Response()
            response.status_code = 404
            return response

        Organization.db.session.delete(organization)
        Organization.db.session.commit()

        deleted_place = Organization.Organization.query.get(organization.id)
        if not deleted_place:
            response = Response()
            response.status_code = 200
            response.headers['req_code'] = 4
            return response
        else:
            response = Response()
            response.status_code = 400
            response.headers['req_code'] = 4
            return response
