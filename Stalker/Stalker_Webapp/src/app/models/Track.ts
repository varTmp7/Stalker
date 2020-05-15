export class Track {
  id: number;
  entered: boolean;
  // tslint:disable-next-line:variable-name
  date_time: string;
  name: string;
  surname: string;
  // tslint:disable-next-line:variable-name
  uid_number: number;
  username: string;
  place_id: number;

  public getDateTime(): Date {
    return new Date(this.date_time);
  }

  public dateTimeToTimestamp(): number {
    return Date.parse(this.date_time) / 1000;
  }
}
