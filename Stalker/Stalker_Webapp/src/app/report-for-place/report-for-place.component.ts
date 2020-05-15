import {Component, Input, OnChanges, OnInit, SimpleChanges, ViewChildren} from '@angular/core';
import {Track} from '../models/Track';
import {MatTableDataSource} from '@angular/material/table';
import {PlaceService} from '../services/place.service';
import {Place} from '../models/Place';
import {FilterCounter, PlaceTracks} from '../models/Filter';
import {MatSlideToggle, MatSlideToggleChange} from "@angular/material/slide-toggle";
import {FormBuilder, FormGroup} from "@angular/forms";

@Component({
  selector: 'app-report-for-place',
  templateUrl: './report-for-place.component.html',
  styleUrls: ['./report-for-place.component.css']
})


export class ReportForPlaceComponent implements OnInit, OnChanges {

  @Input() tracks: Track[];

  filterForm: FormGroup;

  trackPlaces: (Track | Place)[] = [];

  private filterData: FilterCounter = new FilterCounter();
  private tracksToKeep = 0;

  public trackPlaceData = new MatTableDataSource(this.trackPlaces);
  displayedColumns: string[] = ['date_time', 'status', 'name', 'surname', 'username', 'uid_number'];

  constructor(private placeService: PlaceService, formBuilder: FormBuilder) {
    this.filterForm = formBuilder.group({
      enableAlpha: false,
      enableNumbered: false
    });
  }

  ngOnInit(): void {
    this.trackPlaceData.filterPredicate = ((data, filter) => {
      if (data['place_id'] === undefined) {
        // Si tratta di un luogo
        if (data.name.toLowerCase().search(filter) >= 0) {
          this.tracksToKeep = this.filterData.places.find((el) => el.place.id === data.id).tracks.length;
        } else {
          this.tracksToKeep = 0;
        }
        return data.name.toLowerCase().search(filter) >= 0;
      } else {
        // Si tratta di un tracciamento
        return this.tracksToKeep-- > 0;
      }
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (this.tracks) {
      this.buildDataStructure(this.tracks);
    }
  }

  private buildDataStructure(tracks: Track[]) {
    // Azzeramento dei dati in memoria
    this.trackPlaces = [];
    this.filterData = new FilterCounter();

    // Ottenimento della lista di luoghi
    this.placeService.getPlaces(JSON.parse(sessionStorage.getItem('selectOrganization')).id).subscribe(placeList => {
      if (this.filterForm.controls.enableAlpha.value) {
        // Se è richiesto l'ordine alfabetico
        placeList.sort((a, b) => {
          if (a.name < b.name) {
            return -1;
          }
          if (a.name > b.name) {
            return 1;
          }
          return 0;
        });
      }
      if (this.filterForm.controls.enableNumbered.value) {
        // Se è richiesto l'ordine per numero di tracciamenti
        for (const place of placeList) { // Per ogni luogo
          // Recupero i tracciamenti
          const placesTrack = tracks.filter((el) => el.place_id === place.id);
          // Asscoio tracciamenti ai luoghi
          if (placesTrack.length > 0) {
            this.filterData.places.push(new PlaceTracks(place, placesTrack));
          }
        }
        // Deep copy for search
        const copyOfFilteredDate = JSON.parse(JSON.stringify(this.filterData)) as FilterCounter;
        // Sorting per numero di tracciamenti
        copyOfFilteredDate.places.sort((a, b) => {
          return  b.tracks.length - a.tracks.length;
        });
        // Costruzione struttura dati mostrata dalla tabella
        for (const placeTrack of copyOfFilteredDate.places) {
          this.trackPlaces.push(placeTrack.place);
          for (const track of placeTrack.tracks) {
            this.trackPlaces.push(track);
          }
        }
      } else {
        // O è richiesto l'ordine alfabetico oppure niente
        for (const place of placeList) {
          const placesTrack = tracks.filter((el) => el.place_id === place.id);
          if (placesTrack.length > 0) {
            this.filterData.places.push(new PlaceTracks(place, placesTrack));
            this.trackPlaces.push(place);
            for (const track of placesTrack) {
              this.trackPlaces.push(track);
            }
          }
        }
      }
      // Aggiorno la dataSource della tabella con quanto prodotto
      this.trackPlaceData.data = this.trackPlaces;
    });
  }

  isPlace(index, item): boolean {
    return item.place_id === undefined;
  }

  sortAlphabetic(event: MatSlideToggleChange) {
    this.filterForm.controls.enableNumbered.setValue(false);
    this.buildDataStructure(this.tracks);
  }

  sortNumbered(event: MatSlideToggleChange) {
    this.filterForm.controls.enableAlpha.setValue(false);
    this.buildDataStructure(this.tracks);
  }

  applyFilter(event: Event) {
    const filterValue = (event.target as HTMLInputElement).value;
    this.trackPlaceData.filter = filterValue.trim().toLowerCase();
  }
}
