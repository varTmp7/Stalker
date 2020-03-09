from stalker_backend import db_alchemy as db
import enum


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

    #places = db.relationship("Place", backref="organization", lazy=True)

    def __init__(self, name, address, city, region, postal_code, nation, phone_number, email, organization_type,
                 ldap_url=None, ldap_port=None, ldap_cn=None, ldap_dc=None):
        self.name = name
        self.address = address
        self.city = city
        self.region = region
        self.postal_code = postal_code
        self.nation = nation
        self.phone_number = phone_number
        self.email = email
        self.organization_type = organization_type
        self.ldap_url = ldap_url
        self.ldap_port = ldap_port
        self.ldap_common_name = ldap_cn
        self.ldap_domain_component = ldap_dc

    def edit(self, name, address, city, region, postal_code, nation, phone_number, email, organization_type, ldap_url,
             ldap_port, ldap_cn, ldap_dc):
        self.name = name
        self.address = address
        self.city = city
        self.region = region
        self.postal_code = postal_code
        self.nation = nation
        self.phone_number = phone_number
        self.email = email
        self.organization_type = organization_type
        self.ldap_url = ldap_url
        self.ldap_port = ldap_port
        self.ldap_common_name = ldap_cn
        self.ldap_domain_component = ldap_dc

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

    def __repr__(self):
        return "<Organization {}>".format(self.name)
