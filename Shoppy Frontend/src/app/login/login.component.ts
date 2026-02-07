import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit {
  loginForm: FormGroup;
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) {
    // Initialize the login form
    this.loginForm = this.fb.group({
      username: ['', Validators.required],
      password: ['', Validators.required]
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.loginForm.valid) {
      const loginData = this.loginForm.value;

      this.http.post('http://localhost:8080/login', loginData, { responseType: 'text' })
        .subscribe({
          next: (response: any) => {
            console.log('Login successful:', response);
            localStorage.setItem('token', response); // Store JWT token
            const role = this.decodeJwt(response).role;

            if (role === 'ROLE_ADMIN') {
              this.router.navigate(['/admin-dashboard'])
                .then(() => console.log('Navigated to admin dashboard'))
                .catch((err) => console.error('Navigation to admin dashboard failed:', err));
            } else {
              this.router.navigate(['/home'])
                .then(() => console.log('Navigated to home page'))
                .catch((err) => console.error('Navigation to home page failed:', err));
            }
          },
          error: (err) => {
            console.error('Login failed:', err);
            this.errorMessage = 'Invalid username or password. Please try again.';
          }
        });
    } else {
      this.errorMessage = 'Please fill in both fields.';
    }
  }

  goToRegister(): void {
    this.router.navigate(['/register']).then(() => {
      console.log('Navigated to register page');
    }).catch((error) => {
      console.error('Navigation to register page failed:', error);
    });
  }

  //get role
  private decodeJwt(token: string): any {
    const payload = token.split('.')[1];
    return JSON.parse(atob(payload));
  }
}
