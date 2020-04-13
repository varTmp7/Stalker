from flask import jsonify, Response, abort
from flask_jwt_extended import jwt_required
from flask_restful import Resource

from .ResourceClass.PlaceResource import PlaceResource
from ..Models.Track import Track
from ..Parser.TrackParser import track_parser
from stalker_backend.Utils.AuthUtils import watcher_admin_required, organization_token_required
from stalker_backend.Utils.UserAuth import UserAuth


class TrackListForPlace(Resource):
    _place_resource: PlaceResource = None

    def __init__(self, place_resource):
        self._place_resource = place_resource

    @jwt_required
    @watcher_admin_required
    def get(self, organization_id, place_id):
        place, organization, admin_representation, content_provider = self._place_resource.get_place(organization_id,
                                                                                                     place_id)
        tracks = [track.to_dict() for track in place.tracks]

        response = jsonify({'tracks': tracks})
        response.status_code = 200
        response.headers['req_code'] = 10

        return response

    @organization_token_required
    def post(self, organization_id, place_id):
        track = track_parser.parse_args()

        place, organization, content_provider = self._place_resource.get_place_unsafe(organization_id, place_id)

        if track['authenticated'] and organization.ldap_url is not None:
            if track['username'] and track['password']:
                try:
                    user_auth = UserAuth(track['auth_type']).get_user_auth(url=organization.ldap_url,
                                                                           port=organization.ldap_port,
                                                                           cn=organization.ldap_common_name,
                                                                           dn=organization.ldap_domain_component)
                    user_info = user_auth.login(track['username'], track['password'])
                    track['place_id'] = place_id
                    del track['username']
                    print(track)
                    new_track = Track({**track, **user_info})
                except Exception as e:
                    print("Something went wrong while adding a new track")
                    print(e)
                    abort(403, description='Impossible to add new track, auth_type, user and password not recognized')
            else:
                abort(400, description='Missing username or password in request')
        else:
            new_track = None

        content_provider.add_new_track(new_track, track['entered'], place_id)

        response = Response()
        response.status_code = 200
        response.headers['req_code'] = 11
        return response
