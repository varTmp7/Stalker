from stalker_backend import db_alchemy as db
import enum


class Role(enum.Enum):
    OWNER = "owner"
    MANAGER = "manager"
    WATCHER = "watcher"


class OrganizationsAdmins(db.Model):
    __tablename__ = "organizations_admins"
    id = db.Column(db.Integer, primary_key=True)
    organization_id = db.Column(db.Integer, db.ForeignKey('organizations.id'))
    admin_id = db.Column(db.Integer, db.ForeignKey('admins.id'))
    role = db.Column(db.Enum(Role), unique=False, nullable=False, index=False)

    admin = db.relationship('Admin', backref=db.backref('organizations_admins', cascade='all, delete-orphan'))
    organization = db.relationship('Organization',
                                   backref=db.backref('organizations_admins', cascade='all, delete-orphan'))

    def __init__(self, organization_id, admin_id, role):
        self.organization_id = organization_id
        self.admin_id = admin_id
        self.role = role

    def edit_role(self, new_role):
        if new_role == 'WATCHER':
            self.role = Role.WATCHER
        if new_role == 'MANAGER':
            self.role = Role.MANAGER
        db.session.commit()
