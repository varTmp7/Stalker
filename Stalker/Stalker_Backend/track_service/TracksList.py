from flask import jsonify, Response, abort
from flask_jwt_extended import jwt_required
from flask_restful import Resource

from stalker_backend.Resources.ResourceClass.PlaceResource import PlaceResource
from track_service.Track import Track
from stalker_backend.Utils.AuthUtils import watcher_admin_required, organization_token_required
from stalker_backend.Utils.UserAuth import UserAuth

from rethinkdb import RethinkDB
from track_service.TrackParser import track_parser
import pika


class TrackList(Resource):
    _place_resource: PlaceResource = None

    def __init__(self, place_resource):
        self._place_resource = place_resource

    def __add_track(self, organization_id, place_id, track, entered):
        if track:
            connection = pika.BlockingConnection(pika.ConnectionParameters(host='rabbitmq'))
            channel = connection.channel()
            channel.queue_declare(queue='tracks_queue', durable=True)
            channel.basic_publish(
                exchange='',
                routing_key='tracks_queue',
                body=track,
                properties=pika.BasicProperties(
                    delivery_mode=2,
                ))
            connection.close()

        rethink_connection = RethinkDB()
        rethink_connection.connect('rethinkdb-proxy', 28015).repl()
        rethink_db = rethink_connection.db('stalker_organizations')
        if str(organization_id) not in rethink_db.table_list().run():
            rethink_db.table_create(str(organization_id)).run()
        rethink_organization = rethink_db.table(str(organization_id))

        if entered:
            rethink_organization.get(place_id).update(
                {'number_of_people': self.rethink_connection.row['number_of_people'] + 1}).run()
        else:
            rethink_organization.get(place_id).update(
                {'number_of_people': self.rethink_connection.row['number_of_people'] - 1}).run()

    @jwt_required
    @watcher_admin_required
    def get(self, organization_id, place_id):
        tracks = [track.to_dict() for track in
                  Track.query.filter_by(organization_id=organization_id, place_id=place_id).all()]

        response = jsonify({'tracks': tracks})
        response.status_code = 200
        response.headers['req_code'] = 10

        return response

    @organization_token_required
    def post(self, organization_id, place_id):
        track = track_parser.parse_args()
        new_track = None

        if track['authenticated']:
            if track['username'] and track['password']:
                try:
                    user_auth = UserAuth(track['auth_type']).get_user_auth(url=track['ldap_url'],
                                                                           port=track['ldap_port'],
                                                                           cn=track['ldap_common_name'],
                                                                           dn=track['ldap_domain_component'])
                    user_info = user_auth.login(track['username'], track['password'])
                    track['place_id'] = place_id
                    del track['username']
                    new_track = Track({**track, **user_info})
                except Exception as e:
                    print("Something went wrong while adding a new track")
                    print(e)
                    abort(403, description='Impossible to add new track, auth_type, user and password not recognized')
            else:
                abort(400, description='Missing username or password in request')
        else:
            new_track = None

        self.__add_track(organization_id, place_id, new_track, track['entered'])

        response = Response()
        response.status_code = 200
        response.headers['req_code'] = 11
        return response
