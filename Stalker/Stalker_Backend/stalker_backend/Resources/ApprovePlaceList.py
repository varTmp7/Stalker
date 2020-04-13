from flask_jwt_extended import jwt_required
from flask_restful import Resource

from stalker_backend.ContentProvider.OrganizationContentProvider import OrganizationContentProvider
from stalker_backend.Models.Place import Place
from stalker_backend.Utils.AuthUtils import system_admin_required
from stalker_backend.Models.Organization import Organization


class ApprovePlaceList(Resource):

    @jwt_required
    @system_admin_required
    def get(self):
        organizations = [organization for organization in Organization.query.all()]
        response = {
            'places': []
        }
        for organization in organizations:
            content_provider = OrganizationContentProvider(organization.name)
            places_to_approve = content_provider.session.query(Place).filter(Place.approved == False).all()
            for place in places_to_approve:
                response['places'].append(place.to_dict())

        return response
