import { Component, OnInit } from '@angular/core';
import {Administrator} from '../models/Administrator';
import { ActivatedRoute, ParamMap } from '@angular/router';
import { R3TargetBinder } from '@angular/compiler';
//import { switchMap } from 'rxjs/operators';

@Component({
  selector: 'app-administrators-list',
  templateUrl: './administrators-list.component.html',
  styleUrls: ['./administrators-list.component.css']
})
export class AdministratorsListComponent implements OnInit {
  organizationID;
  modify = false;
  administrators ;

  constructor(
    private route: ActivatedRoute,
  ) { }

  ngOnInit(): void {   
      this.organizationID = +this.route.snapshot.paramMap.get('organizationID');
      this.getAdministrators();
  }

  private getAdministrators(){
  
  }

  onChange(event){
    const role = event.value; //nuovo ruolo
    this.modify = true;
    /*
    modifica il ruolo dell'administratore
    */
  }
   
  save(){
    this.modify = false;
  }

}