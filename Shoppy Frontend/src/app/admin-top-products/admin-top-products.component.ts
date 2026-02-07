import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-admin-top-products',
  templateUrl: './admin-top-products.component.html',
  styleUrls: ['./admin-top-products.component.css']
})
export class AdminTopProductsComponent implements OnInit {
  topProfitableProducts: any[] = [];
  errorMessage: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchTopProfitableProducts();
  }

  fetchTopProfitableProducts(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'You must be logged in to view the top profitable products.';
      return;
    }

    this.http
      .get<any[]>('http://localhost:8080/products/profit/3', {
        headers: { Authorization: `Bearer ${token}` }
      })
      .subscribe({
        next: (data) => {
          this.topProfitableProducts = data;
        },
        error: (err) => {
          console.error('Error fetching top profitable products:', err);
          this.errorMessage = 'Failed to load the top profitable products. Please try again later.';
        }
      });
  }
}
