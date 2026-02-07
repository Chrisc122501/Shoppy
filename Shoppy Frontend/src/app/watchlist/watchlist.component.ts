import { Component, OnInit } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Component({
  selector: 'app-watchlist',
  templateUrl: './watchlist.component.html',
  styleUrls: ['./watchlist.component.css']
})
export class WatchlistComponent implements OnInit {
  watchlist: any[] = [];
  errorMessage: string = '';
  successMessage: string = '';

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.fetchWatchlist();
  }

  fetchWatchlist(): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'You must be logged in to view your watchlist.';
      return;
    }

    this.http
      .get<any[]>('http://localhost:8080/watchlist/products/all', {
        headers: { Authorization: `Bearer ${token}` }
      })
      .subscribe({
        next: (data) => {
          this.watchlist = data;
        },
        error: (err) => {
          console.error('Error fetching watchlist:', err);
          this.errorMessage = 'Failed to load watchlist. Please try again later.';
        }
      });
  }

  removeFromWatchlist(productId: number): void {
    const token = localStorage.getItem('token');
    if (!token) {
      this.errorMessage = 'You must be logged in to remove products from your watchlist.';
      return;
    }

    this.http
      .delete(`http://localhost:8080/watchlist/product/${productId}`, {
        headers: { Authorization: `Bearer ${token}` },
        responseType: 'text'
      })
      .subscribe({
        next: (message) => {
          this.successMessage = message;
          setTimeout(() => (this.successMessage = ''), 3000);
          this.fetchWatchlist();
        },
        error: (err) => {
          console.error('Error removing from watchlist:', err);
          this.errorMessage = 'Failed to remove product from watchlist. Please try again.';
          setTimeout(() => (this.errorMessage = ''), 3000);
        }
      });
  }
}
