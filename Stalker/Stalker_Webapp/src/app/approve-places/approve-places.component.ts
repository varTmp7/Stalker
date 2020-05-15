import {AfterViewInit, Component, Inject, OnInit} from '@angular/core';
import {PlaceService} from '../services/place.service';
import {OrganizationsPlacesToApprove, SimpleOrganization} from '../models/OrganizationsPlacesToApprove';
import {Place} from '../models/Place';
import {MatTableDataSource} from '@angular/material/table';
import * as L from 'leaflet';

@Component({
  selector: 'app-approve-places',
  templateUrl: './approve-places.component.html',
  styleUrls: ['./approve-places.component.css']
})
export class ApprovePlacesComponent implements OnInit, AfterViewInit {
  maps: any[] = [];

  organizationsPlacesToApprove: (Place | SimpleOrganization)[] = [];
  organizationsPlacesToApproveData = new MatTableDataSource(this.organizationsPlacesToApprove);
  displayedColumns: string[] = ['name', 'map', 'approve'];

  constructor(private placeService: PlaceService) {
  }


  ngOnInit(): void {
    this.placeService.getPlaceToApprove().subscribe(res => {
      for (const organization of res) {
        this.organizationsPlacesToApprove.push(new SimpleOrganization(organization.id, organization.name));
        for (const place of organization.places) {
          this.organizationsPlacesToApprove.push(place);
        }
      }
      this.organizationsPlacesToApproveData.data = this.organizationsPlacesToApprove;
      setTimeout(() => this.initMap(), 1);
    });
  }

  ngAfterViewInit(): void {

  }

  private initMap(): void {
    this.organizationsPlacesToApprove.forEach((value, index) => {
      if (!(value instanceof SimpleOrganization)) {
        console.log(value);
        const polygon = L.polygon([
          [value.coordinates[0].latitude, value.coordinates[0].longitude],
          [value.coordinates[1].latitude, value.coordinates[1].longitude],
          [value.coordinates[2].latitude, value.coordinates[2].longitude],
          [value.coordinates[3].latitude, value.coordinates[3].longitude],
        ]);
        this.maps.push( L.map('map' + value.id).setView(polygon.getBounds().getCenter(), 17));
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
          attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        }).addTo(this.maps[this.maps.length - 1]);
        polygon.addTo(this.maps[this.maps.length - 1]);
      }
    });
  }

  isOrganization(index, item) {
    return item instanceof SimpleOrganization;
  }

  approvePlace(element: Place) {
    const elementIndex = this.organizationsPlacesToApproveData.data.indexOf(element);
    let organizationId: number;

    let index = elementIndex;
    while (index >= 0) {
      if (this.organizationsPlacesToApproveData.data[index] instanceof SimpleOrganization) {
        organizationId = (this.organizationsPlacesToApproveData.data[index] as SimpleOrganization).id;
        break;
      }
      index--;
    }
    const placeId = element.id;

    this.placeService.approvePlace(organizationId, placeId).subscribe(res => {
      this.organizationsPlacesToApprove.splice(elementIndex, 1);
      let firstSimpleOrg = false;
      if (this.organizationsPlacesToApprove.length === 1) {
        this.organizationsPlacesToApprove.splice(0, 1);
      } else {
        for (let indexToCheck = 0; indexToCheck < this.organizationsPlacesToApprove.length; indexToCheck++) {
          if (this.organizationsPlacesToApprove[indexToCheck] instanceof SimpleOrganization) {
            if (firstSimpleOrg) {
              this.organizationsPlacesToApprove.slice(indexToCheck, 1);
              break;
            } else {
              firstSimpleOrg = true;
            }
          } else {
            firstSimpleOrg = false;
          }
        }
      }
      this.organizationsPlacesToApproveData = new MatTableDataSource<Place | SimpleOrganization>(this.organizationsPlacesToApprove);
    });
  }
}
