import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse} from '@angular/common/http';
import {URL_BASE, handleConnectionError} from './constants';
import {Organization, OrganizationType} from './models/Organization';
import {Observable, throwError} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class OrganizationService {

  constructor(private http: HttpClient) {
  }

  // tslint:disable-next-line:max-line-length
  private createOrganizationJson(name: string, address: string, city: string, region: string, postalCode: string, nation: string, phoneNumber: string, email: string, type: OrganizationType, ldapUrl?: string, ldapPort?: number, ldapCommonName?: string, ldapDomainComponent?: string): JSON {
    const jsonString = `{"name": "${name}",
                            "address": "${address}",
                            "city": "${city}",
                            "region": "${region}",
                            "postal_code": "${postalCode}",
                            "phone_number": "${phoneNumber}",
                            "nation": "${nation}",
                            "email": "${email}",
                            "type": "${type.toString().toUpperCase()}",
                            "ldap_url": "${ldapUrl}",
                            "ldap_port": "${ldapPort}",
                            "ldap_common_name": "${ldapCommonName}",
                            "ldap_domain_component": "${ldapDomainComponent}"}`;
    return JSON.parse(jsonString);
  }

  getOrganizations(): Observable<Organization[]> {
      return this.http.get<Organization[]>(URL_BASE + '/organizations').pipe(map(res => res['organizations']),
        catchError(handleConnectionError));
  }

  getOrganizationDetail(organizationId: number): Observable<Organization> {
    return this.http.get<Organization>(URL_BASE + `/organizations/${organizationId}`).pipe(catchError(handleConnectionError));
  }

  // tslint:disable-next-line:max-line-length
  createNewOrganization(name: string, address: string, city: string, region: string, postalCode: string, nation: string, phoneNumber: string, email: string, type: OrganizationType, ldapUrl?: string, ldapPort?: number, ldapCommonName?: string, ldapDomainComponent?: string): Observable<Organization>{
    const newOrganizationJson = this.createOrganizationJson(name,
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
    return this.http.post<Organization>(URL_BASE + '/organizations', newOrganizationJson).pipe(catchError(handleConnectionError));
  }

  deleteOrganization(organizationId: number): Observable<{}> {
    return this.http.delete(URL_BASE + `/organizations/${organizationId}`).pipe(catchError(handleConnectionError));
  }

  // tslint:disable-next-line:max-line-length
  editOrganization(organizationId: number, name: string, address: string, city: string, region: string, postalCode: string, nation: string,  phoneNumber: string, email: string, type: OrganizationType, ldapUrl?: string, ldapPort?: number, ldapCommonName?: string, ldapDomainComponent?: string): Observable<Organization> {
    const editedOrganization = this.createOrganizationJson( name,
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
    return this.http.put<Organization>(URL_BASE + `/organizations/${organizationId}`, editedOrganization).pipe(catchError(handleConnectionError));
  }
}
