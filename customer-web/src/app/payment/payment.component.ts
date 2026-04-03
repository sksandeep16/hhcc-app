
import { Component, OnInit, AfterViewInit, NgZone, ViewChild, ElementRef } from '@angular/core';
import { loadStripe, Stripe, StripeCardElement } from '@stripe/stripe-js';
import { PaymentService } from '../services/payment.service';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';


@Component({
  selector: 'app-payment',
  templateUrl: './payment.component.html',
  styleUrls: ['./payment.component.css'],
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
})
export class PaymentComponent implements OnInit, AfterViewInit {
  stripe: Stripe | null = null;
  card: StripeCardElement | null = null;
  user: any = null;
  paymentMethod: string = 'CREDIT_CARD';
  amount: number = 0;
  statusMessage: string | null = 'Enter an amount to get started.';
  countdown: number = 0;
  isSubmitted: boolean = false;
  name: string = '';
  stripePublicKey: string = 'pk_test_51THFSrHR7SQGzPyCRTKSkCgRs5ESNHaSxuSKx7AbmyXIHQBz8c0u142Tan0hKCDOfy66Nf33zIlhXXKZ1MDBcdGH00Gt6RIXq1';



  constructor(
    private paymentService: PaymentService,
    private ngZone: NgZone,
    private router: Router
  ) {}

  ngOnInit() {
    const stored = sessionStorage.getItem('loggedInUser');
    if (stored) {
      this.user = JSON.parse(stored);
    }
  }

  ngAfterViewInit() {
    this.initStripe();
  }

  async initStripe() {
    this.stripe = await loadStripe(this.stripePublicKey);
    if (!this.stripe) {
      this.statusMessage = 'Stripe.js failed to load.';
      return;
    }
    this.setupCardElement();
  }



  setupCardElement() {
    if (!this.stripe) return;

    // Only create and mount the card element once.
    // If it already exists, it's already mounted, so no action needed.
    if (this.card) {
      return;
    }

    const elements = this.stripe.elements();
    this.card = elements.create('card');
    const cardElementDiv = document.getElementById('card-element');
    if (cardElementDiv && this.card) {
      this.card.mount(cardElementDiv);
    } else {
        console.error("Could not find '#card-element' div to mount Stripe Card Element.");
    }
  }

  onAmountChange() {
    // The Stripe card element itself does not need to be re-setup when the amount changes.
    // The 'amount' property will be read at the time of `submitPayment`.
  }

  onPaymentMethodChange() {
    // The payment method is currently fixed to 'CREDIT_CARD' in this component.
    // No action needed for this change event in the current implementation.
  }

  async submitPayment() {
    this.isSubmitted = true;
    this.statusMessage = 'Processing payment...';
    if (!this.stripe || !this.card) {
      this.statusMessage = 'Stripe is not initialized. Please refresh.';
      this.isSubmitted = false;
      return;
    }
    const { paymentMethod, error } = await this.stripe.createPaymentMethod({
      type: 'card',
      card: this.card,
      billing_details: {
        name: this.name,
        email: this.user?.email,
      },
    });
    if (error) {
      this.statusMessage = error.message || 'An error occurred with the card details.';
      this.isSubmitted = false;
      return;
    }
    this.paymentService.payWithCard(this.user?.id, this.amount, paymentMethod.id).subscribe({
      next: (res) => {
        this.statusMessage = 'Payment successful!';
        this.startCountdown();
      },
      error: (err) => {
        this.statusMessage = err.error?.message || 'Payment failed. Please try again.';
        this.isSubmitted = false;
      },
    });
  }

  startCountdown() {
    this.countdown = 5;
    const interval = setInterval(() => {
      this.countdown--;
      if (this.countdown <= 0) {
        clearInterval(interval);
        if (this.router) {
          this.router.navigate(['/profile']);
        }
      }
    }, 1000);
  }

  logout() {
    sessionStorage.removeItem('loggedInUser');
    if (this.router) {
      this.router.navigate(['/login']);
    }
  }
}
