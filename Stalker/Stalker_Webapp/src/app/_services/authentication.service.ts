import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {BehaviorSubject, Observable} from 'rxjs';
import {map} from 'rxjs/operators';

import {environment} from '../../environments/environment';
import {Administrator} from '../models/Administrator';
import {URL_BASE} from '../constants';

@Injectable({providedIn: 'root'})
export class AuthenticationService {
  private currentUserSubject: BehaviorSubject<Administrator>;
  public currentUser: Observable<Administrator>;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<Administrator>(JSON.parse(localStorage.getItem('currentUser')));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): Administrator {
    return this.currentUserSubject.value;
  }

  login(email: string, password: string) {
    const formData: FormData = new FormData();
    formData.append('email', email);
    formData.append('password', password)
    return this.http.post<any>(`${URL_BASE}/login`, formData)
      .pipe(map(user => {
        // store user details and jwt token in local storage to keep user logged in between page refreshes
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
        return user;
      }));
  }

  logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
  }
}
