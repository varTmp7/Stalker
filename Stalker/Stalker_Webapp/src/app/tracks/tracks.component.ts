import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {DatePipe, Location} from '@angular/common';
import {Track} from '../models/Track';
import {TracksService} from '../tracks.service';


@Component({
  selector: 'app-tracks',
  templateUrl: './tracks.component.html',
  styleUrls: ['./tracks.component.css']
})
export class TracksComponent implements OnInit {
  tracks: Track[];
  displayedColumns: string[] = ['ID', 'date_time', 'uid_number', 'username', 'name_surname'];
  constructor(private tracksService: TracksService, private route: ActivatedRoute, private location: Location) {
  }

  ngOnInit(): void {
    this.getTracks();
  }

  private getTracks(): void {
    const organizationId = +this.route.snapshot.paramMap.get('organization_id');
    const placeId = +this.route.snapshot.paramMap.get('place_id');
    this.tracksService.getTracks(organizationId, placeId).subscribe(tracksReturned => this.tracks = tracksReturned);
  }

}

