import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {Organization} from '../models/Organization';
import {Place} from '../models/Place';
import {MatSort} from '@angular/material/sort';
import {Track} from '../models/Track';
import {MatTableDataSource} from '@angular/material/table';
import {TrackService} from '../services/track.service';
import {ActivatedRoute} from '@angular/router';

@Component({
  selector: 'app-tracks-list',
  templateUrl: './tracks-list.component.html',
  styleUrls: ['./tracks-list.component.css']
})
export class TracksListComponent implements OnInit {

  @Input() organization: Organization;

  tracks = new MatTableDataSource<Track>([]);
  displayedColumns: string[] = ['date_time', 'status', 'name', 'surname', 'username', 'uid_number'];

  @ViewChild(MatSort, {static: true}) sort: MatSort;

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.tracks.filter = filterValue.trim().toLowerCase();
  }

  constructor(private trackService: TrackService, private activatedRoute: ActivatedRoute) {
  }

  ngOnInit(): void {
    this.getTracks();
    this.tracks.sort = this.sort;
  }

  private getTracks() {
    const placeId = this.activatedRoute.snapshot.paramMap.get('place_id');
    if (placeId) {
      this.trackService.getTracks(this.organization.id, placeId).subscribe(tracksList => {
        this.tracks.data = tracksList;
        this.tracks.data.reverse();
      });
    }
  }
}
