import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { FeedbackService } from '../services/feedback.service';

@Component({
  selector: 'app-feedback',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './feedback.component.html',
  styleUrl: './feedback.component.css'
})
export class FeedbackComponent implements OnInit {

  form!: FormGroup;
  loading = false;
  submitted = false;
  errorMessage = '';
  category = 'FEEDBACK';

  user: { id: number; username: string; email: string } | null = null;

  constructor(private fb: FormBuilder, private feedbackService: FeedbackService) {}

  ngOnInit(): void {
    const stored = sessionStorage.getItem('loggedInUser');
    if (stored) this.user = JSON.parse(stored);
    this.buildForm();
  }

  buildForm(): void {
    this.form = this.fb.group({
      name:        [this.user?.username ?? '', Validators.required],
      email:       [this.user?.email ?? '', [Validators.required, Validators.email]],
      category:    ['FEEDBACK', Validators.required],
      supportType: [''],
      rating:      [null],
      message:     ['', [Validators.required, Validators.minLength(10)]]
    });

    this.form.get('category')!.valueChanges.subscribe(val => {
      this.category = val;
      if (val === 'SUPPORT') {
        this.form.get('supportType')!.setValidators(Validators.required);
        this.form.get('rating')!.clearValidators();
      } else {
        this.form.get('supportType')!.clearValidators();
        this.form.get('rating')!.setValidators([Validators.required, Validators.min(1), Validators.max(5)]);
      }
      this.form.get('supportType')!.updateValueAndValidity();
      this.form.get('rating')!.updateValueAndValidity();
    });

    this.form.get('category')!.setValue('FEEDBACK');
  }

  setRating(val: number): void { this.form.get('rating')!.setValue(val); }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;
    const payload = { ...this.form.value, userId: this.user?.id ?? null };
    this.feedbackService.submit(payload).subscribe({
      next: () => { this.loading = false; this.submitted = true; },
      error: () => { this.loading = false; this.errorMessage = 'Failed to submit. Please try again.'; }
    });
  }

  reset(): void { this.submitted = false; this.form.reset(); this.buildForm(); }
}
