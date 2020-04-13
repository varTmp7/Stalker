import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AdministratorDetailsComponent } from './administrator-details.component';

describe('AdministratorDetailsComponent', () => {
  let component: AdministratorDetailsComponent;
  let fixture: ComponentFixture<AdministratorDetailsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AdministratorDetailsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AdministratorDetailsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
