import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { EventEmitterService } from '../event-emitter.service';

@Component({
  selector: 'app-cart',
  templateUrl: './cart.component.html',
  styleUrls: ['./cart.component.css']
})
export class CartComponent implements OnInit {
  cart: any[] = [];
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private http: HttpClient, private eventEmitter: EventEmitterService) {}

  ngOnInit(): void {
    this.loadCart();
  }

  loadCart(): void {
    const savedCart = localStorage.getItem('cart');
    this.cart = savedCart ? JSON.parse(savedCart) : [];
  }

  removeFromCart(productId: number): void {
    //Filter out the item to be removed
    this.cart = this.cart.filter(item => item.productId !== productId);
    localStorage.setItem('cart', JSON.stringify(this.cart));
    this.loadCart();
  }

  placeOrder(): void {
    if (this.cart.length === 0) {
      this.errorMessage = 'Your cart is empty.';
      setTimeout(() => (this.errorMessage = ''), 3000);
      return;
    }

    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'You must be logged in to place an order.';
      setTimeout(() => (this.errorMessage = ''), 3000);
      return;
    }

    const orderRequest = {
      order: this.cart.map(item => ({
        productId: item.productId,
        quantity: item.quantity
      }))
    };

    this.http
      .post('http://localhost:8080/orders', orderRequest, {
        headers: { Authorization: `Bearer ${token}` }
      })
      .subscribe({
        next: (response) => {
          this.successMessage = 'Order placed successfully! Please wait for your order to finish processing';
          this.cart = []; // Clear the cart
          localStorage.removeItem('cart'); //Clear from local storage
          this.eventEmitter.emitOrderPlaced();
          setTimeout(() => (this.successMessage = ''), 3000);
        },
        error: (err) => {
          console.error('Error placing order:', err);
          if (err.status === 400) {
            this.errorMessage = 'Order failed: ' + err.error;
          } else {
            this.errorMessage = 'Failed to place the order. Please try again.';
          }
          setTimeout(() => (this.errorMessage = ''), 3000);
        }
      });
  }

  getTotalPrice(): number {
    return this.cart.reduce((total, item) => total + item.retailPrice * item.quantity, 0);
  }

  formattedTotalPrice(): string {
    return this.getTotalPrice().toFixed(2);
  }
}
