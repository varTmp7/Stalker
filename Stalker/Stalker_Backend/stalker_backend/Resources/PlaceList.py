from flask import jsonify, abort
from flask_jwt_extended import jwt_required
from flask_restful import Resource

from stalker_backend.Resources.ResourceClass.OrganizationResource import OrganizationResource

from ..Models.Place import Place
from stalker_backend.ContentProvider.OrganizationContentProvider import OrganizationContentProvider
from ..Parser.PlaceParser import place_parser
from stalker_backend.Utils.AuthUtils import organization_token_required, manager_admin_required
from stalker_backend.Utils.NotificationManager import NotificationManger

class PlaceList(Resource):
    _organization_resource: OrganizationResource = None

    def __init__(self, organization_resource):
        self._organization_resource = organization_resource

    @organization_token_required
    def get(self, organization_id):
        organization = self._organization_resource.get_organizations_unsafe(organization_id)

        content_provider = OrganizationContentProvider(organization.name)

        places = [place.to_dict() for place in
                  content_provider.session.query(Place).all()]

        for place in places:
            place['number_of_people'] = content_provider.get_number_of_people(str(place['id'])).run()[
                'number_of_people']

        response = jsonify({'places': places})
        response.status_code = 200
        response.headers['req_code'] = 5

        return response

    @jwt_required
    @manager_admin_required
    def post(self, organization_id):
        place = place_parser.parse_args()
        if len(place['coordinates']) != 4:
            abort(400, description="Exactly 4 coordinates are requested to create a place")

        organization, admin_representation, content_provider = self._organization_resource.get_organization(
            organization_id)

        areas_sum = 0.0
        for place_existent in content_provider.session.query(Place).all():
            areas_sum += abs(((place_existent.first_node_latitude*place_existent.second_node_longitude - place_existent.first_node_longitude*place_existent.second_node_latitude) +
                              (place_existent.second_node_latitude*place_existent.third_node_longitude - place_existent.second_node_longitude*place_existent.third_node_latitude) +
                              (place_existent.third_node_latitude*place_existent.fourth_node_longitude - place_existent.third_node_longitude*place_existent.fourth_node_latitude) +
                              (place_existent.fourth_node_latitude*place_existent.first_node_longitude - place_existent.fourth_node_longitude*place_existent.first_node_latitude)) / 2)

        areas_sum += abs(((place.get('coordinates')[0].get('latitude')*place.get('coordinates')[1].get('longitude') - place.get('coordinates')[0].get('longitude')*place.get('coordinates')[1].get('latitude')) +
                            (place.get('coordinates')[1].get('latitude')*place.get('coordinates')[2].get('longitude') - place.get('coordinates')[1].get('longitude')*place.get('coordinates')[2].get('latitude')) +
                            (place.get('coordinates')[2].get('latitude')*place.get('coordinates')[3].get('longitude') - place.get('coordinates')[2].get('longitude')*place.get('coordinates')[3].get('latitude')) +
                            (place.get('coordinates')[3].get('latitude')*place.get('coordinates')[0].get('longitude') - place.get('coordinates')[3].get('longitude')*place.get('coordinates')[0].get('latitude'))) / 2)
        if areas_sum > organization.max_quota_area_places:
            abort(403, description='You have reached maximum area for place')

        new_place = Place(place)

        content_provider.create_new_place(new_place)

        # Return place created
        created_place = content_provider.session.query(Place).get(new_place.id).to_dict()
        response = jsonify({'place': created_place})
        response.status_code = 200
        NotificationManger().send_notifications('New place! Update your organization list')
        response.headers['req_code'] = 6

        return response
