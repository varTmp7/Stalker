from stalker_backend import db_alchemy as db

class Track(db.Model):
    __tablename__ = "tracks"

    id = db.Column(db.Integer, primary_key=True)
    entered = db.Column(db.Boolean, unique=False, nullable=False)
    uidNumber = db.Column(db.Integer, unique=False, nullable=False)
    username = db.Column(db.String(128), unique=False, nullable=True)
    name = db.Column(db.String(128), unique=False, nullable=True)
    surname = db.Column(db.String(128), unique=False, nullable=True)
    dateTime = db.Column(db.DateTime, unique=False, nullable=True)

    place_id = db.Column(db.Integer, db.ForeignKey('places.id'), nullable=False)

    def __init__(self, entered, uid_number, username, name, surname, date_time, place_id):
        self.entered = entered
        self.uidNumber = uid_number
        self.username = username
        self.name = name
        self.surname = surname
        self.dateTime = date_time
        self.place_id = place_id

    def to_dict(self):
        return {
            'id': self.id,
            'entered': self.entered,
            'date_time': self.dateTime.strftime("%Y-%m-%dT%H:%M:%S"),
            'uid_number': self.uidNumber,
            'username': self.username,
            'name': self.name,
            'surname': self.surname
        }