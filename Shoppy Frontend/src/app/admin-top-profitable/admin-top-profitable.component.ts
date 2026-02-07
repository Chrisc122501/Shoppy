import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-admin-top-profitable',
  templateUrl: './admin-top-profitable.component.html',
  styleUrls: ['./admin-top-profitable.component.css']
})
export class AdminTopProfitableComponent implements OnChanges {
  @Input() userOrders: any[] = []; // Input to receive orders from parent
  //topProfitableProducts: { name: string; description: string; revenue: number }[] = [];
  topProfitableProducts: { name: string; description: string; profit: number }[] = [];
  errorMessage: string = '';

  constructor() {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['userOrders'] && this.userOrders) {
      this.calculateTopProfitableProducts();
    }
  }

  // calculateTopProfitableProducts(): void {
  //   const productRevenueMap: { [key: string]: { name: string; description: string; revenue: number } } = {};
  //
  //   this.userOrders.forEach((order) => {
  //     if (order.orderStatus === 'Completed') {
  //       order.orderItems.forEach((item: any) => {
  //         const productId = item.product.productId;
  //         if (!productRevenueMap[productId]) {
  //           productRevenueMap[productId] = {
  //             name: item.product.name,
  //             description: item.product.description,
  //             revenue: 0
  //           };
  //         }
  //         productRevenueMap[productId].revenue += item.quantity * item.product.retailPrice;
  //       });
  //     }
  //   });
  //
  //   this.topProfitableProducts = Object.values(productRevenueMap)
  //     .sort((a, b) => b.revenue - a.revenue)
  //     .slice(0, 3);
  // }

  calculateTopProfitableProducts(): void {
    const productProfitMap: { [key: string]: { name: string; description: string; profit: number } } = {};

    this.userOrders.forEach((order) => {
      if (order.orderStatus === 'Completed') {
        order.orderItems.forEach((item: any) => {
          const productId = item.product.productId;
          const profitPerUnit = item.product.retailPrice - item.product.wholesalePrice;

          if (!productProfitMap[productId]) {
            productProfitMap[productId] = {
              name: item.product.name,
              description: item.product.description,
              profit: 0,
            };
          }

          productProfitMap[productId].profit += item.quantity * profitPerUnit;
        });
      }
    });

    this.topProfitableProducts = Object.values(productProfitMap)
      .sort((a, b) => b.profit - a.profit) // Sort by total profit in descending order
      .slice(0, 3); // Take the top 3 products

    console.log('Top Profitable Products:', this.topProfitableProducts);
  }
}
