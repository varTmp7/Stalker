from flask import Blueprint, request, abort, jsonify
from flask_cors import cross_origin
from flask_jwt_extended import jwt_required

from stalker_backend.Utils.AuthUtils import system_admin_required
from stalker_backend.Resources.ResourceClass.PlaceResource import PlaceResource
from stalker_backend.Resources.ResourceClass.OrganizationResource import OrganizationResource

other_routes = Blueprint('others', __name__, url_prefix='/')


@other_routes.route('/', methods=['GET'])
def index():
    return "Hi, this is stalker-backend", 200


@other_routes.route('/organizations/<int:organization_id>/places/<int:place_id>/approve', methods=['POST'])
@jwt_required
@system_admin_required
def approve_place(organization_id, place_id):
    place_resource = PlaceResource(OrganizationResource())
    approved = request.get_json().get('approved')
    if approved is not None and type(approved) is bool:
        place_resource.approve_place(organization_id, place_id, approved)
        return jsonify({'ok': True}), 200
    else:
        return abort(400, description="Missing or wrong 'approved' key in json")
