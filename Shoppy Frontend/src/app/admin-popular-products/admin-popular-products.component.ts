import { Component, Input, OnChanges, SimpleChanges } from '@angular/core';

@Component({
  selector: 'app-admin-popular-products',
  templateUrl: './admin-popular-products.component.html',
  styleUrls: ['./admin-popular-products.component.css']
})
export class AdminPopularProductsComponent implements OnChanges {
  @Input() userOrders: any[] = []; // Input from the parent component
  popularProducts: { name: string; description: string; quantity: number }[] = [];
  errorMessage: string = '';

  constructor() {}

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['userOrders'] && this.userOrders) {
      this.calculatePopularProducts();
    }
  }

  calculatePopularProducts(): void {
    const productQuantityMap: { [key: string]: { name: string; description: string; quantity: number } } = {};

    this.userOrders.forEach((order) => {
      if (order.orderStatus === 'Completed') {
        order.orderItems.forEach((item: any) => {
          const productId = item.product.productId;
          if (!productQuantityMap[productId]) {
            productQuantityMap[productId] = {
              name: item.product.name,
              description: item.product.description,
              quantity: 0
            };
          }
          productQuantityMap[productId].quantity += item.quantity;
        });
      }
    });

    this.popularProducts = Object.values(productQuantityMap)
      .sort((a, b) => b.quantity - a.quantity)
      .slice(0, 3); // Take the top 3
  }
}
