import { Component, OnInit } from '@angular/core';
import {FormControl,FormBuilder,Validators, FormGroup} from '@angular/forms';
import {Location} from '@angular/common';
import {ActivatedRoute} from '@angular/router';
import { AdministratorService } from '../administrator.service';

@Component({
  selector: 'app-new-administrator',
  templateUrl: './new-administrator.component.html',
  styleUrls: ['./new-administrator.component.css']
})
export class NewAdministratorComponent implements OnInit {

  editForm : FormGroup;

  constructor(
    private formBuilder: FormBuilder,
    private adminService: AdministratorService,
    private location: Location,
    private route:ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.editForm = this.formBuilder.group({
      firstname: '',
      lastname: '',
      email: '',
      role:''
    });
  }

  onSubmit(newAdmin) {
    
  }

}
