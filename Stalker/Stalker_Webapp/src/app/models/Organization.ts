export enum OrganizationType {
  PRIVATE = 'private',
  PUBLIC = 'public',
  BOTH = 'both'
}

export interface Organization {
  id: number;
  name: string;
  address: string;
  city: string;
  postal_code: number;
  region: string;
  nation: string;
  email: string;
  phone_number: string;
  type: OrganizationType;
  ldap_domain_component?: string;
  ldap_common_name?: string;
  ldap_port?: number;
  ldap_url?: string;
}
