import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AdminPopularProductsComponent } from './admin-popular-products.component';

describe('AdminPopularProductsComponent', () => {
  let component: AdminPopularProductsComponent;
  let fixture: ComponentFixture<AdminPopularProductsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AdminPopularProductsComponent ]
    })
    .compileComponents();

    fixture = TestBed.createComponent(AdminPopularProductsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
