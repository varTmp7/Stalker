from flask import jsonify
from flask_restful import Resource
from ..Models import Track, Organization, Place
from stalker_backend.ContentProvider.OrganizationContentProvider import OrganizationContentProvider
from ..Parser.TrackParser import track_parser

class TrackListForOrganization(Resource):
    @staticmethod
    def get(organization_id):
        org = Organization.Organization.query.get_or_404(organization_id,
                                                         description='There is no organization with id={}'.format(
                                                             organization_id))

        content_provider = OrganizationContentProvider(org.name)

        tracks = []
        for track in content_provider.session.query(Track.Track).all():
            track_json = track.to_dict()
            track_json['place'] = content_provider.session.query(Place.Place).get(track.place_id).to_dict()
            tracks.append(track_json)


        response = jsonify({'tracks': tracks})
        response.status_code = 200
        response.headers['req_code'] = 12

        return response
