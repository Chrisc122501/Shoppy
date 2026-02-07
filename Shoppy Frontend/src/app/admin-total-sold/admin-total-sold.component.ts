import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-admin-total-sold',
  templateUrl: './admin-total-sold.component.html',
  styleUrls: ['./admin-total-sold.component.css']
})
export class AdminTotalSoldComponent implements OnChanges {
  @Input() userOrders: any[] = [];
  totalSoldItems: number = 0;

  constructor() {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['userOrders'] && this.userOrders) {
      this.calculateTotalSoldItems();
    }
  }

  calculateTotalSoldItems(): void {
    this.totalSoldItems = 0;
    this.userOrders.forEach((order) => {
      if (order.orderStatus === 'Completed') {
        order.orderItems.forEach((item: any) => {
          this.totalSoldItems += item.quantity;
        });
      }
    });
  }
}
