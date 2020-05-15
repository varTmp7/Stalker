import {Component, OnInit, ViewChild} from '@angular/core';
import {AuthenticationService} from '../services/authentication.service';
import {Router} from '@angular/router';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {map, shareReplay} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {Admin} from '../models/Admin';
import {Organization} from '../models/Organization';
import {OrganizationService} from '../services/organization.service';
import {DataService} from '../services/data.service';

@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class DashboardComponent implements OnInit {

  public currentAdmin: Admin;
  public organizations: Organization[];
  public selectedOrganization: Organization;
  public selectedOrganizationId = 0;


  constructor(private authenticationService: AuthenticationService,
              private organizationService: OrganizationService,
              private router: Router,
              private breakpointObserver: BreakpointObserver,
              private dataService: DataService) {
  }

  public isHandset$: Observable<boolean> = this.breakpointObserver.observe(Breakpoints.Handset)
    .pipe(
      map(result => result.matches),
      shareReplay()
    );

  ngOnInit(): void {
    this.currentAdmin = this.authenticationService.currentUserValue;
    this.getOrganizations();
    this.dataService.changeSelectedOrganization.subscribe(newOrganization => {
      if (this.organizations && this.organizations.find((el) => el.id === newOrganization.id) === undefined) {
        this.getOrganizations();
      }
    });
  }


  private getOrganizations() {
    if (this.currentAdmin.role !== 'system') {
      this.organizationService.getOrganizations().subscribe(organizations => {
        this.organizations = organizations;
        try {
          this.selectedOrganization = this.organizations.find(el => el.id === JSON.parse(sessionStorage.getItem('selectOrganization')).id);
          this.selectedOrganizationId = this.selectedOrganization.id;
        } catch (e) {
          console.log('new page');
        }
      });
    }
  }

  public logout() {
    this.authenticationService.logout();
    this.router.navigate(['/login']);
  }

  public deleteOrganization() {
    this.organizationService.deleteOrganization(this.selectedOrganization.id).subscribe(res => {
      const indexInArray = this.organizations.indexOf(this.selectedOrganization);
      this.organizations.splice(indexInArray, 1);
      this.selectedOrganization = undefined;
    });
  }

  public handleOnChangeOrganization(event) {
    if (event.value === '-1' && this.currentAdmin.role === 'owner') {
      sessionStorage.removeItem('selectOrganization');
      this.selectedOrganization = undefined;
    } else {
      this.organizationService.selectOrganization(event.value).subscribe(newAdmin => {
        this.currentAdmin = newAdmin;
        this.selectedOrganization = this.organizations.find(el => el.id === parseInt(event.value));
        this.dataService.changeOrganization(this.selectedOrganization);
        this.router.navigate(['']);
      });
    }
  }
}
