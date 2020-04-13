import { Component, OnInit } from '@angular/core';
import {FormControl,FormBuilder,Validators, FormGroup} from '@angular/forms';
import {Location} from '@angular/common';
import { AdministratorService } from '../administrator.service';

@Component({
  selector: 'app-administrator-details',
  templateUrl: './administrator-details.component.html',
  styleUrls: ['./administrator-details.component.css']
})
export class AdministratorDetailsComponent implements OnInit {
  editForm : FormGroup;
  user;
  showField=false;
  constructor(
    private fb: FormBuilder,
    private location: Location,
    private adminService: AdministratorService
  ) { }

  ngOnInit(): void {
    this.getUser();
    this.editForm = this.fb.group({
      firstname: '',//this.user.firstname,
      lastname: '',//this.user.lastname,
      password: '',
      email: ''//this.user.email,
    });
  }
  private getUser(){

  }
  
  onClick(){
    this.showField = !this.showField ? true : false;
    if(this.showField){
      this.editForm.get('password').setValidators([Validators.required]);
      this.editForm.get('password').updateValueAndValidity();
    }else {
      this.editForm.get('password').clearValidators();
      this.editForm.get('password').updateValueAndValidity();
      this.editForm.get('password').setValue('');
    }
  }
  editUser(user){
    //if password is edited password equals editted value else password remains old password
    user.password = user.password === "" ? this.user.password : user.password;
  }

}
