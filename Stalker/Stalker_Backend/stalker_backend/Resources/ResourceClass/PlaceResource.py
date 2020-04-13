from flask import abort, jsonify
from flask_jwt_extended import get_jwt_identity

from .OrganizationResource import OrganizationResource
from ...ContentProvider.OrganizationContentProvider import OrganizationContentProvider
from ...Models.Organization import Organization
from ...Models.Place import Place


class PlaceResource:
    _organization_resource: OrganizationResource = None

    def __init__(self, organization_resource: OrganizationResource):
        self._organization_resource = organization_resource

    def get_place_unsafe(self, organizations_id, place_id):
        organization = self._organization_resource.get_organizations_unsafe(organizations_id)
        content_provider = OrganizationContentProvider(organization.name)
        place = content_provider.session.query(Place).get_or_404(place_id,
                                                                 description='There is no place with id={}'.format(
                                                                     place_id))
        return place, organization, content_provider

    def get_place(self, organization_id, place_id) -> (Place, Organization, dict, OrganizationContentProvider):
        organization, admin_representation, content_provider = self._organization_resource.get_organization(
            organization_id)
        place, organization, content_provider = self.get_place_unsafe(organization_id, place_id)
        return place, organization, admin_representation, content_provider

    def approve_place(self, organization_id, place_id, approve):
        organization = self._organization_resource.get_organizations_unsafe(organization_id)
        content_provider = OrganizationContentProvider(organization.name)

        place = content_provider.session.query(Place).get_or_404(place_id,
                                                                 description='There is no place with id={}'.format(
                                                                     place_id))
        place.set_approved(approve)
        content_provider.session.commit()

    def get_organization_resource(self):
        return self._organization_resource
