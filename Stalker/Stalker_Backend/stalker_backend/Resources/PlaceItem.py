from flask import jsonify, Response, abort
from flask_jwt_extended import jwt_required, get_jwt_identity
from flask_restful import Resource

from .ResourceClass.PlaceResource import PlaceResource

from ..Parser.PlaceParser import place_parser
from ..Models.Place import Place
from stalker_backend.Utils.AuthUtils import organization_token_required, manager_admin_required, owner_admin_required


class PlaceItem(Resource):
    _place_resource: PlaceResource = None

    def __init__(self, place_resource):
        self._place_resource = place_resource

    @organization_token_required
    def get(self, organization_id, place_id):
        place, organization, content_provider = self._place_resource.get_place_unsafe(organization_id, place_id)

        place_info = place.to_dict()
        place_info['number_of_people'] = content_provider.get_number_of_people(str(place_info['id'])).run()[
            'number_of_people']
        response = jsonify(place_info)
        response.status_code = 200
        response.headers['req_code'] = 7
        return response

    @jwt_required
    @manager_admin_required
    def put(self, organization_id, place_id):
        edited_place = place_parser.parse_args()

        place, organization, admin_representation, content_provider = self._place_resource.get_place(organization_id,
                                                                                                     place_id)

        place.edit(edited_place)
        content_provider.session.commit()

        # return the modified place
        modified_place = content_provider.session.query(Place).get(place.id).to_dict()
        response = jsonify(modified_place)
        response.status_code = 200
        response.headers['req_code'] = 8
        return response

    @jwt_required
    @owner_admin_required
    def delete(self, organization_id, place_id):
        place, organization, admin_representation, content_provider = self._place_resource.get_place(organization_id,
                                                                                                     place_id)

        content_provider.delete_place(place)

        response = Response()
        response.status_code = 200
        response.headers['req_code'] = 4
        return response
