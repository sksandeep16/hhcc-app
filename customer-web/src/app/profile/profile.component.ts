import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators, AbstractControl, ValidationErrors } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { forkJoin } from 'rxjs';
import { UserService } from '../services/user.service';
import { FamilyService } from '../services/family.service';
import { PetService } from '../services/pet.service';
import { PaymentService, Payment } from '../services/payment.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {

  user: { id: number; username: string; email: string; role: string } | null = null;
  initial = '';

  stats = { familyMembers: 0, pets: 0 };

  // ── Edit Profile ────────────────────────────────────────────────────────
  profileForm!: FormGroup;
  profileSuccess = '';
  profileError   = '';
  profileLoading = false;
  editingProfile = false;

  // ── Change Password ──────────────────────────────────────────────────────
  passwordForm!: FormGroup;
  passwordSuccess = '';
  passwordError   = '';
  passwordLoading = false;
  showCurrentPw   = false;
  showNewPw       = false;
  showConfirmPw   = false;

  // ── Payment Section ──────────────────────────────────────────────
  paymentHistory: Payment[] = [];
  paymentError = '';
  paymentSuccess = '';
  paymentLoading = false;
  showPaymentModal = false;
  paymentAmount = 0;
  paymentMethod = 'APPLE_PAY'; // Default to Apple Pay
  paymentMethods: any[] = [];
  // Card fields
  cardNumber = '';
  cardExpiry = '';
  cardCvv = '';
  cardName = '';

  constructor(
    private fb: FormBuilder,
    private router: Router,
    private userService: UserService,
    private familyService: FamilyService,
    private petService: PetService,
    private paymentService: PaymentService
  ) {}

  ngOnInit(): void {
    const stored = sessionStorage.getItem('loggedInUser');
    if (!stored) { this.router.navigate(['/login']); return; }
    this.user    = JSON.parse(stored);
    this.initial = this.user?.username?.charAt(0).toUpperCase() ?? '?';
    this.buildForms();
    this.loadStats();
    this.loadPaymentHistory();
    this.loadPaymentMethods();
  }

  // ── Forms ────────────────────────────────────────────────────────────────

  buildForms(): void {
    this.profileForm = this.fb.group({
      username: [this.user?.username ?? '', [Validators.required, Validators.minLength(3), Validators.maxLength(50)]],
      email:    [this.user?.email    ?? '', [Validators.required, Validators.email]]
    });

    this.passwordForm = this.fb.group({
      currentPassword: ['', Validators.required],
      newPassword:     ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', Validators.required]
    }, { validators: this.passwordMatchValidator });
  }

  private passwordMatchValidator(group: AbstractControl): ValidationErrors | null {
    const np = group.get('newPassword')?.value;
    const cp = group.get('confirmPassword')?.value;
    return np && cp && np !== cp ? { passwordMismatch: true } : null;
  }

  // ── Stats ────────────────────────────────────────────────────────────────

  loadStats(): void {
    const uid = this.user!.id;
    forkJoin({
      family: this.familyService.getAll(uid),
      pets:   this.petService.getAll(uid)
    }).subscribe({
      next: (data) => {
        this.stats.familyMembers = data.family.length;
        this.stats.pets          = data.pets.length;
      }
    });
  }

  // ── Edit Profile ─────────────────────────────────────────────────────────

  startEdit(): void {
    this.profileForm.patchValue({ username: this.user?.username, email: this.user?.email });
    this.profileSuccess = '';
    this.profileError   = '';
    this.editingProfile = true;
  }

  cancelEdit(): void {
    this.editingProfile = false;
    this.profileForm.patchValue({ username: this.user?.username, email: this.user?.email });
  }

  saveProfile(): void {
    if (this.profileForm.invalid) { this.profileForm.markAllAsTouched(); return; }
    this.profileLoading = true;
    this.profileSuccess = '';
    this.profileError   = '';

    this.userService.updateProfile(this.user!.id, this.profileForm.value).subscribe({
      next: (updated) => {
        this.profileLoading = false;
        this.profileSuccess = 'Profile updated successfully!';
        this.editingProfile = false;
        this.user = { ...this.user!, username: updated.username, email: updated.email };
        this.initial = updated.username.charAt(0).toUpperCase();
        sessionStorage.setItem('loggedInUser', JSON.stringify(this.user));
      },
      error: (err) => {
        this.profileLoading = false;
        this.profileError = err.error?.message ?? err.error ?? 'Failed to update profile.';
      }
    });
  }

  // ── Change Password ───────────────────────────────────────────────────────

  savePassword(): void {
    if (this.passwordForm.invalid) { this.passwordForm.markAllAsTouched(); return; }
    this.passwordLoading = true;
    this.passwordSuccess = '';
    this.passwordError   = '';

    const { currentPassword, newPassword } = this.passwordForm.value;
    this.userService.changePassword(this.user!.id, { currentPassword, newPassword }).subscribe({
      next: () => {
        this.passwordLoading = false;
        this.passwordSuccess = 'Password changed successfully!';
        this.passwordForm.reset();
      },
      error: (err) => {
        this.passwordLoading = false;
        this.passwordError = err.error?.message ?? err.error ?? 'Failed to change password.';
      }
    });
  }

  // ── Payment ──────────────────────────────────────────────────────────────

  loadPaymentHistory(): void {
    if (!this.user) return;
    this.paymentService.getPaymentHistory(this.user.id).subscribe({
      next: (history) => { this.paymentHistory = history; },
      error: (err) => { this.paymentError = 'Failed to load payment history.'; }
    });
  }

  loadPaymentMethods(): void {
    if (!this.user) return;
    this.paymentService.getPaymentMethods(this.user.id).subscribe({
      next: (methods) => { this.paymentMethods = methods; },
      error: () => { /* ignore for now */ }
    });
  }

  openPaymentModal(): void {
    this.showPaymentModal = true;
    this.paymentAmount = 0;
    this.paymentMethod = 'APPLE_PAY';
    this.paymentError = '';
    this.paymentSuccess = '';
    this.cardNumber = '';
    this.cardExpiry = '';
    this.cardCvv = '';
    this.cardName = '';
  }

  closePaymentModal(): void {
    this.showPaymentModal = false;
  }

  makePayment(): void {
    if (!this.user || !this.paymentAmount || !this.paymentMethod) {
      this.paymentError = 'Please enter amount and select a payment method.';
      return;
    }
    this.paymentLoading = true;
    this.paymentError = '';
    this.paymentSuccess = '';
    if (this.paymentMethod === 'APPLE_PAY') {
      this.payWithApplePay();
      return;
    }
    if (this.paymentMethod === 'GOOGLE_PAY') {
      this.payWithGooglePay();
      return;
    }
    if (this.paymentMethod === 'CREDIT_CARD' || this.paymentMethod === 'DEBIT_CARD') {
      // Simple validation
      if (!this.cardNumber || !this.cardExpiry || !this.cardCvv || !this.cardName) {
        this.paymentLoading = false;
        this.paymentError = 'Please fill all card details.';
        return;
      }
      // Optionally: Add more validation for card format, expiry, etc.
      this.paymentService.makePaymentWithCard(
        this.user.id,
        this.paymentAmount,
        this.paymentMethod,
        {
          cardNumber: this.cardNumber,
          cardExpiry: this.cardExpiry,
          cardCvv: this.cardCvv,
          cardName: this.cardName
        }
      ).subscribe({
        next: (res) => {
          this.paymentLoading = false;
          this.paymentSuccess = 'Payment successful!';
          this.closePaymentModal();
          this.loadPaymentHistory();
        },
        error: (err) => {
          this.paymentLoading = false;
          this.paymentError = err.error?.message || 'Payment failed.';
        }
      });
      return;
    }
    this.paymentLoading = false;
    this.paymentError = 'Unsupported payment method.';
  }

  payWithApplePay(): void {
    // Simulate Apple Pay (replace with real integration as needed)
    setTimeout(() => {
      this.paymentLoading = false;
      this.paymentSuccess = 'Apple Pay payment successful!';
      this.closePaymentModal();
      this.loadPaymentHistory();
    }, 1200);
  }

  payWithGooglePay(): void {
    // Simulate Google Pay (replace with real integration as needed)
    setTimeout(() => {
      this.paymentLoading = false;
      this.paymentSuccess = 'Google Pay payment successful!';
      this.closePaymentModal();
      this.loadPaymentHistory();
    }, 1200);
  }

  managePaymentMethods(): void {
    // Placeholder: open a modal or navigate to payment methods management
    alert('Manage payment methods coming soon!');
  }

  // ── Helpers ───────────────────────────────────────────────────────────────

  get pf() { return this.profileForm.controls; }
  get pwf() { return this.passwordForm.controls; }
  get pwMismatch() { return this.passwordForm.errors?.['passwordMismatch'] && this.pwf['confirmPassword'].touched; }

  logout(): void {
    sessionStorage.removeItem('loggedInUser');
    this.router.navigate(['/login']);
  }
}
