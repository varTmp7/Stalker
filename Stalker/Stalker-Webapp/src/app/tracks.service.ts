import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Track} from './models/Track';
import {catchError, map} from 'rxjs/operators';
import {handleConnectionError, URL_BASE} from './constants';

@Injectable({
  providedIn: 'root'
})
export class TracksService {

  constructor(private http: HttpClient) { }

  getTracks(organizationId: number, placeId): Observable<Track[]> {
    return this.http.get<Track[]>(URL_BASE + `/organizations/${organizationId}/places/${placeId}/tracks`).pipe(map(res => res['tracks']), catchError(handleConnectionError));
  }
}
