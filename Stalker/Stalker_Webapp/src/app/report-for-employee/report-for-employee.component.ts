import {Component, Input, OnChanges, OnInit, SimpleChanges} from '@angular/core';
import {Track} from '../models/Track';
import {PlaceService} from '../services/place.service';
import {Place} from '../models/Place';
import {MatTableDataSource} from '@angular/material/table';
import {Employee} from '../models/Employee';
import * as moment from 'moment';
import {duration} from 'moment';
import {FormBuilder, FormGroup} from '@angular/forms';
import {PlaceTracks} from '../models/Filter';

@Component({
  selector: 'app-report-for-employee',
  templateUrl: './report-for-employee.component.html',
  styleUrls: ['./report-for-employee.component.css']
})
export class ReportForEmployeeComponent implements OnInit, OnChanges {
  @Input() tracks: Track[];
  places: Place[];

  dateForm: FormGroup;
  userTracks: (Track | Employee | string)[] = [];

  public userTracksData = new MatTableDataSource(this.userTracks);
  displayedColumns: string[] = ['date_time', 'place', 'status', 'uid_number'];
  private tracksToKeep = 0;

  constructor(private placeService: PlaceService, formBuilder: FormBuilder) {
    this.dateForm = formBuilder.group({
      date: ['']
    });
  }

  ngOnInit(): void {
    this.getPalces();
    this.userTracksData.filterPredicate = (((data, filter) => {
      if (data instanceof Employee) {
        // Nome dipendente
        const employee = data as Employee;
        if (employee.getName().toLowerCase().search(filter) >= 0) {
          this.tracksToKeep = this.tracks.filter((el) => el.uid_number === employee.uidNumber).length + 1;
        } else {
          this.tracksToKeep = 0;
        }

        return employee.getName().toLowerCase().search(filter) >= 0;
      } else {
        // Tracciamneto o "footer sezione"
        return this.tracksToKeep-- > 0;
      }
    }));
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.tracks) {
      this.buildDataStructure(this.tracks);
    }
  }

  private getPalces() {
    this.placeService.getPlaces(JSON.parse(sessionStorage.getItem('selectOrganization')).id).subscribe(placeList => {
      this.places = placeList;
    });
  }

  private buildDataStructure(tracks: Track[]) {
    this.userTracks = [];
    const uidNumbers = [...new Set(tracks.map((el) => el.uid_number))];
    this.placeService.getPlaces(JSON.parse(sessionStorage.getItem('selectOrganization')).id).subscribe((placeList: Place[]) => {
      for (const uidNumber of uidNumbers) {
        const uidTracks = tracks.filter((el) => el.uid_number === uidNumber);

        this.userTracks.push(new Employee(uidTracks[0].uid_number,
          uidTracks[0].name,
          uidTracks[0].surname,
          uidTracks[0].username));

        for (const track of uidTracks) {
          this.userTracks.push(track);
        }

        let hoursReport = '';
        for (const place of placeList) {
          const tracksInPlace = uidTracks.filter((el) => el.place_id === place.id);
          if (tracksInPlace.length > 0) {
            hoursReport = hoursReport + '<strong>Time spent in ' + place.name + ': <span class="time-in-place">' + this.sumDate(tracksInPlace) + '</span></strong><br>';
          }
        }
        hoursReport = hoursReport + '<strong>Total time: ' + this.sumDate(uidTracks) + '</strong>';

        this.userTracks.push(hoursReport);
      }
      this.userTracksData.data = this.userTracks;
    });
  }


  isUser(index, item): boolean {
    return item instanceof Employee;
  }

  isSum(index, item): boolean {
    return typeof item === 'string';
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.userTracksData.filter = filterValue.trim().toLowerCase();
  }

  getPlaceFromTrack(track: Track): string {
    if (this.places) {
      const place = this.places.find((el) => el.id === track.place_id);
      if (place) {
        return place.name;
      }
      return '';
    }
  }

  public rangeChange() {
    if (this.dateForm.controls.date.value) {
      const beginDate = moment(this.dateForm.controls.date.value.begin);
      const endDate = moment(this.dateForm.controls.date.value.end);
      this.buildDataStructure(this.tracks.filter((el) => moment(el.date_time).isBetween(beginDate, endDate)));
    } else {
      this.buildDataStructure(this.tracks);
    }
  }

  private sumDate(tracks: Track[]): string {
    // Somma le date di un array di tracciamenti nel seguente modo: (Track1_IN - Track1_OUT) + (Track2_IN - Track2_OUT)...
    const totalSum = duration();
    let inTrackDateTime: moment.Moment;

    for (const track of tracks) {
      if (track.entered) {
        // track_IN
        inTrackDateTime = moment(track.date_time);
      } else {
        // track_OUT
        const thisTrackTime = moment(track.date_time);
        const diffInDate = thisTrackTime.diff(inTrackDateTime);
        totalSum.add(diffInDate);
      }
    }
    return totalSum.hours().toString() + ':' + totalSum.minutes().toString() + ':' + totalSum.seconds().toString();
  }
}
