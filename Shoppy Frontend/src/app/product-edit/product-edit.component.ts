import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-product-edit',
  templateUrl: './product-edit.component.html',
  styleUrls: ['./product-edit.component.css']
})
export class ProductEditComponent implements OnInit {
  product: any = {};
  errorMessage: string = '';
  successMessage: string = '';

  constructor(
    private route: ActivatedRoute,
    private http: HttpClient,
    private router: Router
  ) {}

  ngOnInit(): void {
    const productId = this.route.snapshot.paramMap.get('id');
    this.fetchProductDetails(productId);
  }

  fetchProductDetails(productId: string | null): void {
    const token = localStorage.getItem('token');
    if (!productId || !token) {
      this.errorMessage = 'Product not found or unauthorized.';
      return;
    }

    this.http
      .get<any>(`http://localhost:8080/products/${productId}`, {
        headers: { Authorization: `Bearer ${token}` }
      })
      .subscribe({
        next: (data) => {
          this.product = data;
        },
        error: (err) => {
          console.error('Error fetching product details:', err);
          this.errorMessage = 'Failed to fetch product details.';
        }
      });
  }

  updateProduct(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'Unauthorized.';
      return;
    }

    this.http
      .patch(`http://localhost:8080/products/${this.product.productId}`, this.product, {
        headers: { Authorization: `Bearer ${token}` }
      })
      .subscribe({
        next: () => {
          this.successMessage = 'Product updated successfully.';
          setTimeout(() => this.router.navigate(['/products']), 2000);
        },
        error: (err) => {
          console.error('Error updating product:', err);
          this.errorMessage = 'Failed to update product.';
        }
      });
  }

  cancel(): void {
    this.router.navigate(['/products']);
  }
}
