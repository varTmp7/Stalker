import { Component, OnInit } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Place} from '../models/Place';
import {PlaceService} from '../place.service';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';


@Component({
  selector: 'app-placedetail',
  templateUrl: './placedetail.component.html',
  styleUrls: ['./placedetail.component.css']
})
export class PlacedetailComponent implements OnInit {
  place:Place;
  editForm: FormGroup;

  constructor(private fb: FormBuilder, private placeService: PlaceService, private route: ActivatedRoute, private location: Location) { }

  ngOnInit(){

  const coordinate = this.fb.group({
      latitude:'',
      longitude:''
    })

  this.getPlaceDetail();
    this.editForm = this.fb.group({
      approved:'',
      coordinates: this.fb.group({
        coordinate1: this.fb.group({
          latitude:'',
          longitude:''
        }),
        coordinate2: this.fb.group({
          latitude:'',
          longitude:''
        }),
        coordinate3: this.fb.group({
          latitude:'',
          longitude:''
        }),
        coordinate4: this.fb.group({
          latitude:'',
          longitude:''
        })
      }),
      name:'',
      num_max_people:'',
      number_of_people:'',
      organization_id:''
    })

  }

  private getPlaceDetail(): void {
      const organizationId = +this.route.snapshot.paramMap.get('organization_id');
      const placeId = +this.route.snapshot.paramMap.get('place_id');
      this.placeService.getPlaceDetail(organizationId,placeId).subscribe((res: Place) => {this.editForm.patchValue(res);});;
    }
}
