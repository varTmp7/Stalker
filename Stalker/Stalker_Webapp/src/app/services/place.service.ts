import {Injectable} from '@angular/core';
import {HttpClient, HttpErrorResponse} from '@angular/common/http';
import {catchError, map} from 'rxjs/operators';
import {Observable, throwError} from 'rxjs';
import {Coordinate, Place} from '../models/Place';
import {URL_BASE} from '../constants';
import {OrganizationsPlacesToApprove} from "../models/OrganizationsPlacesToApprove";


@Injectable({
  providedIn: 'root'
})
export class PlaceService {
  constructor(private http: HttpClient) {
  }

  private createPlaceJSON(organizationId: number, name: string, numMaxPeople: number, coordinates: Coordinate[]): JSON {
    const jsonString = `{"name": "${name}",
    "num_max_people": "${numMaxPeople}",
    "organization_id": "${organizationId}",
    "coordinates": [
      {"latitude": ${coordinates[0].latitude}, "longitude": ${coordinates[0].longitude}},
      {"latitude": ${coordinates[1].latitude}, "longitude": ${coordinates[1].longitude}},
      {"latitude": ${coordinates[2].latitude}, "longitude": ${coordinates[2].longitude}},
      {"latitude": ${coordinates[3].latitude}, "longitude": ${coordinates[3].longitude}}
      ]
    }`;
    return JSON.parse(jsonString);
  }

  getPlaces(organizationId: number): Observable<Place[]> {
    return this.http.get<Place[]>(URL_BASE + `/organizations/${organizationId}/places`).pipe(map(res => {
      return res['places'];
    }));
  }

  getPlaceDetail(organizationId: number, placeId: string): Observable<Place> {
    return this.http.get<Place>(URL_BASE + `/organizations/${organizationId}/places/${placeId}`);
  }

  createNewPlace(organizationId: number, name: string, numMaxPeople: number, coordinates: Coordinate[]): Observable<Place> {
    const newPlace = this.createPlaceJSON(organizationId, name, numMaxPeople, coordinates);
    return this.http.post<Place>(URL_BASE + `/organizations/${organizationId}/places`, newPlace).pipe(
      catchError((err: HttpErrorResponse) => {
        return throwError(err);
      }));
  }

  deletePlace(organizationId: number, placeId: number): Observable<{}> {
    return this.http.delete(URL_BASE + `/organizations/${organizationId}/places/${placeId}`);
  }

  editPlace(organizationId: number, placeId: string, name: string, numMaxPeople: number, coordinates: Coordinate[]): Observable<Place> {
    const editedPlace = this.createPlaceJSON(organizationId, name, numMaxPeople, coordinates);
    return this.http.put<Place>(URL_BASE + `/organizations/${organizationId}/places/${placeId}`, editedPlace).pipe(
      catchError((err: HttpErrorResponse) => {
        return throwError(err);
      }));
  }

  getPlaceToApprove(): Observable<OrganizationsPlacesToApprove[]> {
    return this.http.get<OrganizationsPlacesToApprove[]>(URL_BASE + '/place-to-approve').pipe(map(res => {
      return res['organizations'];
    }));
  }

  approvePlace(organizationId: number, placeId: number): Observable<{}> {
    return this.http.post(URL_BASE + `/organizations/${organizationId}/places/${placeId}/approve`,
      JSON.parse('{"approved": true}'));
  }

}
