import { Component, OnInit } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'online-shopping-frontend';
  isLoggedIn = false;
  isAdmin = false; // Flag to track if the user is an admin
  initialized = false; //Flag to check if the component has been initialized

  constructor(private router: Router) {}

  ngOnInit(): void {
    this.router.events.subscribe((event) => {
      if (event instanceof NavigationEnd) {
        this.checkLoginState();
      }
    });

    // Initial login state check
    this.checkLoginState();
  }

  // checkLoginState(): void {
  //   const token = localStorage.getItem('token');
  //   this.isLoggedIn = !!token;
  //
  //   // Ensure the navbar is not shown on login/register routes
  //   const publicRoutes = ['/login', '/register'];
  //   if (publicRoutes.includes(this.router.url)) {
  //     this.isLoggedIn = false;
  //   }
  //
  //   this.initialized = true;
  // }

  checkLoginState(): void {
    const token = localStorage.getItem('token');
    if (token) {
      this.isLoggedIn = true;

      // Decode token to check role
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      this.isAdmin = decoded.role === 'ROLE_ADMIN';
    } else {
      this.isLoggedIn = false;
      this.isAdmin = false;
    }

    // Ensure the navbar is not shown on login/register routes
    const publicRoutes = ['/login', '/register'];
    if (publicRoutes.includes(this.router.url)) {
      this.isLoggedIn = false;
    }

    this.initialized = true;
  }

  // logout(): void {
  //   localStorage.removeItem('token');
  //   this.isLoggedIn = false;
  //   this.router.navigate(['/login']).then(() => {
  //     console.log('Navigation to login successful');
  //   });
  // }

  logout(): void {
    localStorage.removeItem('token');
    this.isLoggedIn = false;
    this.isAdmin = false;
    this.router.navigate(['/login']).then(() => {
      console.log('Navigation to login successful');
    });
  }
}
