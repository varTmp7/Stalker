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

    def __init__(self, name, up_dx_lat, up_dx_long, up_sx_lat, up_sx_long, down_dx_lat, down_dx_long, down_sx_lat,
                 down_sx_long, num_max_people, organization_id):
        self.name = name
        self.first_node_latitude = up_dx_lat
        self.first_node_longitude = up_dx_long
        self.second_node_latitude = up_sx_lat
        self.second_node_longitude = up_sx_long
        self.third_node_latitude = down_dx_lat
        self.third_node_longitude = down_dx_long
        self.fourth_node_latitude = down_sx_lat
        self.fourth_node_longitude = down_sx_long
        self.num_max_people = num_max_people
        self.organization_id = organization_id

    def edit(self, name, up_dx_lat, up_dx_long, up_sx_lat, up_sx_long, down_dx_lat, down_dx_long, down_sx_lat,
             down_sx_long, num_max_people):

        self.name = name
        self.first_node_latitude = up_dx_lat
        self.first_node_longitude = up_dx_long
        self.second_node_latitude = up_sx_lat
        self.second_node_longitude = up_sx_long
        self.third_node_latitude = down_dx_lat
        self.third_node_longitude = down_dx_long
        self.fourth_node_latitude = down_sx_lat
        self.fourth_node_longitude = down_sx_long
        self.num_max_people = num_max_people
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
            'approved': self.approved
        }
