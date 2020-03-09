import {Component, OnInit} from '@angular/core';
import {Organization, OrganizationType} from '../models/Organization';
import {OrganizationService} from '../organization.service';

@Component({
  selector: 'app-organizations',
  templateUrl: './organizations.component.html',
  styleUrls: ['./organizations.component.css']
})
export class OrganizationsComponent implements OnInit {
  organizations: Organization[];

  constructor(private organizationService: OrganizationService) {
  }

  ngOnInit(): void {
    this.getOrganizations();
  }

  private getOrganizations(): void {
    this.organizationService.getOrganizations().subscribe(organizationsRetrieved => this.organizations = organizationsRetrieved);
  }
}
