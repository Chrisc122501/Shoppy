import { Component, OnInit } from '@angular/core'; // Removed ViewChild and MatPaginator
import { HttpClient } from '@angular/common/http';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { OrderDetailComponent } from '../order-detail/order-detail.component';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.css']
})
export class AdminDashboardComponent implements OnInit {
  displayedColumns: string[] = ['orderId', 'user', 'datePlaced', 'status', 'actions'];
  dataSource = new MatTableDataSource<any>();
  topProfitableProducts: any[] = [];
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private http: HttpClient, private dialog: MatDialog) {}

  ngOnInit(): void {
    this.fetchOrders();
    //this.fetchTopProfitableProducts();
  }

  fetchOrders(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'You must be logged in to view orders.';
      return;
    }

    this.http
      .get<any[]>('http://localhost:8080/orders/all', {
        headers: { Authorization: `Bearer ${token}` }
      })
      .subscribe({
        next: (data) => {
          this.dataSource.data = data;
        },
        error: (err) => {
          console.error('Error fetching orders:', err);
          this.errorMessage = 'Failed to load orders. Please try again later.';
        }
      });
  }

  // fetchTopProfitableProducts(): void {
  //   const token = localStorage.getItem('token');
  //   if (!token) {
  //     this.errorMessage = 'You must be logged in to view the top profitable products.';
  //     return;
  //   }
  //
  //   this.http
  //     .get<any[]>('http://localhost:8080/products/profit/3', {
  //       headers: { Authorization: `Bearer ${token}` }
  //     })
  //     .subscribe({
  //       next: (data) => {
  //         this.topProfitableProducts = data;
  //       },
  //       error: (err) => {
  //         console.error('Error fetching top profitable products:', err);
  //         this.errorMessage = 'Failed to load the top profitable products. Please try again later.';
  //       }
  //     });
  // }

  cancelOrder(order: any): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'You must be logged in to cancel orders.';
      return;
    }

    this.http
      .patch(`http://localhost:8080/orders/${order.orderId}/cancel`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      })
      .subscribe({
        next: () => {
          this.successMessage = `Order ${order.orderId} has been canceled successfully.`;
          this.fetchOrders(); // Refresh orders
          setTimeout(() => (this.successMessage = ''), 3000);
        },
        error: (err) => {
          console.error('Error canceling order:', err);
          this.errorMessage = `Failed to cancel order ${order.orderId}. Please try again.`;
          setTimeout(() => (this.errorMessage = ''), 3000);
        }
      });
  }

  completeOrder(order: any): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'You must be logged in to complete orders.';
      return;
    }

    this.http
      .patch(`http://localhost:8080/orders/${order.orderId}/complete`, {}, {
        headers: { Authorization: `Bearer ${token}` }
      })
      .subscribe({
        next: () => {
          this.successMessage = `Order ${order.orderId} has been completed successfully.`;
          this.fetchOrders(); // Refresh orders
          setTimeout(() => (this.successMessage = ''), 3000);
        },
        error: (err) => {
          console.error('Error completing order:', err);
          this.errorMessage = `Failed to complete order ${order.orderId}. Please try again.`;
          setTimeout(() => (this.errorMessage = ''), 3000);
        }
      });
  }

  viewOrder(order: any): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'You must be logged in to view orders.';
      return;
    }

    this.http
      .get<any>(`http://localhost:8080/orders/${order.orderId}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      .subscribe({
        next: (orderDetails) => {
          this.dialog.open(OrderDetailComponent, {
            data: orderDetails,
            width: '600px'
          });
        },
        error: (err) => {
          console.error('Error fetching order details:', err);
          this.errorMessage = `Failed to view order ${order.orderId}. Please try again.`;
          setTimeout(() => (this.errorMessage = ''), 3000);
        }
      });
  }
}
