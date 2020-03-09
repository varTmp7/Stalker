import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { OrganizationdetailComponent } from './organizationdetail.component';

describe('OrganizationdetailComponent', () => {
  let component: OrganizationdetailComponent;
  let fixture: ComponentFixture<OrganizationdetailComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ OrganizationdetailComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(OrganizationdetailComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
