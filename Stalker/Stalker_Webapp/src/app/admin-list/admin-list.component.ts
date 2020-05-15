import {Component, OnInit, ViewChild} from '@angular/core';
import {Admin} from '../models/Admin';
import {AdminService} from '../services/admin.service';
import {AuthenticationService} from '../services/authentication.service';
import {Router} from '@angular/router';
import {Organization} from '../models/Organization';
import {DataService} from '../services/data.service';
import {MatTableDataSource} from '@angular/material/table';
import {MatSort} from '@angular/material/sort';

@Component({
  selector: 'app-admin-list',
  templateUrl: './admin-list.component.html',
  styleUrls: ['./admin-list.component.css']
})
export class AdminListComponent implements OnInit {

  admins = new MatTableDataSource<Admin>([]);
  adminsData = Object.assign(this.admins.data);
  displayedColumns: string[] = ['name', 'surname', 'email', 'role', 'delete'];
  selectedOrganization: Organization;

  @ViewChild(MatSort, {static: true}) sort: MatSort;

  constructor(private adminService: AdminService, private authenticationService: AuthenticationService, private router: Router, private dataService: DataService) {
    this.dataService.selectedOrganization.subscribe(changedOrganization => {
      this.selectedOrganization = changedOrganization;
      this.getAdmins();
    });
  }

  ngOnInit(): void {
    this.getAdmins();
    this.admins.sort = this.sort;
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.admins.filter = filterValue.trim().toLowerCase();
  }

  changedRole(event) {
    const adminInfo = JSON.parse(event.value);
    this.adminService.changeAdminRole(this.selectedOrganization.id, adminInfo.id, adminInfo.role).subscribe(res => {
    });
  }

  compareRole(element: any, optionValue: any): boolean {
    return JSON.parse(element).role === optionValue;
  }

  deleteAdmin(admin) {
    this.adminsData = Object.assign(this.admins.data);
    this.adminService.deleteAdmin(this.selectedOrganization.id, admin.id).subscribe(res => {
      const index = this.admins.data.indexOf(admin);
      this.adminsData.splice(index, 1);
      this.admins = new MatTableDataSource<Admin>(this.adminsData);
    });
  }

  private getAdmins() {
    if (this.authenticationService.currentUserValue.role === 'system') {
      this.adminService.getAllAdmins().subscribe(adminsList => {
        this.admins.data = adminsList;
      });
    } else {
      this.adminService.getAdminsOfOrganizztion(this.selectedOrganization.id).subscribe(adminsList => {
        this.admins.data = adminsList;
      });
    }
  }
}
