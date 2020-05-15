export class Coordinate {
  latitude: number;
  longitude: number;

  constructor(latitude: number, longitude: number) {
    this.latitude = latitude;
    this.longitude = longitude;
  }
}

export class Place {
  approved: boolean;
  coordinates: Coordinate[];
  id: number;
  name: string;
  // tslint:disable-next-line:variable-name
  num_max_people: number;
  // tslint:disable-next-line:variable-name
  number_of_people: number;
  // tslint:disable-next-line:variable-name
  organization_id: number;
}
