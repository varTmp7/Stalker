import { Injectable } from '@angular/core';
import {HttpClient, HttpErrorResponse, HttpHeaders, HttpResponse} from '@angular/common/http';
import {URL_BASE, handleConnectionError} from './constants';
import {Administrator} from './models/Administrator';
import {Observable, throwError} from 'rxjs';
import {catchError, map, tap} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AdministratorService{

   constructor(private http: HttpClient) {
    }

    private createAdministratorJson(firstName: string, lastName:string, email:string, password:string, role:string): JSON {
          const jsonString = `{"firstName": "${firstName}",
                                    "lastName": "${lastName}",
                                    "email": "${email}",
                                    "password": "${password}",
                                    "role": "${role}"}`;
            return JSON.parse(jsonString);
    }

    getAdministrators(): Observable<Administrator[]> {
          return this.http.get<Administrator[]>(URL_BASE + '/administrators').pipe(map(res => res['administrators']),
              catchError(handleConnectionError));
    }

}
