import {Component, OnInit} from '@angular/core';
import {FormArray, FormBuilder, FormGroup} from '@angular/forms';
import {Coordinate, Place} from '../models/Place';
import {DataService} from '../services/data.service';
import {Organization} from '../models/Organization';
import {ActivatedRoute, Router} from '@angular/router';
import {PlaceService} from '../services/place.service';
import {Location} from '@angular/common';
import {HttpErrorResponse} from "@angular/common/http";
import {MatSnackBar} from "@angular/material/snack-bar";
import {LatLng} from "leaflet";


@Component({
  selector: 'app-place-detail',
  templateUrl: './place-detail.component.html',
  styleUrls: ['./place-detail.component.css']
})
export class PlaceDetailComponent implements OnInit {

  public organization: Organization;
  public place: Place;
  public placeForm: FormGroup;



  get coordinates(): FormArray {
    return this.placeForm.get('coordinates') as FormArray;
  }

  constructor(private formBuilder: FormBuilder,
              private dataService: DataService,
              private activatedRoute: ActivatedRoute,
              private placeService: PlaceService,
              private location: Location,
              private snackBar: MatSnackBar) {

  }

  ngOnInit(): void {
    this.placeForm = this.formBuilder.group({
      coordinates: this.formBuilder.array([
        this.addCoordinateFormGroup(),
        this.addCoordinateFormGroup(),
        this.addCoordinateFormGroup(),
        this.addCoordinateFormGroup(),
      ]),
      name: '',
      num_max_people: '',
    });

    this.organization = JSON.parse(sessionStorage.getItem('selectOrganization'));
    if (this.activatedRoute.snapshot.url[2] && this.activatedRoute.snapshot.url[2].path === 'place-list') {
      this.getplace();
    } else {
      this.place = undefined;
    }
  }

  private getplace() {
    const placeId = this.activatedRoute.snapshot.paramMap.get('place_id');
    this.placeService.getPlaceDetail(this.organization.id, placeId).subscribe(placeDetail => {
      this.place = placeDetail;
      this.placeForm.patchValue(this.place);
    });
  }

  private addCoordinateFormGroup() {
    return this.formBuilder.group({
      latitude: '',
      longitude: ''
    });
  }

  onMapChanged(coordinates: LatLng[]) {
    coordinates.forEach((coordinate, index) => {
      // @ts-ignore
      this.placeForm.controls.coordinates.controls[index].setValue({latitude: coordinate.lat,
        longitude: coordinate.lng});
    });
  }

  onSubmit() {
    if (this.place === undefined) {

      this.placeService.createNewPlace(this.organization.id, this.placeForm.controls.name.value, this.placeForm.controls.num_max_people.value,
        [
          new Coordinate(this.placeForm.controls.coordinates.value[0].latitude, this.placeForm.controls.coordinates.value[0].longitude),
          new Coordinate(this.placeForm.controls.coordinates.value[1].latitude, this.placeForm.controls.coordinates.value[1].longitude),
          new Coordinate(this.placeForm.controls.coordinates.value[2].latitude, this.placeForm.controls.coordinates.value[2].longitude),
          new Coordinate(this.placeForm.controls.coordinates.value[3].latitude, this.placeForm.controls.coordinates.value[3].longitude),
        ]).subscribe(newPlace => {
        this.place = newPlace;
        this.placeForm.patchValue(this.place);
        this.location.back();
      }, (error: HttpErrorResponse) => {
        if (error.status === 403) {
          console.log("Troppo grande");
          this.snackBar.open('Organization reached maximum place areas. Contact a System Admin to increase this number');
        }
      });
    } else {
      const placeId = this.activatedRoute.snapshot.paramMap.get('place_id');
      this.placeService.editPlace(this.organization.id,
        placeId,
        this.placeForm.controls.name.value, this.placeForm.controls.num_max_people.value,
        [
          new Coordinate(this.placeForm.controls.coordinates.value[0].latitude, this.placeForm.controls.coordinates.value[0].longitude),
          new Coordinate(this.placeForm.controls.coordinates.value[1].latitude, this.placeForm.controls.coordinates.value[1].longitude),
          new Coordinate(this.placeForm.controls.coordinates.value[2].latitude, this.placeForm.controls.coordinates.value[2].longitude),
          new Coordinate(this.placeForm.controls.coordinates.value[3].latitude, this.placeForm.controls.coordinates.value[3].longitude)]).subscribe(editedPlace => {
        this.location.back();
      }, (error: HttpErrorResponse) => {
        if (error.status === 403) {
          console.log("Troppo grande");
          this.snackBar.open('Organization reached maximum place areas. Contact a System Admin to increase this number');
        }
      });
    }
  }

}
