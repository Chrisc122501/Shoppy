import { Component } from '@angular/core';

@Component({
  selector: 'app-contact-us',
  templateUrl: './contact-us.component.html',
  styleUrls: ['./contact-us.component.css']
})
export class ContactUsComponent {
  message: string = '';
  successMessage: string = '';

  submitMessage(): void {
    this.successMessage = 'Your message was sent. Patiently wait for support to get back to you.';
    this.message = ''; // Clear the message box
    setTimeout(() => (this.successMessage = ''), 5000); // Clear the success message after 5 seconds
  }
}
