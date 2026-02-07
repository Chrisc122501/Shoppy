import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-products',
  templateUrl: './products.component.html',
  styleUrls: ['./products.component.css']
})
export class ProductsComponent implements OnInit {
  products: any[] = [];
  errorMessage: string = '';
  successMessage: string = '';
  cart: any[] = [];
  role: string = '';
  desiredQuantities: { [key: number]: number | null } = {};

  constructor(private http: HttpClient, private router: Router) {}

  ngOnInit(): void {
    this.role = this.getUserRole();
    this.fetchProducts();
  }

  navigateToCreateProduct(): void {
    this.router.navigate(['/products/create']).then(() => {
      console.log('Navigated to Create Product form');
    }).catch((err) => {
      console.error('Navigation error:', err);
    });
  }

  editProduct(productId: number): void {
    this.router.navigate([`/products/edit/${productId}`])
      .then(() => {
        console.log('Navigation for editing product successful');
      })
      .catch((err) => {
        console.error('Navigation error for editing product:', err);
      });
  }

  getUserRole(): string {
    const token = localStorage.getItem('token');
    if (token) {
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      return decoded.role; // 'ROLE_ADMIN' or 'ROLE_USER'
    }
    return '';
  }

  // fetchProducts(): void {
  //   const token = localStorage.getItem('token');
  //   if (!token) {
  //     this.errorMessage = 'You must be logged in to view products.';
  //     return;
  //   }
  //
  //   this.http
  //     .get<any[]>('http://localhost:8080/products/all', {
  //       headers: { Authorization: `Bearer ${token}` }
  //     })
  //     .subscribe({
  //       next: (data) => {
  //         this.products = data;
  //       },
  //       error: (err) => {
  //         console.error('Error fetching products:', err);
  //         this.errorMessage = 'Failed to load products. Please try again later.';
  //       }
  //     });
  // }

  fetchProducts(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'You must be logged in to view products.';
      return;
    }

    this.http
      .get<any[]>('http://localhost:8080/products/all', {
        headers: { Authorization: `Bearer ${token}` }
      })
      .subscribe({
        next: (data) => {
          // Admin sees all products; users see only in-stock products
          this.products =
            this.role === 'ROLE_ADMIN'
              ? data
              : data.filter(product => product.quantity > 0);
        },
        error: (err) => {
          console.error('Error fetching products:', err);
          this.errorMessage = 'Failed to load products. Please try again later.';
        }
      });
  }

  addToWatchlist(productId: number): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'You must be logged in to add products to your watchlist.';
      return;
    }

    this.http
      .post(`http://localhost:8080/watchlist/product/${productId}`, {}, {
        headers: { Authorization: `Bearer ${token}` },
        responseType: 'text'
      })
      .subscribe({
        next: (message) => {
          this.successMessage = message;
          setTimeout(() => (this.successMessage = ''), 3000);
        },
        error: (err) => {
          console.error('Error adding to watchlist:', err);
          if (err.status === 409) {
            this.errorMessage = 'Product is already in your watchlist.';
          } else {
            this.errorMessage = 'Failed to add product to watchlist. Please try again.';
          }
          setTimeout(() => (this.errorMessage = ''), 3000);
        }
      });
  }

  // addToCart(product: any): void {
  //   if (!product.quantity || product.quantity < 1) {
  //     this.errorMessage = 'Please specify a valid quantity.';
  //     return;
  //   }
  //
  //   const savedCart = localStorage.getItem('cart');
  //   this.cart = savedCart ? JSON.parse(savedCart) : [];
  //
  //   const existingItem = this.cart.find(item => item.productId === product.productId);
  //   if (existingItem) {
  //     existingItem.quantity += product.quantity;
  //   } else {
  //     this.cart.push({ ...product });
  //   }
  //
  //   localStorage.setItem('cart', JSON.stringify(this.cart));
  //
  //   this.successMessage = `${product.name} added to cart successfully.`;
  //   setTimeout(() => (this.successMessage = ''), 3000);
  // }
  addToCart(product: any): void {
    const desiredQuantity = this.desiredQuantities[product.productId] || 0;

    if (desiredQuantity < 1) {
      this.errorMessage = 'Please specify a valid quantity.';
      return;
    }

    const savedCart = localStorage.getItem('cart');
    this.cart = savedCart ? JSON.parse(savedCart) : [];

    const existingItem = this.cart.find(item => item.productId === product.productId);
    if (existingItem) {
      existingItem.quantity += desiredQuantity;
    } else {
      this.cart.push({ ...product, quantity: desiredQuantity });
    }
    localStorage.setItem('cart', JSON.stringify(this.cart));

    this.successMessage = `${product.name} added to cart successfully.`;
    setTimeout(() => (this.successMessage = ''), 3000);

    // Clear the desired quantity after adding to cart
    this.desiredQuantities[product.productId] = null;
  }
}
