from stalker_backend import db_alchemy as db


class Admin(db.Model):
    __tablename__ = "admins"
    id = db.Column(db.Integer, primary_key=True)
    email = db.Column(db.String(128), unique=True, nullable=False)
    password = db.Column(db.String(128), unique=False, index=False, nullable=False)
    name = db.Column(db.String(128), unique=False, nullable=False)
    surname = db.Column(db.String(128), unique=False, nullable=False)
    is_system_admin = db.Column(db.Boolean, unique=False, nullable=False, default=False)
    is_owner = db.Column(db.Boolean, unique=False, nullable=False, default=False)
    max_quota_organizations = db.Column(db.Integer, unique=False, nullable=False, default=10)

    organizations = db.relationship('Organization', secondary='organizations_admins')

    def __set_data(self, admin):
        self.email = admin.get('email')
        if admin['password']:
            self.password = admin.get('password')
        self.name = admin.get('name')
        self.surname = admin.get('surname')

    def __init__(self, admin, is_owner, is_system_admin):
        self.__set_data(admin)
        self.is_owner = is_owner
        self.is_system_admin = is_system_admin

    def edit(self, admin):
        self.__set_data(admin)
        db.session.commit()

    def to_dict(self, organization_id):
        if organization_id is None:
            if self.is_system_admin:
                role = 'system'
            elif self.is_owner:
                role = 'owner'
            else:
                role = 'other'
        else:
            from ..Models import OrganizationsAdmins

            relationship = db.session.query(OrganizationsAdmins.OrganizationsAdmins).filter_by(
                organization_id=organization_id, admin_id=self.id).first()

            role = relationship.role.value

        return {
            'id': self.id,
            'email': self.email,
            'name': self.name,
            'surname': self.surname,
            'role': role,
            'max_quota_organizations': self.max_quota_organizations
        }
