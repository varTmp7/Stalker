import { Component, OnInit } from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {FormBuilder, FormGroup} from '@angular/forms';
import {Place} from '../models/Place';
import {PlaceService} from '../place.service';
import {HttpClient} from '@angular/common/http';
import {ActivatedRoute} from '@angular/router';
import {Location} from '@angular/common';

@Component({
  selector: 'app-newplace',
  templateUrl: './newplace.component.html',
  styleUrls: ['./newplace.component.css']
})
export class NewplaceComponent implements OnInit {
  place:Place;
  editForm: FormGroup;

  constructor(private fb: FormBuilder, private placeService: PlaceService, private route: ActivatedRoute, private location: Location) { }

  ngOnInit(): void {
    const coordinate = this.fb.group({
      latitude:'',
      longitude:''
    });
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
    });

  }

  createPlace(place){
    const organizationId = +this.route.snapshot.paramMap.get('organization_id');
    this.placeService.createNewPlace(organizationId, place.name, place.num_max_people, place.coordinates).subscribe(res => {
    this.location.back(); }, (error => { this.location.back();}));
  
  }

}
