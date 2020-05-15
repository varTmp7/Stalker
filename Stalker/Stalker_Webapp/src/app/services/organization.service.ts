import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {Observable, throwError} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';
import {Organization, OrganizationType} from '../models/Organization';
import {URL_BASE} from '../constants';
import {Admin} from '../models/Admin';

@Injectable({
  providedIn: 'root'
})
export class OrganizationService {

  constructor(private http: HttpClient) {
  }

  // tslint:disable-next-line:max-line-length
  private createOrganizationJson(name: string, imgUrl:string, address: string, city: string, region: string, postalCode: string, nation: string, phoneNumber: string, email: string, type: OrganizationType, ldapUrl?: string, ldapPort?: number, ldapCommonName?: string, ldapDomainComponent?: string): JSON {
    const jsonStringBase = `{"name": "${name}",
                            "address": "${address}",
                            "image": "${imgUrl}",
                            "city": "${city}",
                            "region": "${region}",
                            "postal_code": "${postalCode}",
                            "phone_number": "${phoneNumber}",
                            "nation": "${nation}",
                            "email": "${email}",
                            "type": "${type.toString().toUpperCase()}"
                            }`;

    var returnJSON = JSON.parse(jsonStringBase);
    if (type !== 'public') {
      const LDAPJsonString = `{"ldap_url": "${ldapUrl}",
      "ldap_port": "${ldapPort}",
      "ldap_common_name": "${ldapCommonName}",
      "ldap_domain_component": "${ldapDomainComponent}"}`;
      returnJSON = {...returnJSON, ...JSON.parse(LDAPJsonString)};
    }

    return returnJSON;
  }


  getOrganizations(): Observable<Organization[]> {
    return this.http.get<Organization[]>(URL_BASE + '/organizations-admin').pipe(map(res => {
      return res['organizations'];
    }));
  }

  public selectOrganization(organizationId: number): Observable<Admin> {
    return this.http.get<Admin>(URL_BASE + `/select-organization?organization_id=${organizationId}`).pipe(map(admin => {
      localStorage.setItem('currentUser', JSON.stringify(admin));
      return admin;
    }));
  }

  getOrganizationDetail(organizationId: number): Observable<Organization> {
    return this.http.get<Organization>(URL_BASE + `/organizations/${organizationId}`);
  }

  // tslint:disable-next-line:max-line-length
  createNewOrganization(name: string, imgUrl:string, address: string, city: string, region: string, postalCode: string, nation: string, phoneNumber: string, email: string, type: OrganizationType, ldapUrl?: string, ldapPort?: number, ldapCommonName?: string, ldapDomainComponent?: string): Observable<Organization> {
    const newOrganizationJson = this.createOrganizationJson(name,
      imgUrl,
      address,
      city,
      region,
      postalCode,
      nation,
      phoneNumber,
      email,
      type,
      ldapUrl,
      ldapPort,
      ldapCommonName,
      ldapDomainComponent);
    return this.http.post<Organization>(URL_BASE + '/organizations', newOrganizationJson).pipe(tap(res => {
      const currentAdmin = JSON.parse(localStorage.getItem('currentUser')) as Admin;
      currentAdmin.max_quota_organizations--;
      sessionStorage.setItem('currentUser', JSON.stringify(currentAdmin));
    }), catchError((err: HttpErrorResponse) => {
      return throwError(err);
    }));
  }

  deleteOrganization(organizationId: number): Observable<{}> {
    sessionStorage.removeItem('selectOrganization');
    return this.http.delete(URL_BASE + `/organizations/${organizationId}`).pipe(tap(res => {
      const currentAdmin = JSON.parse(localStorage.getItem('currentUser')) as Admin;
      currentAdmin.max_quota_organizations++;
      sessionStorage.setItem('currentUser', JSON.stringify(currentAdmin));
    }));
  }

  // tslint:disable-next-line:max-line-length
  editOrganization(organizationId: number, name: string, imgUlr: string, address: string, city: string, region: string, postalCode: string, nation: string, phoneNumber: string, email: string, type: OrganizationType, ldapUrl?: string, ldapPort?: number, ldapCommonName?: string, ldapDomainComponent?: string): Observable<Organization> {
    const editedOrganization = this.createOrganizationJson(name,
      imgUlr,
      address,
      city,
      region,
      postalCode,
      nation,
      phoneNumber,
      email,
      type,
      ldapUrl,
      ldapPort,
      ldapCommonName,
      ldapDomainComponent);
    return this.http.put<Organization>(URL_BASE + `/organizations/${organizationId}`, editedOrganization);
  }

}
