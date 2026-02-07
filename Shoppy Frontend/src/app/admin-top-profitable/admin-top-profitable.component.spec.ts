import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminTopProfitableComponent } from './admin-top-profitable.component';

describe('AdminTopProfitableComponent', () => {
  let component: AdminTopProfitableComponent;
  let fixture: ComponentFixture<AdminTopProfitableComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminTopProfitableComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminTopProfitableComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
