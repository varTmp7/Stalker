from flask_restful import reqparse
from datetime import datetime

track_parser = reqparse.RequestParser()

track_parser.add_argument('entered', type=bool, required=True)
track_parser.add_argument('authenticated', type=bool, required=True)
track_parser.add_argument('uid_number', type=int, required=False)
track_parser.add_argument('username', required=False)
track_parser.add_argument('name', required=False)
track_parser.add_argument('surname', required=False)
track_parser.add_argument('date_time', type=lambda x: datetime.strptime(x, '%Y-%m-%dT%H:%M:%S'), required=False)
