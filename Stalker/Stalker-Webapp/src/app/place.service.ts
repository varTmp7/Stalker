import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {handleConnectionError, URL_BASE} from './constants';
import {catchError, map} from 'rxjs/operators';
import {Observable} from 'rxjs';
import {Coordinate, Place} from './models/Place';


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
    return this.http.get<Place[]>(URL_BASE + `/organizations/${organizationId}/places`).pipe(map(res => res['places']),
      catchError(handleConnectionError));
  }

  getPlaceDetail(organizationId: number, placeId: number): Observable<Place> {
    return this.http.get<Place>(URL_BASE + `/organizations/${organizationId}/places/${placeId}`).pipe(catchError(handleConnectionError));
  }

  createNewPlace(organizationId: number, name: string, numMaxPeople: number, coordinates: Coordinate[]): Observable<Place> {
    const newPlace = this.createPlaceJSON(organizationId, name, numMaxPeople, coordinates);
    return this.http.post<Place>(URL_BASE + `/organizations/${organizationId}/places`, newPlace).pipe(catchError(handleConnectionError));
  }

  deletePlace(organizationId: number, placeId: number): Observable<{}> {
    return this.http.delete(URL_BASE + `/organizations/${organizationId}/places/${placeId}`).pipe(catchError(handleConnectionError));
  }

  editPlace(organizationId: number, placeId: number, name: string, numMaxPeople: number, coordinates: Coordinate[]): Observable<Place> {
    const editedPlace = this.createPlaceJSON(organizationId, name, numMaxPeople, coordinates);
    return this.http.put<Place>(URL_BASE + `/organizations/${organizationId}/places/${placeId}`, editedPlace).pipe(catchError(handleConnectionError));
  }
}
