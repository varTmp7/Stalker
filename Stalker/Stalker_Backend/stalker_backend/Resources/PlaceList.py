from flask import jsonify
from flask_restful import Resource

from ..Models import Organization, Place
from stalker_backend.ContentProvider.OrganizationContentProvider import OrganizationContentProvider
from ..Parser.PlaceParser import place_parser


class PlaceList(Resource):
    @staticmethod
    def get(organization_id):
        org = Organization.Organization.query.get_or_404(organization_id,
                                                         description='There is no organization with id={}'.format(
                                                             organization_id))

        content_provider = OrganizationContentProvider(org.name)

        places = [place.to_dict() for place in
                  content_provider.session.query(Place.Place).all()]

        for place in places:
            place['number_of_people'] = content_provider.get_numeber_of_people(str(place['id'])).run()[
                'number_of_people']

        response = jsonify({'places': places})
        response.status_code = 200
        response.headers['req_code'] = 5

        return response

    @staticmethod
    def post(organization_id):
        org = Organization.Organization.query.get_or_404(organization_id,
                                                         description='There is no organization with id={}'.format(
                                                             organization_id))

        content_provider = OrganizationContentProvider(org.name)
        place = place_parser.parse_args()

        new_place = Place.Place(place['name'],
                                place['coordinates'][0]['latitude'],
                                place['coordinates'][0]['longitude'],
                                place['coordinates'][1]['latitude'],
                                place['coordinates'][1]['longitude'],
                                place['coordinates'][2]['latitude'],
                                place['coordinates'][2]['longitude'],
                                place['coordinates'][3]['latitude'],
                                place['coordinates'][3]['longitude'],
                                place['num_max_people'],
                                place['organization_id'])

        content_provider.create_new_place(new_place)

        # Return place created
        created_place = content_provider.session.query(Place.Place).get(new_place.id).to_dict()
        response = jsonify({'place': created_place})
        response.status_code = 200
        response.headers['req_code'] = 6

        return response
