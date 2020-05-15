from flask import jsonify, abort
from flask_jwt_extended import jwt_required
from flask_restful import Resource

from .ResourceClass.OrganizationResource import OrganizationResource
from ..Models import Track, Place
from ..Utils.AuthUtils import organization_token_required, watcher_admin_required
from stalker_backend.Parser.AuthParser import auth_parser
from stalker_backend.Utils.UserAuth import UserAuth
from stalker_backend.ContentProvider.OrganizationContentProvider import OrganizationContentProvider


class TrackListForOrganization(Resource):
    _organization_resource: OrganizationResource = None

    def __init__(self, organization_resource):
        self._organization_resource = organization_resource

    def get_tracks(self, organization):
        content_provider = OrganizationContentProvider(organization.name)
        tracks = []
        for track in content_provider.session.query(Track.Track).all():
            track_json = track.to_dict()
            track_json['place'] = content_provider.session.query(Place.Place).get(track.place_id).to_dict()
            tracks.append(track_json)
        return tracks

    @jwt_required
    @watcher_admin_required
    def get(self, organization_id):
        organization = self._organization_resource.get_organizations_unsafe(organization_id)
        return jsonify({'tracks': self.get_tracks(organization)})

    @organization_token_required
    def post(self, organization_id):
        auth_info = auth_parser.parse_args()
        organization = self._organization_resource.get_organizations_unsafe(organization_id)
        content_provider = OrganizationContentProvider(organization.name)
        user_info = None

        try:
            user_auth = UserAuth(auth_info['auth_type']).get_user_auth(url=organization.ldap_url,
                                                                       port=organization.ldap_port,
                                                                       cn=organization.ldap_common_name,
                                                                       dn=organization.ldap_domain_component)
            user_info = user_auth.login(auth_info.get('username'), auth_info.get('password'))
        except Exception as e:
            print(e)
            abort(403, description='Login failed, check auth_type, username and password')

        tracks = self.get_tracks(organization)

        tracks = list(filter(lambda track: track['uid_number'] == user_info.get('uid_number'), tracks))

        response = jsonify({'user_info': user_info,
                            'tracks': tracks})
        response.status_code = 200
        response.headers['req_code'] = 12

        return response
