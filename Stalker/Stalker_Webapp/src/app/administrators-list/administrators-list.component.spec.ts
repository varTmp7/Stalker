import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdministratorsListComponent } from './administrators-list.component';

describe('AdministratorsListComponent', () => {
  let component: AdministratorsListComponent;
  let fixture: ComponentFixture<AdministratorsListComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdministratorsListComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdministratorsListComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
