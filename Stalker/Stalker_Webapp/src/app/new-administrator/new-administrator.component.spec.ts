import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewAdministratorComponent } from './new-administrator.component';

describe('NewAdministratorComponent', () => {
  let component: NewAdministratorComponent;
  let fixture: ComponentFixture<NewAdministratorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewAdministratorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewAdministratorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
