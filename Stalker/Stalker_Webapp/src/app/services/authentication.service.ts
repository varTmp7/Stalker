import {Injectable} from '@angular/core';

import {URL_BASE} from '../constants';
import {HttpClient} from '@angular/common/http';
import {Admin} from '../models/Admin';
import {BehaviorSubject, Observable} from 'rxjs';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthenticationService {
  private currentUserSubject: BehaviorSubject<Admin>;
  public currentUser: Observable<Admin>;

  constructor(private http: HttpClient) {
    this.currentUserSubject = new BehaviorSubject<Admin>(JSON.parse(localStorage.getItem('currentUser')));
    this.currentUser = this.currentUserSubject.asObservable();
  }

  public get currentUserValue(): Admin {
    return this.currentUserSubject.value;
  }

  public login(email: string, password: string) {
    const formData: FormData = new FormData();
    formData.append('email', email);
    formData.append('password', password);
    return this.http.post<any>(`${URL_BASE}/login`, formData)
      .pipe(map(user => {
        // store user details and jwt token in local storage to keep user logged in between page refreshes
        localStorage.setItem('currentUser', JSON.stringify(user));
        this.currentUserSubject.next(user);
        return user;
      }));
  }

  public logout() {
    // remove user from local storage to log user out
    localStorage.removeItem('currentUser');
    sessionStorage.removeItem('selectOrganization');
    this.currentUserSubject.next(null);
  }
}
