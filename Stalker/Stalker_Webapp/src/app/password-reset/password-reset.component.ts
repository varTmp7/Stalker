import { Component, OnInit } from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AdminService} from '../services/admin.service';
import {AuthenticationService} from '../services/authentication.service';
import {Router} from '@angular/router';

@Component({
  selector: 'app-password-reset',
  templateUrl: './password-reset.component.html',
  styleUrls: ['./password-reset.component.css']
})
export class PasswordResetComponent implements OnInit {
  public pwdForm: FormGroup;
  public isSent = false;
  public isErr = false;

  constructor(private formBuilder: FormBuilder, private adminService: AdminService, private authenticationService: AuthenticationService, private router: Router) {
    if (this.authenticationService.currentUserValue) {
      this.router.navigate(['/']);
    }
  }

  ngOnInit(): void {
    this.pwdForm = this.formBuilder.group({
      email: ['', Validators.required]
    });
  }

  public onSubmit() {
    if (this.pwdForm.invalid) {
      return;
    } else {
      this.adminService.resetPassword(this.pwdForm.value).subscribe(res => {this.isSent=true; this.isErr=false;}, err => {this.isErr=true; this.isSent=false;})
    }
  }

}
