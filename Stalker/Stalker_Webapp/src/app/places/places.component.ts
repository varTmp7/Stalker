import {Component, OnInit} from '@angular/core';
import {Place} from '../models/Place';
import {PlaceService} from '../place.service';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';

@Component({
  selector: 'app-places',
  templateUrl: './places.component.html',
  styleUrls: ['./places.component.css']
})
export class PlacesComponent implements OnInit {
  places: Place[];
  organizationId;

  constructor(private placeService: PlaceService, private route: ActivatedRoute, private location: Location) {
  }

  ngOnInit(): void {
    this.organizationId = +this.route.snapshot.paramMap.get('organization_id');
    this.getPlaces();
    //this.placeService.getPlaceDetail(1, 1).subscribe(res => console.log(res));
    //this.placeService.createNewPlace(2, 'nuovo Luogo', 100, [{longitude: 1.01, latitude: 1.01}, {longitude: 2.01, latitude: 2.01}, {longitude: 3.01, latitude: 3.01}, {longitude: 4.01, latitude: 4.01}]).subscribe(res => console.log(res));
    //this.placeService.editPlace(2, 4, 'nuovo Luogo ma diverso', 100, [{longitude: 1.01, latitude: 1.01}, {longitude: 2.01, latitude: 2.01}, {longitude: 3.01, latitude: 3.01}, {longitude: 4.01, latitude: 4.01}]).subscribe(res => console.log(res));
    //this.placeService.deletePlace(2, 4).subscribe(res => console.log(res));
  }

  private getPlaces(): void {
    this.placeService.getPlaces(this.organizationId).subscribe(placesRetrieved => this.places = placesRetrieved);
  }
}
