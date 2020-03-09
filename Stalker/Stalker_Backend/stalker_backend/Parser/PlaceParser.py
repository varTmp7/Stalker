from flask_restful import reqparse


def coordinates_list_parser(coordinate):
    if 'latitude' not in coordinate or 'longitude' not in coordinate:
        raise ValueError('Latitude and longitude is required for every node')
    return coordinate


place_parser = reqparse.RequestParser()

place_parser.add_argument('name', required=True, trim=True)
place_parser.add_argument('num_max_people', required=False, trim=True)
place_parser.add_argument('coordinates', required=True, location='json', type=coordinates_list_parser, action='append')
place_parser.add_argument('organization_id', required=True, type=int)

coordinates_parser = reqparse.RequestParser()

