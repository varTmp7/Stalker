import { Component, OnInit } from '@angular/core';
import {Organization, OrganizationType} from '../models/Organization';
import {OrganizationService} from '../organization.service';
import {ActivatedRoute} from '@angular/router';
import {FormBuilder, FormGroup} from '@angular/forms';
import { Location } from '@angular/common';

@Component({
  selector: 'app-organizationdetail',
  templateUrl: './organizationdetail.component.html',
  styleUrls: ['./organizationdetail.component.css']
})
export class OrganizationdetailComponent implements OnInit {
  organization;
  isPublic= true;
  editForm: FormGroup;

  constructor(
    private fb: FormBuilder,
    private route: ActivatedRoute,
    private organizationService: OrganizationService,
    private location: Location
    ) {}

  ngOnInit(): void {
    this.getOrganization();
    this.editForm = this.fb.group({
        name: this.organization.name, 
        address: this.organization.address, 
        city: this.organization.city,
        region: this.organization.region, 
        postalCode: this.organization.postal_code, 
        nation: this.organization.nation,  
        phoneNumber: this.organization.phone_number, 
        email: this.organization.email, 
        type: this.organization.type,
        ldapUrl: this.organization.ldapUrl, 
        ldapPort: this.organization.ldapPort, 
        ldapCommonName: this.organization.ldapCommonName, 
        ldapDomainComponent: this.organization.ldapDomainComponent
    });

    if(this.organization.type != "Public"){
      this.isPublic= false;
    } 
  }

  private getOrganization(){
    const organizationId = +this.route.snapshot.paramMap.get('organization_id');
    this.organizationService.getOrganizationDetail(organizationId).subscribe(organizationRetrieved => this.organization = organizationRetrieved); 
  }

  submit(organization){
    const organizationId = +this.route.snapshot.paramMap.get('organization_id');
    this.organizationService.editOrganization(organizationId, organization.name, organization.address, organization.city, organization.region, organization.postalCode, organization.nation,  organization.phoneNumber, organization.email, organization.type, organization.ldapUrl, organization.ldapPort, organization.ldapCommonName, organization.ldapDomainComponent); 
  }

  onChange(event){
    if(event.value !== "Public"){
       this.isPublic = false;
    }
    else  this.isPublic = true;
  }

  editOrganization(organization){
    const organizationId = +this.route.snapshot.paramMap.get('organization_id');
    this.organizationService.editOrganization(
      organizationId,
      organization.name, 
      organization.address, 
      organization.city, 
      organization.region, 
      organization.postalCode, 
      organization.nation, 
      organization.phoneNumber, 
      organization.email,
      organization.type, 
      organization.ldapUrl, 
      organization.ldapPort, 
      organization.ldapCommonName, 
      organization.ldapDomainComponent).subscribe(res => {
        this.location.back();
      },
      (error => {
        this.location.back();
      })
    );
   
  }

}
