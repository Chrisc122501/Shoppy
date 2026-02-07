import { Component } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-create-product',
  templateUrl: './create-product.component.html',
  styleUrls: ['./create-product.component.css']
})
export class CreateProductComponent {
  newProduct = {
    name: '',
    description: '',
    wholesalePrice: null,
    retailPrice: null,
    quantity: null
  };

  errorMessage: string = '';
  successMessage: string = '';

  constructor(private http: HttpClient, protected router: Router) {}

  submitProduct(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'You must be logged in as an admin to create products.';
      return;
    }

    this.http.post('http://localhost:8080/products', this.newProduct, {
      headers: { Authorization: `Bearer ${token}` }
    }).subscribe({
      next: (response) => {
        this.successMessage = 'Product created successfully!';
        setTimeout(() => {
          this.router.navigate(['/products']); // Redirect to products page
        }, 2000);
      },
      error: (err) => {
        console.error('Error creating product:', err);
        this.errorMessage = 'Failed to create product. Please try again.';
      }
    });
  }

  cancel(): void {
    this.router.navigate(['/products']);
  }
}
