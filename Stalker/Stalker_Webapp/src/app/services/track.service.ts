import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {Track} from '../models/Track';
import {URL_BASE} from '../constants';
import {map} from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class TrackService {

  constructor(private http: HttpClient) {
  }

  getTracks(organizationId: number, placeId: string): Observable<Track[]> {
    return this.http.get<Track[]>(URL_BASE + `/organizations/${organizationId}/places/${placeId}/tracks`).pipe(map(res => {
      console.log(res);
      return res['tracks'];
    }));
  }

  getTracksForOrganization(organizationId: number): Observable<Track[]> {
    return this.http.get<Track[]>(URL_BASE + `/organizations/${organizationId}/tracks`).pipe(map(res => {
      return res['tracks'];
    }));
  }
}
