from flask_jwt_extended import get_jwt_identity
from flask import abort

from stalker_backend.ContentProvider.OrganizationContentProvider import OrganizationContentProvider
from stalker_backend.Models.Organization import Organization
from stalker_backend.Utils.AuthUtils import check_admin_permission


class OrganizationResource:
    def get_organizations_unsafe(self, organization_id: int) -> Organization:
        organization = Organization.query.get_or_404(organization_id,
                                                     description='There is no organization with id={}'.format(
                                                         organization_id))
        return organization

    def get_organization(self, organization_id: int):
        organization = self.get_organizations_unsafe(organization_id)
        admin_representation = get_jwt_identity()

        if not check_admin_permission(admin_representation['id'], organization):
            abort(403, description='Admin not allowed to act on this organization')

        content_provider = OrganizationContentProvider(organization.name)

        return organization, admin_representation, content_provider
