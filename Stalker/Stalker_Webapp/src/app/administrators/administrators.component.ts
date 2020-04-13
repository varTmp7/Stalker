import { Component, OnInit } from '@angular/core';
import {Organization, OrganizationType} from '../models/Organization';
import {OrganizationService} from '../organization.service';

@Component({
  selector: 'app-administrators',
  templateUrl: './administrators.component.html',
  styleUrls: ['./administrators.component.css']
})
export class AdministratorsComponent implements OnInit {
  organizations : Organization[];
  constructor(private organizationService: OrganizationService) {}

  ngOnInit(): void {
    this.getOrganizations();
  }

  private getOrganizations(): void {
    //this.organizationService.getOrganizations().subscribe(organizationsRetrieved => this.organizations = organizationsRetrieved);
    const retrievedFromServer = {
      organizations: [
        {
          address: 'Via mele, 9',
          city: 'Città',
          email: 'org@organazi.it',
          id: 1,
          ldap_common_name: 'organizzazione.org.it',
          ldap_domain_component: 'user.accounts',
          ldap_port: 396,
          ldap_url: 'ldap://organizzazione.org.it',
          name: 'Nuova organizzazione',
          nation: 'Italy',
          phone_number: '+31234567890',
          postal_code: 35010,
          region: 'Regione',
          type: OrganizationType.BOTH
        },
        {
          address: 'Via mele, 9',
          city: 'Città',
          email: 'public.org@organizzazione.it',
          id: 2,
          ldap_common_name: null,
          ldap_domain_component: null,
          ldap_port: null,
          ldap_url: null,
          name: 'Nuova organizzazione pubblica',
          nation: 'Italy',
          phone_number: '+31234567890',
          postal_code: 35010,
          region: 'Regione',
          type: OrganizationType.PUBLIC
        }
      ]
    };

    this.organizations = retrievedFromServer.organizations;
  }

 
}
