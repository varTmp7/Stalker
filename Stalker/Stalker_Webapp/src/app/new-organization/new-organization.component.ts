import { Component, OnInit } from '@angular/core';
import {Organization, OrganizationType} from '../models/Organization';
import {OrganizationService} from '../organization.service';
import {FormControl,FormBuilder,Validators, FormGroup} from '@angular/forms';
import {Location} from '@angular/common';

@Component({
  selector: 'app-new-organization',
  templateUrl: './new-organization.component.html',
  styleUrls: ['./new-organization.component.css']
})
export class NewOrganizationComponent implements OnInit {
  editForm: FormGroup;
  isPublic = true;
  constructor(
    private fb: FormBuilder,
    private organizationService: OrganizationService,
    private location: Location
  ) { }

  ngOnInit(): void {
    this.editForm = this.fb.group({
      name: '', 
      address: '', 
      city: '',
      region: '', 
      postalCode: '', 
      nation: '',  
      phoneNumber: '', 
      email: '', 
      type: 'Public',
      ldapUrl: '', 
      ldapPort: '', 
      ldapCommonName: '', 
      ldapDomainComponent: ''
    });
  }

  create(organization){
    this.organizationService.createNewOrganization(
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
        //this is temporary, until we create our dialogs
        this.location.back();
      },
      (error => {
        //temporary as well
        this.location.back();
      })
    )
   
  }

  onChange(event){
    if(event.value !== "Public"){
       this.isPublic = false;
    }
    else  this.isPublic = true;
  }

}
