import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ApprovePlacesComponent } from './approve-places.component';

describe('ApprovePlacesComponent', () => {
  let component: ApprovePlacesComponent;
  let fixture: ComponentFixture<ApprovePlacesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ApprovePlacesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ApprovePlacesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
