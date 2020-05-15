import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { TracksListComponent } from './tracks-list.component';

describe('TracksListComponent', () => {
  let component: TracksListComponent;
  let fixture: ComponentFixture<TracksListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ TracksListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(TracksListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
