import {Component, Input, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Admin} from '../models/Admin';
import {ActivatedRoute, Router} from '@angular/router';
import {AdminService} from '../services/admin.service';
import {Organization} from '../models/Organization';
import {Location} from '@angular/common';
import {DataService} from '../services/data.service';
import {AuthenticationService} from '../services/authentication.service';

@Component({
  selector: 'app-admin-detail',
  templateUrl: './admin-detail.component.html',
  styleUrls: ['./admin-detail.component.css']
})
export class AdminDetailComponent implements OnInit {

  public adminForm: FormGroup;
  public currentAdmin: Admin;
  public operatingAdmin: Admin;
  @Input() selectedOrganization: Organization;

  constructor(private formBuilder: FormBuilder,
              private adminService: AdminService,
              private activatedRoute: ActivatedRoute,
              private location: Location,
              private dataService: DataService,
              private authenticationService: AuthenticationService) {
    this.dataService.selectedOrganization.subscribe(changedOrganization => this.selectedOrganization = changedOrganization);
  }

  ngOnInit(): void {
    this.operatingAdmin = this.authenticationService.currentUserValue;
    this.adminForm = this.formBuilder.group({
      name: [this.currentAdmin !== undefined ? `${this.currentAdmin.name}` : '', Validators.required],
      surname: [this.currentAdmin !== undefined ? `${this.currentAdmin.surname}` : '', Validators.required],
      email: [this.currentAdmin !== undefined ? `${this.currentAdmin.email}` : '', Validators.required],
      password: [this.currentAdmin !== undefined ? `${this.currentAdmin.password}` : '',
      Validators.pattern('(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])[A-Za-z\d$@$!%*?&].{8,}') ],
      confirmpassword: '',
      role: ''
    }, {
        validators: this.match('password','confirmpassword')
    });


    if (this.activatedRoute.snapshot.url[0].path === 'admin-detail') {
      this.adminService.getAdmin().subscribe(adminInfo => {
        this.currentAdmin = adminInfo;
        this.adminForm.patchValue(this.currentAdmin);
      }, error => {
        this.currentAdmin = null;
      });
    } else {
      this.currentAdmin = null;
    }
  }

  private match(controlName: string, matchingControlName: string) {
    return (formGroup: FormGroup) => {
        const control = formGroup.controls[controlName];
        const matchingControl = formGroup.controls[matchingControlName];

        if (control.errors && !matchingControl.errors.match) {
            // return if another validator has already found an error on the matchingControl
            return;
        }

        // set error on matchingControl if validation fails
        if (control.value !== matchingControl.value) {
            matchingControl.setErrors({ mustMatch: true });
        } else {
            matchingControl.setErrors(null);
        }
    }
  }

  public onSubmit() {
    if (this.currentAdmin) {
      this.adminService.editAdminInfo(this.adminForm.controls.name.value, this.adminForm.controls.surname.value,
        this.adminForm.controls.email.value, this.adminForm.controls.password.value).subscribe(editedAdminInfo => {
        this.currentAdmin = editedAdminInfo;
        this.adminForm.patchValue(this.currentAdmin);
      });
    } else {
      if (this.operatingAdmin.role === 'system' && this.adminForm.controls.role.value === 'OWNER') {
        this.adminService.createNewOwnerAdmin(this.adminForm.controls.name.value, this.adminForm.controls.surname.value, this.adminForm.controls.email.value).subscribe(res => {
          this.location.back();
        });
      } else {
        // tslint:disable-next-line:max-line-length
        this.adminService.createNewAdminForOrganization(this.adminForm.controls.name.value, this.adminForm.controls.surname.value, this.adminForm.controls.email.value, this.adminForm.controls.role.value, this.selectedOrganization.id).subscribe(res => {
          this.location.back();
        });
      }
    }
  }
}
