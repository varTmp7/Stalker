import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportForPlaceComponent } from './report-for-place.component';

describe('ReportForPlaceComponent', () => {
  let component: ReportForPlaceComponent;
  let fixture: ComponentFixture<ReportForPlaceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReportForPlaceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReportForPlaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
