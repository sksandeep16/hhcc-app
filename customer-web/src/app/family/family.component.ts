import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { FamilyService, FamilyMember } from '../services/family.service';

@Component({
  selector: 'app-family',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './family.component.html',
  styleUrl: './family.component.css'
})
export class FamilyComponent implements OnInit {

  user: { id: number; username: string; email: string; role: string } | null = null;
  members: FamilyMember[] = [];
  form!: FormGroup;

  showForm = false;
  editingId: number | null = null;
  loading = false;
  successMessage = '';
  errorMessage = '';

  relationships = ['Spouse', 'Child', 'Parent', 'Sibling', 'Grandparent', 'Grandchild', 'Other'];

  constructor(
    private fb: FormBuilder,
    private familyService: FamilyService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const stored = sessionStorage.getItem('loggedInUser');
    if (!stored) { this.router.navigate(['/login']); return; }
    this.user = JSON.parse(stored);
    this.buildForm();
    this.loadMembers();
  }

  buildForm(): void {
    this.form = this.fb.group({
      firstName:    ['', Validators.required],
      lastName:     ['', Validators.required],
      relationship: ['', Validators.required],
      dateOfBirth:  ['']
    });
  }

  loadMembers(): void {
    this.familyService.getAll(this.user!.id).subscribe({
      next: (data) => this.members = data,
      error: () => this.errorMessage = 'Failed to load family members.'
    });
  }

  openAddForm(): void {
    this.editingId = null;
    this.form.reset();
    this.showForm = true;
    this.successMessage = '';
    this.errorMessage = '';
  }

  openEditForm(member: FamilyMember): void {
    this.editingId = member.id!;
    this.form.patchValue({
      firstName:    member.firstName,
      lastName:     member.lastName,
      relationship: member.relationship,
      dateOfBirth:  member.dateOfBirth ?? ''
    });
    this.showForm = true;
    this.successMessage = '';
    this.errorMessage = '';
  }

  cancelForm(): void {
    this.showForm = false;
    this.editingId = null;
    this.form.reset();
  }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    this.loading = true;
    this.successMessage = '';
    this.errorMessage = '';

    const payload: FamilyMember = this.form.value;

    const request$ = this.editingId
      ? this.familyService.update(this.user!.id, this.editingId, payload)
      : this.familyService.add(this.user!.id, payload);

    request$.subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = this.editingId ? 'Member updated successfully!' : 'Member added successfully!';
        this.showForm = false;
        this.editingId = null;
        this.form.reset();
        this.loadMembers();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error || 'Something went wrong.';
      }
    });
  }

  deleteMember(memberId: number): void {
    if (!confirm('Are you sure you want to delete this family member?')) return;
    this.familyService.delete(this.user!.id, memberId).subscribe({
      next: () => {
        this.successMessage = 'Member deleted successfully.';
        this.loadMembers();
      },
      error: () => this.errorMessage = 'Failed to delete member.'
    });
  }

  logout(): void {
    sessionStorage.removeItem('loggedInUser');
    this.router.navigate(['/login']);
  }
}
