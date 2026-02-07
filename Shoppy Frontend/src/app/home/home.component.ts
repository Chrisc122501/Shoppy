import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { OrderDetailComponent } from '../order-detail/order-detail.component';
import { EventEmitterService } from '../event-emitter.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  username: string = '';
  orders: any[] = [];
  frequentItems: any[] = [];
  recentItems: any[] = [];
  errorMessage: string = '';

  constructor(private http: HttpClient, public dialog: MatDialog, private eventEmitter: EventEmitterService ) {}

  ngOnInit(): void {
    this.getUsernameFromToken();
    this.fetchOrders();
    this.fetchTopFrequentItems();
    this.fetchTopRecentItems();
    this.eventEmitter.orderPlaced$.subscribe(() => {
      this.fetchOrders(); // Refresh orders
    });
  }

  private getUsernameFromToken(): void {
    const token = localStorage.getItem('token');
    if (token) {
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      this.username = decoded.sub; //sub holds username
    } else {
      console.error('No token found in local storage');
    }
  }

  private fetchOrders(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'No token found in local storage.';
      return;
    }
    console.log('Token in localStorage when fetching orders:', token);

    this.http.get<any[]>('http://localhost:8080/orders/all', {
      headers: { Authorization: token }
    }).subscribe({
      next: (data) => {
        console.log('Orders fetched successfully:', data);
        this.orders = data;
        this.fetchTopFrequentItems();
        console.log('Orders:', this.orders);
      },
      error: (err) => {
        console.error('Error fetching orders:', err);
        this.errorMessage = 'Failed to fetch orders.';
      }
    });
  }

  // Fetch top 3 frequent items
  // private fetchTopFrequentItems(): void {
  //   const token = localStorage.getItem('token');
  //   this.http.get<any[]>('http://localhost:8080/products/frequent/3', {
  //     headers: { Authorization: `Bearer ${token}` }
  //   }).subscribe({
  //     next: (data) => {
  //       this.frequentItems = data;
  //     },
  //     error: (err) => {
  //       console.error('Error fetching frequent items:', err);
  //       this.errorMessage = 'Failed to fetch frequent items.';
  //     }
  //   });
  // }

  private fetchTopFrequentItems(): void {
    if (!this.orders || this.orders.length === 0) {
      console.warn('No orders available to calculate frequent items.');
      this.frequentItems = [];
      return;
    }

    const completedOrders = this.orders.filter(order =>
      order.orderStatus === 'Completed' || order.orderStatus === 'Processing'
    );

    const productFrequencyMap: { [key: number]: { name: string; quantity: number } } = {};

    completedOrders.forEach(order => {
      if (order.orderItems && Array.isArray(order.orderItems)) {
        order.orderItems.forEach((item: any) => {
          const productId = item.product?.productId;
          const productName = item.product?.name;

          if (productId && productName) {
            if (productFrequencyMap[productId]) {
              productFrequencyMap[productId].quantity += item.quantity;
            } else {
              productFrequencyMap[productId] = {
                name: productName,
                quantity: item.quantity,
              };
            }
          } else {
            console.warn('Invalid product details for item:', item);
          }
        });
      } else {
        console.warn('Order missing orderItems:', order);
      }
    });

    const sortedProducts = Object.entries(productFrequencyMap)
      .map(([productId, product]) => ({ productId, ...product }))
      .sort((a, b) => b.quantity - a.quantity)
      .slice(0, 3);

    this.frequentItems = sortedProducts;
    console.log('Top 3 frequent items:', this.frequentItems);
  }

  // Fetch top 3 recent items
  private fetchTopRecentItems(): void {
    const token = localStorage.getItem('token');
    this.http.get<any[]>('http://localhost:8080/products/recent/3', {
      headers: { Authorization: `Bearer ${token}` }
    }).subscribe({
      next: (data) => {
        this.recentItems = data;
      },
      error: (err) => {
        console.error('Error fetching recent items:', err);
        this.errorMessage = 'Failed to fetch recent items.';
      }
    });
  }

  cancelOrder(orderId: number): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'No token found in local storage.';
      return;
    }
    this.http.patch<{ message: string }>(`http://localhost:8080/orders/${orderId}/cancel`, {}, {
      headers: { Authorization: `Bearer ${token}` }
    }).subscribe({
      next: (response) => {
        console.log(response.message);
        this.fetchOrders();
      },
      error: (err) => {
        console.error(`Error canceling order ${orderId}:`, err);
        this.errorMessage = `Order ${orderId} is already cancelled.`;
      }
    });
  }

  viewOrder(orderId: number): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'No token found in local storage.';
      return;
    }
    console.log('Token from localStorage:', token);

    this.http.get<any>(`http://localhost:8080/orders/${orderId}`, {
      headers: { Authorization: `Bearer ${token}` }
    }).subscribe({
      next: (orderDetails) => {
        // Open the dialog and pass the order details
        this.dialog.open(OrderDetailComponent, {
          width: '500px',
          data: orderDetails
        });
      },
      error: (err) => {
        console.error(`Error fetching details for order ${orderId}:`, err);
        this.errorMessage = `Failed to fetch order details for order ${orderId}.`;
      }
    });
  }
}
