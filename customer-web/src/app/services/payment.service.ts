import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Payment {
  id: number;
  userId: number;
  date: string;
  amount: number;
  status: string;
  method: string;
  receiptUrl?: string;
}

@Injectable({ providedIn: 'root' })
export class PaymentService {
  private baseUrl = '/api/payments'; // Adjust as needed

  constructor(private http: HttpClient) {}

  getPaymentHistory(userId: number): Observable<Payment[]> {
    return this.http.get<Payment[]>(`${this.baseUrl}/history/${userId}`);
  }

  makePayment(userId: number, amount: number | string, method: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/make`, { userId, amount: Number(amount), method, currency: 'USD' });
  }

  makePaymentWithCard(userId: number, amount: number | string, method: string, card: { cardNumber: string; cardExpiry: string; cardCvv: string; cardName: string }): Observable<any> {
    return this.http.post(`${this.baseUrl}/make/card`, { userId, amount: Number(amount), method, currency: 'USD', ...card });
  }

  payWithCard(userId: number, amount: number, paymentMethodId: string): Observable<any> {
    return this.http.post(`${this.baseUrl}/card`, { userId, amount, paymentMethodId });
  }

  getPaymentMethods(userId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.baseUrl}/methods/${userId}`);
  }

  payWithApplePay(userId: number, amount: number, paymentToken: string) {
    return this.http.post(`${this.baseUrl}/applepay`, { userId, amount, paymentToken });
  }

  payWithGPay(userId: number, amount: number, paymentToken: string) {
    return this.http.post(`${this.baseUrl}/gpay`, { userId, amount, paymentToken });
  }

  // Add more methods as needed for real payment integration
}
