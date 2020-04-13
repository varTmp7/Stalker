from flask import jsonify
from flask_jwt_extended import jwt_required
from flask_restful import Resource

from .ResourceClass.PlaceResource import PlaceResource
from ..Models import Track, Place
from ..Utils.AuthUtils import watcher_admin_required


class TrackListForOrganization(Resource):
    _place_resource: PlaceResource = None

    def __init__(self, place_resource):
        self._place_resource = place_resource

    @jwt_required
    @watcher_admin_required
    def get(self, organization_id):
        organization, admin_representation, content_provider = self._place_resource.get_organization_resource().\
            get_organization(organization_id)

        tracks = []
        for track in content_provider.session.query(Track.Track).all():
            track_json = track.to_dict()
            track_json['place'] = content_provider.session.query(Place.Place).get(track.place_id).to_dict()
            tracks.append(track_json)

        response = jsonify({'tracks': tracks})
        response.status_code = 200
        response.headers['req_code'] = 12

        return response
