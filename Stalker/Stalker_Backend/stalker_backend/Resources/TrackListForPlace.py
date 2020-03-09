from flask import jsonify, Response
from flask_restful import Resource
from ..Models import Organization, Place, Track
from stalker_backend.ContentProvider.OrganizationContentProvider import OrganizationContentProvider
from ..Parser.TrackParser import track_parser


class TrackListForPlace(Resource):
    @staticmethod
    def get(organization_id, place_id):
        org = Organization.Organization.query.get_or_404(organization_id,
                                                         description='There is no organization with id={}'.format(
                                                             organization_id))

        content_provider = OrganizationContentProvider(org.name)

        place = content_provider.session.query(Place.Place).get_or_404(place_id,
                                                                       description='There is no place with id={}'.format(
                                                                           place_id))
        tracks = [track.to_dict() for track in place.tracks]

        response = jsonify({'tracks': tracks})
        response.status_code = 200
        response.headers['req_code'] = 10

        return response

    @staticmethod
    def post(organization_id, place_id):
        org = Organization.Organization.query.get_or_404(organization_id,
                                                         description='There is no organization with id={}'.format(
                                                             organization_id))

        content_provider = OrganizationContentProvider(org.name)

        place = content_provider.session.query(Place.Place).get_or_404(place_id,
                                                                       description='There is no place with id={}'.format(
                                                                           place_id))
        track = track_parser.parse_args()
        if track['authenticated']:
            new_track = Track.Track(track['entered'],
                                    track['uid_number'],
                                    track['username'],
                                    track['name'],
                                    track['surname'],
                                    track['date_time'],
                                    place.id)
        else:
            new_track = None

        content_provider.add_new_track(new_track, track['entered'], place_id)

        response = Response()
        response.status_code = 200
        response.headers['req_code'] = 11
        return response
