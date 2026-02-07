import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminTotalSoldComponent } from './admin-total-sold.component';

describe('AdminTotalSoldComponent', () => {
  let component: AdminTotalSoldComponent;
  let fixture: ComponentFixture<AdminTotalSoldComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminTotalSoldComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminTotalSoldComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
