import {EventEmitter, Injectable, Output} from '@angular/core';
import {BehaviorSubject, Observable} from 'rxjs';
import {Organization} from '../models/Organization';

@Injectable({
  providedIn: 'root'
})
export class DataService {
  public selectedOrganizationSource = new BehaviorSubject(null);
  selectedOrganization = this.selectedOrganizationSource.asObservable();

  @Output() changeSelectedOrganization: EventEmitter<Organization> = new EventEmitter<Organization>();

  constructor() {
  }

  changeOrganization(organization: Organization) {
    sessionStorage.setItem('selectOrganization', JSON.stringify(organization));
    this.selectedOrganizationSource.next(organization);
    this.changeSelectedOrganization.emit(organization);
  }
}
