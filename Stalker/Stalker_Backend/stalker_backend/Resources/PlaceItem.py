from flask import jsonify, Response
from flask_restful import Resource

from ..Parser.PlaceParser import place_parser
from stalker_backend.ContentProvider.OrganizationContentProvider import OrganizationContentProvider
from ..Models import Organization, Place


class PlaceItem(Resource):

    @staticmethod
    def get(organization_id, place_id):
        org = Organization.Organization.query.get_or_404(organization_id,
                                                         description='There is no organization with id={}'.format(
                                                             organization_id))

        content_provider = OrganizationContentProvider(org.name)
        place = content_provider.session.query(Place.Place).get_or_404(place_id,
                                                                       description='There is no place with id={}'.format(
                                                                           place_id))
        place_info = place.to_dict()
        place_info['number_of_people'] = content_provider.get_numeber_of_people(str(place_info['id'])).run()[
            'number_of_people']
        response = jsonify(place_info)
        response.status_code = 200
        response.headers['req_code'] = 7
        return response

    @staticmethod
    def put(organization_id, place_id):
        args = place_parser.parse_args()
        org = Organization.Organization.query.get_or_404(organization_id,
                                                         description='There is no organization with id={}'.format(
                                                             organization_id))

        content_provider = OrganizationContentProvider(org.name)
        place = content_provider.session.query(Place.Place).get_or_404(place_id,
                                                                       description='There is no place with id={}'.format(
                                                                           place_id))
        place.edit(args['name'],
                   args['coordinates'][0]['latitude'],
                   args['coordinates'][0]['longitude'],
                   args['coordinates'][1]['latitude'],
                   args['coordinates'][1]['longitude'],
                   args['coordinates'][2]['latitude'],
                   args['coordinates'][2]['longitude'],
                   args['coordinates'][3]['latitude'],
                   args['coordinates'][3]['longitude'],
                   args['num_max_people'])
        content_provider.session.commit()

        # return the modified place
        modified_place = content_provider.session.query(Place.Place).get(place.id).to_dict()
        response = jsonify(modified_place)
        response.status_code = 200
        response.headers['req_code'] = 8
        return response

    @staticmethod
    def delete(organization_id, place_id):
        org = Organization.Organization.query.get_or_404(organization_id,
                                                         description='There is no organization with id={}'.format(
                                                             organization_id))

        content_provider = OrganizationContentProvider(org.name)
        place = content_provider.session.query(Place.Place).get_or_404(place_id,
                                                                       description='There is no place with id={}'.format(
                                                                           place_id))

        content_provider.delete_place(place)

        deleted_place = content_provider.session.query(Place.Place).get(place.id)
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
