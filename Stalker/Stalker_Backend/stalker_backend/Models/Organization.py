from stalker_backend import db_alchemy as db
import enum
import secrets


class OrganizationType(enum.Enum):
    PRIVATE = "private"
    PUBLIC = "public"
    BOTH = "both"


class Organization(db.Model):
    __tablename__ = "organizations"

    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(64), index=False, unique=True, nullable=False)
    # image
    address = db.Column(db.String(128), index=False, unique=False, nullable=False)
    city = db.Column(db.String(128), index=False, unique=False, nullable=False)
    region = db.Column(db.String(128), index=False, unique=False, nullable=False)
    postal_code = db.Column(db.String(5), index=False, unique=False, nullable=False)
    nation = db.Column(db.String(128), index=False, unique=False, nullable=False)
    phone_number = db.Column(db.String(16), index=False, unique=False, nullable=False)
    email = db.Column(db.String(128), index=False, unique=True, nullable=False)
    organization_type = db.Column(db.Enum(OrganizationType), index=False, unique=False, nullable=False)
    # ldap_things
    ldap_url = db.Column(db.String(2048), index=False, unique=False, nullable=True)
    ldap_port = db.Column(db.Integer(), index=False, unique=False, nullable=True)
    ldap_domain_component = db.Column(db.String(128), index=False, unique=False, nullable=True)
    ldap_common_name = db.Column(db.String(128), index=False, unique=False, nullable=True)
    token = db.Column(db.String(32), index=False, unique=True, nullable=False)

    admins = db.relationship('Admin', secondary='organizations_admins')

    def __set_data(self, organization):
        self.name = organization.get('name')
        self.address = organization.get('address')
        self.city = organization.get('city')
        self.region = organization.get('region')
        self.postal_code = organization.get('postal_code')
        self.nation = organization.get('nation')
        self.phone_number = organization.get('phone_number')
        self.email = organization.get('email')
        self.organization_type = organization.get('type')
        self.ldap_url = organization.get('ldap_url')
        self.ldap_port = organization.get('ldap_port')
        self.ldap_common_name = organization.get('ldap_common_name')
        self.ldap_domain_component = organization.get('ldap_domain_component')

    def __init__(self, organization):
        self.__set_data(organization)
        self.token = secrets.token_hex(16)

    def edit(self, organization):
        self.__set_data(organization)

    def to_dict(self):
        return {
            'id': self.id,
            'name': self.name,
            # 'image': organization.image,
            'address': self.address,
            'city': self.city,
            'region': self.region,
            'postal_code': self.postal_code,
            'nation': self.nation,
            'phone_number': self.phone_number,
            'email': self.email,
            'type': self.organization_type.value,
            'ldap_url': self.ldap_url,
            'ldap_port': self.ldap_port,
            'ldap_domain_component': self.ldap_domain_component,
            'ldap_common_name': self.ldap_common_name,
        }

