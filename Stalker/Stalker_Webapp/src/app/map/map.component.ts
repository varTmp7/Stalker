import {AfterViewInit, Component, EventEmitter, Output} from '@angular/core';
import * as L from 'leaflet';
import '@geoman-io/leaflet-geoman-free';
import '@geoman-io/leaflet-geoman-free/dist/leaflet-geoman.css';
import {Input} from '@angular/core';
import {FormArray} from '@angular/forms';
import {ActivatedRoute} from "@angular/router";
import {LatLng, Layer, Polygon, Rectangle} from "leaflet";

@Component({
  selector: 'app-map',
  templateUrl: './map.component.html',
  styleUrls: ['./map.component.css']
})

export class MapComponent implements AfterViewInit {
  private map;

  @Input() coordinatesIn: FormArray;
  @Output() coordinatesOut = new EventEmitter<LatLng[]>();

  private mapLayers: Layer[] = [];

  private polygon: Polygon;

  private initMapForEdit(): void {
    this.polygon = L.polygon([
      [this.coordinatesIn.value[0].latitude, this.coordinatesIn.value[0].longitude],
      [this.coordinatesIn.value[1].latitude, this.coordinatesIn.value[1].longitude],
      [this.coordinatesIn.value[2].latitude, this.coordinatesIn.value[2].longitude],
      [this.coordinatesIn.value[3].latitude, this.coordinatesIn.value[3].longitude],
    ]);

    this.map = L.map('map').setView(this.polygon.getBounds().getCenter(), 17);
    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 20,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    });

    tiles.addTo(this.map);

    this.map.pm.addControls({
      position: 'topright',
      drawMarker: false,
      drawCircle: false,
      cutPolygon: false,
      drawPolygon: false,
      drawCircleMarker: false,
      drawPolyline: false,
      drawRectangle: false,
      editMode: false,
      dragMode: false,
      removalMode: false
    });
    this.polygon.addTo(this.map);
    // @ts-ignore
    this.polygon.pm.enable({
      allowSelfIntersection: false,
    });
    this.polygon.on('pm:edit', e => {
      this.coordinatesOut.emit(e.target._latlngs[0]);
    });
  }

  private initMapForCreate(): void {
    this.map = L.map('map').setView([45.416667, 11.883333], 5);
    const tiles = L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
      maxZoom: 20,
      attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
    });

    tiles.addTo(this.map);

    this.map.pm.addControls({
      position: 'topright',
      drawMarker: false,
      drawCircle: false,
      cutPolygon: false,
      drawPolygon: true,
      drawCircleMarker: false,
      drawPolyline: false,
      drawRectangle: false,
      editMode: true,
      removalMode: false,
      dragMode: false
    });

    this.map.on('pm:create', create => {
      this.coordinatesOut.emit(create.layer._latlngs[0]);
      this.map.eachLayer((layer) => {
        this.mapLayers.push(layer);
      });

      this.mapLayers[this.mapLayers.length - 1].on('pm:edit', changed => {
        this.coordinatesOut.emit(changed.target._latlngs[0]);
      });
    });
  }

  constructor(private activatedRoute: ActivatedRoute) {
  }

  ngAfterViewInit(): void {
    /*
    this.coordinatesIn.valueChanges.subscribe((coordinates: any[]) => {
      let fourCoordinates = true;

      coordinates.forEach((coordinate) => {
        if (coordinate.latitude === '' || coordinate.longitude === '') {
          fourCoordinates = fourCoordinates && false;
        }
      });

      if (fourCoordinates) {
        if (this.polygon) {
          this.polygon.setLatLngs([
            [coordinates[0].latitude, coordinates[0].longitude],
            [coordinates[0].latitude, coordinates[0].longitude],
            [coordinates[1].latitude, coordinates[1].longitude],
            [coordinates[1].latitude, coordinates[1].longitude],
            [coordinates[2].latitude, coordinates[2].longitude],
            [coordinates[2].latitude, coordinates[2].longitude],
            [coordinates[3].latitude, coordinates[3].longitude],
            [coordinates[3].latitude, coordinates[3].longitude],
          ]);
        }
      }
    });*/
    if (this.activatedRoute.snapshot.url[2] && this.activatedRoute.snapshot.url[2].path === 'place-list') {
      setTimeout(() => this.initMapForEdit(), 1000);
    } else {
      setTimeout(() => this.initMapForCreate(), 1000);
    }

  }

}
