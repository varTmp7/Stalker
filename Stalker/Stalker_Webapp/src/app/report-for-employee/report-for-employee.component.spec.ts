import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReportForEmployeeComponent } from './report-for-employee.component';

describe('ReportForEmployeeComponent', () => {
  let component: ReportForEmployeeComponent;
  let fixture: ComponentFixture<ReportForEmployeeComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReportForEmployeeComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReportForEmployeeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
