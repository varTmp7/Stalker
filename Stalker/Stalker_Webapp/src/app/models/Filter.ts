import {Place} from './Place';
import {Track} from './Track';

export class PlaceTracks {
  place: Place;
  tracks: Track[];


  constructor(place: Place, tracks: Track[]) {
    this.place = place;
    this.tracks = tracks;
  }
}

export class FilterCounter {
  places: PlaceTracks[];

  constructor() {
    this.places = [];
  }
}


