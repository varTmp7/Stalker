import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {Organization} from '../models/Organization';
import {PlaceService} from '../services/place.service';
import {MatSort} from '@angular/material/sort';
import {DataService} from '../services/data.service';
import {Place} from '../models/Place';
import {Admin} from '../models/Admin';
import {AuthenticationService} from '../services/authentication.service';
import {interval} from 'rxjs';
import {switchMap} from 'rxjs/operators';
import {Router} from '@angular/router';

@Component({
  selector: 'app-places-list',
  templateUrl: './places-list.component.html',
  styleUrls: ['./places-list.component.css']
})
export class PlacesListComponent implements OnInit {
  organization: Organization;

  places = new MatTableDataSource([]);
  placesData = Object.assign(this.places.data);
  displayedColumns: string[] = ['name', 'number_of_people', 'manage', 'delete'];
  currentAdmin: Admin;

  @ViewChild(MatSort, {static: true}) sort: MatSort;

  constructor(private placeService: PlaceService, private authenticationService: AuthenticationService, private dataService: DataService, private router: Router) {
    this.dataService.selectedOrganization.subscribe(selectedOrganization => {
      this.organization = JSON.parse(sessionStorage.getItem('selectOrganization'));
    });
  }

  ngOnInit(): void {
    this.currentAdmin = this.authenticationService.currentUserValue;
    this.organization = JSON.parse(sessionStorage.getItem('selectOrganization'));
    // Polling solution very bad solution
    interval(5000).pipe(switchMap(() => this.placeService.getPlaces(this.organization.id))).subscribe(placesList => {
      // console.log('polled places');
      this.places.data = placesList;
      this.places.sort = this.sort;
    });
    this.getPlaces();
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.places.filter = filterValue.trim().toLowerCase();
  }

  deletePlace(place: Place) {
    this.placesData = Object.assign(this.places.data);
    this.placeService.deletePlace(this.organization.id, place.id).subscribe(res => {
      const index = this.places.data.indexOf(place);
      this.placesData.splice(index, 1);
      this.places = new MatTableDataSource<Place>(this.placesData);
    });
  }

  private getPlaces() {
    this.placeService.getPlaces(this.organization.id).subscribe(placesList => {
      this.places.data = placesList;
      this.places.sort = this.sort;
    });
  }

  getColor(act: number, tot: number) {
    let perc : number = (act/tot > 1 ? 1 : act/tot);
    let hue=((1-perc)*120).toString(10);
    return ["hsl(",hue,",100%,50%)"].join("");
  }

}
