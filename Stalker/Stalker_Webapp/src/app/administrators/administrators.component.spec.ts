import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdministratorsComponent } from './administrators.component';

describe('AdministratorsComponent', () => {
  let component: AdministratorsComponent;
  let fixture: ComponentFixture<AdministratorsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdministratorsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdministratorsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
