from stalker_backend import db_alchemy as db


class Place(db.Model):
    __tablename__ = "places"

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=False, unique=False, nullable=False)
    first_node_latitude = db.Column(db.Float(), index=False, unique=False, nullable=False)
    first_node_longitude = db.Column(db.Float(), index=False, unique=False, nullable=False)
    second_node_latitude = db.Column(db.Float(), index=False, unique=False, nullable=False)
    second_node_longitude = db.Column(db.Float(), index=False, unique=False, nullable=False)
    third_node_latitude = db.Column(db.Float(), index=False, unique=False, nullable=False)
    third_node_longitude = db.Column(db.Float(), index=False, unique=False, nullable=False)
    fourth_node_latitude = db.Column(db.Float(), index=False, unique=False, nullable=False)
    fourth_node_longitude = db.Column(db.Float(), index=False, unique=False, nullable=False)
    num_max_people = db.Column(db.Integer(), index=False, unique=False, nullable=True)
    approved = db.Column(db.Boolean(), index=False, unique=False, nullable=False, default=False)

    organization_id = db.Column(db.Integer, unique=False, index=False, nullable=False)
    tracks = db.relationship('Track', backref='places', lazy=True, cascade='delete')

    def __set_data(self, place):
        self.name = place.get('name')
        self.first_node_latitude = place.get('coordinates')[0].get('latitude')
        self.first_node_longitude = place.get('coordinates')[0].get('longitude')
        self.second_node_latitude = place.get('coordinates')[1].get('latitude')
        self.second_node_longitude = place.get('coordinates')[1].get('longitude')
        self.third_node_latitude = place.get('coordinates')[2].get('latitude')
        self.third_node_longitude = place.get('coordinates')[2].get('longitude')
        self.fourth_node_latitude = place.get('coordinates')[3].get('latitude')
        self.fourth_node_longitude = place.get('coordinates')[3].get('longitude')
        self.num_max_people = place.get('num_max_people')

    def __init__(self, place):
        self.__set_data(place)
        self.organization_id = place['organization_id']

    def edit(self, place):
        self.__set_data(place)
        self.approved = False

    def set_approved(self, approved=True):
        self.approved = approved

    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            'num_max_people': self.num_max_people,
            'coordinates': [
                {'latitude': self.first_node_latitude, 'longitude': self.first_node_longitude},
                {'latitude': self.second_node_latitude, 'longitude': self.second_node_longitude},
                {'latitude': self.third_node_latitude, 'longitude': self.third_node_longitude},
                {'latitude': self.fourth_node_latitude, 'longitude': self.fourth_node_longitude},
            ],
            'approved': self.approved,
            'type': 'LuogoQuadrilatero'
        }
