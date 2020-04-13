import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { PlacedetailComponent } from './placedetail.component';

describe('PlacedetailComponent', () => {
  let component: PlacedetailComponent;
  let fixture: ComponentFixture<PlacedetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ PlacedetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(PlacedetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
