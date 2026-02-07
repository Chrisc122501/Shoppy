import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(private router: Router) {}

  // canActivate(): boolean {
  //   const token = localStorage.getItem('token');
  //   if (token) {
  //     return true;
  //   } else {
  //     this.router.navigate(['/login']);
  //     return false;
  //   }
  // }

  canActivate(): boolean {
    const token = localStorage.getItem('token');
    if (token) {
      const payload = token.split('.')[1];
      const decoded = JSON.parse(atob(payload));
      const isAdmin = decoded.role === 'ROLE_ADMIN';

      if (this.router.url === '/home' && isAdmin) {
        this.router.navigate(['/admin-dashboard']);
        return false;
      }

      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }

}
