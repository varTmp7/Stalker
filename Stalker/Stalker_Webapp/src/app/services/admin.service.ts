import {Injectable} from '@angular/core';
import {AuthenticationService} from './authentication.service';
import {HttpClient} from '@angular/common/http';
import {Admin} from '../models/Admin';
import {URL_BASE} from '../constants';
import {Observable} from 'rxjs';
import {map, catchError} from 'rxjs/operators';
import { stringify } from 'querystring';

@Injectable({
  providedIn: 'root'
})
export class AdminService {

  constructor(private http: HttpClient, private authenticationService: AuthenticationService) {
  }

  private createEditAdminJSON(name: string, surname: string, email: string, password: string): JSON {
    const jsonString = `{
    "name": "${name}",
    "surname": "${surname}",
    "email": "${email}",
    "password": "${password}"
    }`;
    return JSON.parse(jsonString);
  }

  private createNewAdminJSON(name: string, surname: string, email: string, role: string, organization_id: number): JSON {
    const jsonString = `{
    "name": "${name}",
    "surname": "${surname}",
    "email": "${email}",
    "role": "${role}",
    "organization_id": "${organization_id.toString()}"
    }`;
    return JSON.parse(jsonString);
  }

  private createNewOwnerAdminJSON(name: string, surname: string, email: string): JSON {
    const jsonString = `{
    "name": "${name}",
    "surname": "${surname}",
    "email": "${email}",
    "role": "OWNER",
    "is_owner": "true"
    }`;
    return JSON.parse(jsonString);
  }

  private createEditRoleJSON(role: string) {
    const jsonString = `{"role": "${role}"}`;
    return JSON.parse(jsonString);
  }


  public getAllAdmins(): Observable<Admin[]> {
    return this.http.get<Admin[]>(URL_BASE + '/admins').pipe(map(res => {
      return res['admins'];
    }));
  }


  public getAdminsOfOrganizztion(organizationId: number): Observable<Admin[]> {
    return this.http.get<Admin[]>(URL_BASE + `/organizations/${organizationId}/admins`).pipe(map(res => {
      return res['admins'];
    }));
  }

  public getAdmin(): Observable<Admin> {
    return this.http.get<Admin>(URL_BASE + `/admins/${this.authenticationService.currentUserValue.id}`);
  }

  public editAdminInfo(name: string, surname: string, email: string, password: string): Observable<Admin> {
    return this.http.put<Admin>(URL_BASE + `/admins/${this.authenticationService.currentUserValue.id}`, this.createEditAdminJSON(name,
      surname, email, password));
  }

  public createNewAdminForOrganization(name: string, surname: string, email: string, role: string, organization_id: number): Observable<Admin> {
    return this.http.post<Admin>(URL_BASE + `/organizations/${organization_id}/admins`, this.createNewAdminJSON(name,
      surname, email, role, organization_id));
  }

  public createNewOwnerAdmin(name: string, surname: string, email: string): Observable<Admin> {
    return this.http.post<Admin>(URL_BASE + '/admins', {...this.createNewOwnerAdminJSON(name, surname, email)});
  }

  public changeAdminRole(organizationId: number, adminId: number, role: string) {
    return this.http.put(URL_BASE + `/organizations/${organizationId}/admins/${adminId}`, this.createEditRoleJSON(role));
  }

  public deleteAdmin(organizationId: number, adminId: number) {
    return this.http.delete(URL_BASE + `/organizations/${organizationId}/admins/${adminId}`);
  }

  public resetPassword(email: string) {
    const formData: FormData = new FormData();
    formData.append('email', email);
    return this.http.post<any>(`${URL_BASE}/reset-password`, formData);
  }

}
