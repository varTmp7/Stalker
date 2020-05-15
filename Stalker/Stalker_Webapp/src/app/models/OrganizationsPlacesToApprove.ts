import {Place} from './Place';

export class OrganizationsPlacesToApprove {
  id: number;
  name: string;
  places: Place[];
}

export class SimpleOrganization {
  id: number;
  name: string;

  constructor(id: number, name: string) {
    this.id = id;
    this.name = name;
  }
}
