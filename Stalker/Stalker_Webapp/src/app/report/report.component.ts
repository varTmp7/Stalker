import { Component, OnInit } from '@angular/core';
import {TrackService} from "../services/track.service";
import {Track} from "../models/Track";
import {Organization} from "../models/Organization";

@Component({
  selector: 'app-report',
  templateUrl: './report.component.html',
  styleUrls: ['./report.component.css']
})
export class ReportComponent implements OnInit {
  public tracks: Track[];
  public organization: Organization;

  constructor(private trackService: TrackService) { }

  ngOnInit(): void {
    this.organization = JSON.parse(sessionStorage.getItem('selectOrganization'));
    this.getAllTracks();
  }

  private getAllTracks() {
    this.trackService.getTracksForOrganization(this.organization.id).subscribe(tracksList => {
      this.tracks = tracksList;
    });
  }
}
