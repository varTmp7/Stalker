import {Component, OnInit} from '@angular/core';
import {OrganizationService} from '../services/organization.service';
import {Organization, OrganizationType} from '../models/Organization';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ActivatedRoute, Router} from '@angular/router';
import {DataService} from '../services/data.service';
import {Admin} from '../models/Admin';
import {AuthenticationService} from '../services/authentication.service';
import {ImageService} from '../services/image.service';
import {HttpErrorResponse} from "@angular/common/http";
import {MatSnackBar} from "@angular/material/snack-bar";


@Component({
  selector: 'app-organization-detail',
  templateUrl: './organization-detail.component.html',
  styleUrls: ['./organization-detail.component.css']
})
export class OrganizationDetailComponent implements OnInit {

  public organization: Organization;
  public organizationForm: FormGroup;
  public isPublic = false;
  public title: string;
  public subtitle: string;
  public currentAdmin: Admin;
  public imagePreviewSrc = 'https://res.cloudinary.com/dyz86jubl/image/upload/v1587155106/iu_uvy5az.jpg';

  constructor(private organizationService: OrganizationService,
              private formBuilder: FormBuilder,
              private activeRoute: ActivatedRoute,
              private router: Router,
              private dataService: DataService,
              private authenticationService: AuthenticationService,
              private imageService: ImageService,
              private snackBar: MatSnackBar) {
  }

  ngOnInit(): void {
    this.currentAdmin = this.authenticationService.currentUserValue;
    this.organizationForm = this.formBuilder.group({
      name: [this.organization !== undefined ? `${this.organization.name}` : '', Validators.required],
      address: [this.organization !== undefined ? `${this.organization.address}` : '', Validators.required],
      city: [this.organization !== undefined ? `${this.organization.city}` : '', Validators.required],
      postal_code: [this.organization !== undefined ? `${this.organization.postal_code}` : '', Validators.required],
      region: [this.organization !== undefined ? `${this.organization.region}` : '', Validators.required],
      nation: [this.organization !== undefined ? `${this.organization.nation}` : '', Validators.required],
      email: [this.organization !== undefined ? `${this.organization.email}` : '', Validators.required],
      phone_number: [this.organization !== undefined ? `${this.organization.phone_number}` : '', Validators.required],
      type: [this.organization !== undefined ? `${this.organization.type}` : '', Validators.required],
      ldap_domain_component: this.organization !== undefined ? `${this.organization.ldap_domain_component}` : '',
      ldap_common_name: this.organization !== undefined ? `${this.organization.ldap_common_name}` : '',
      ldap_port: this.organization !== undefined ? `${this.organization.ldap_port}` : '',
      ldap_url: this.organization !== undefined ? `${this.organization.ldap_url}` : '',
    });
    if (this.activeRoute.snapshot.url[0].path === 'organization-detail') {
      this.getOrganization();
    }

  }

  private getOrganization() {
    const organizationId = +this.activeRoute.snapshot.paramMap.get('organization_id');
    this.organizationService.getOrganizationDetail(organizationId)
      .subscribe(organizationRetrieved => {
        this.organization = organizationRetrieved;
        this.organizationForm.patchValue(this.organization);
        this.isPublic = this.organization.type === OrganizationType.PUBLIC;
        this.imagePreviewSrc = this.organization.image_url;
      });
  }

  submit(organization) {
    const organizationId = +this.activeRoute.snapshot.paramMap.get('organization_id');
    this.organizationService.editOrganization(organizationId, organization.name, organization.address, organization.city,
      organization.region, organization.postalCode, organization.nation, organization.phoneNumber, organization.email, organization.type,
      organization.ldapUrl, organization.ldapPort, organization.ldapCommonName, organization.ldapDomainComponent);
  }

  onChange(event) {
    if (event.value !== 'public') {
      this.isPublic = false;
    } else {
      this.isPublic = true;
    }
  }

  deleteOrganization() {
    const organizationId = +this.activeRoute.snapshot.paramMap.get('organization_id');
    this.organizationService.deleteOrganization(organizationId).subscribe(res => {
      this.router.navigate(['']);
    });
  }

  uploadPhoto() {
    const fileUpload = document.getElementById('fileUpload') as HTMLInputElement;
    fileUpload.onchange = () => {
      const image = fileUpload.files[0];
      console.log(image);
      const imagePreview = document.getElementById('image-preview');
      const reader = new FileReader();

      this.imageService.uploadImage(image).subscribe(imgUrl => {
        console.log(imgUrl);
        this.imagePreviewSrc = imgUrl;
      });

      /*
      reader.onload = (e) => {
        if (typeof e.target.result === "string") {
          console.log(e.target.result);
          this.imagePreviewSrc = e.target.result;
        }
      };

      reader.readAsDataURL(image);
      */
    };
    fileUpload.click();
  }

  createEditOrganization(detailOrganization) {
    if (this.organization === undefined) {
      this.organizationService.createNewOrganization(detailOrganization.name,
        this.imagePreviewSrc,
        detailOrganization.address,
        detailOrganization.city,
        detailOrganization.region,
        detailOrganization.postal_code,
        detailOrganization.nation,
        detailOrganization.phone_number,
        detailOrganization.email,
        detailOrganization.type,
        detailOrganization.ldap_url,
        detailOrganization.ldap_port,
        detailOrganization.ldap_common_name,
        detailOrganization.ldap_domain_component).subscribe(organizationCreated => {
        this.router.navigate(['']);
        this.dataService.changeOrganization(organizationCreated);
      }, (error: HttpErrorResponse) => {
        if (error.status === 403) {
          this.snackBar.open('Admin reached maximum organization. Contact a System Admin to increase this number');
        }
      });
    } else {
      const organizationId = +this.activeRoute.snapshot.paramMap.get('organization_id');
      this.organizationService.editOrganization(
        organizationId,
        detailOrganization.name,
        this.imagePreviewSrc,
        detailOrganization.address,
        detailOrganization.city,
        detailOrganization.region,
        detailOrganization.postal_code,
        detailOrganization.nation,
        detailOrganization.phone_number,
        detailOrganization.email,
        detailOrganization.type,
        detailOrganization.ldap_url,
        detailOrganization.ldap_port,
        detailOrganization.ldap_common_name,
        detailOrganization.ldap_domain_component).subscribe(res => {
          this.organization = res;
        },
        (error => {
          this.router.navigate(['']);
        })
      );
    }
  }

}
