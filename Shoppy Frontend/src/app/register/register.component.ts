import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {
  registerForm: FormGroup;
  successMessage: string = '';
  errorMessage: string = '';

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router // Inject Router
  ) {
    this.registerForm = this.fb.group({
      username: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
    });
  }

  ngOnInit(): void {}

  onSubmit(): void {
    if (this.registerForm.valid) {
      const formData = this.registerForm.value;

      this.http.post('http://localhost:8080/signup', formData, { responseType: 'text' })
        .subscribe({
          next: (response: any) => {
            console.log('Registration successful:', response);
            this.successMessage = 'Registration successful! You can now log in.';
            this.errorMessage = '';
            this.registerForm.reset(); //clear the form
          },
          error: (err) => {
            console.error('Registration failed:', err);
            this.errorMessage = err.error || 'An error occurred during registration.';
            this.successMessage = '';
          }
        });
    } else {
      this.errorMessage = 'Please fill in all required fields correctly.';
      this.successMessage = '';
    }
  }

  goToLogin(): void {
    this.router.navigate(['/login'])
      .then(() => console.log('Navigated to login page successfully'))
      .catch((err) => console.error('Failed to navigate to login page:', err));
  }
}
