import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef, MatDialog } from '@angular/material/dialog';
import { HttpClient } from '@angular/common/http';
import { ProductDetailComponent } from '../product-detail/product-detail.component';

@Component({
  selector: 'app-order-detail',
  templateUrl: './order-detail.component.html',
  styleUrls: ['./order-detail.component.css']
})
export class OrderDetailComponent {
  constructor(
    public dialogRef: MatDialogRef<OrderDetailComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any, // Inject the order details
    private dialog: MatDialog,
    private http: HttpClient
  ) {}

  closeDialog(): void {
    this.dialogRef.close();
  }

  viewProductDetails(productId: number): void {
    const token = localStorage.getItem('token');
    if (!token) {
      console.error('Authorization token is missing.');
      return;
    }

    this.http.get(`http://localhost:8080/products/${productId}`, {
      headers: { Authorization: `${token}` }
    }).subscribe({
      next: (product: any) => {
        this.dialog.open(ProductDetailComponent, {
          data: product
        });
      },
      error: (err) => {
        console.error('Error fetching product details:', err);
      }
    });
  }
}
