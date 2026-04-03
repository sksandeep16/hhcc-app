import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule, CurrencyPipe } from '@angular/common';

@Component({
  selector: 'app-payment-success',
  standalone: true,
  imports: [CommonModule, CurrencyPipe],
  template: `
    <h2>Payment Successful!</h2>
    <p>Thank you for your payment.</p>
    <p *ngIf="method && amount">Method: {{ method }}<br>Amount: {{ amount | currency:'USD' }}</p>
    <div class="success-warning">
      <strong>Please do not refresh or close the browser until you receive confirmation.</strong>
    </div>
  `
})
export class PaymentSuccessComponent {
  method: string | null = null;
  amount: number | null = null;
  constructor(route: ActivatedRoute) {
    this.method = route.snapshot.queryParamMap.get('method');
    this.amount = Number(route.snapshot.queryParamMap.get('amount'));
  }
}
