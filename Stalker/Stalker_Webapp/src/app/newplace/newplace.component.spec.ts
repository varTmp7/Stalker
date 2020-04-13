import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { NewplaceComponent } from './newplace.component';

describe('NewplaceComponent', () => {
  let component: NewplaceComponent;
  let fixture: ComponentFixture<NewplaceComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ NewplaceComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(NewplaceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
