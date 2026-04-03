import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule, ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { forkJoin } from 'rxjs';
import { FeedbackService, Feedback, AdminStats } from '../services/feedback.service';
import { FamilyService, FamilyMember } from '../services/family.service';
import { PetService, Pet } from '../services/pet.service';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './admin.component.html',
  styleUrl: './admin.component.css'
})
export class AdminComponent implements OnInit {

  adminUser: { id: number; username: string; role: string } | null = null;

  activeTab = 'overview';

  stats: AdminStats | null = null;

  // ── User Management ────────────────────────────────────────
  userList: any[] = [];
  userSearch = '';
  userRoleFilter = '';
  userPage = 0;
  userSize = 10;
  userTotal = 0;
  userTotalPages = 0;

  selectedUser: any = null;
  userTab = 'family';
  familyMembers: FamilyMember[] = [];
  pets: Pet[] = [];

  // ── Feedback ───────────────────────────────────────────────
  feedbackList: Feedback[] = [];
  feedbackSearch = '';
  feedbackStatusFilter = '';
  feedbackCategoryFilter = '';
  feedbackPage = 0;
  feedbackSize = 10;
  feedbackTotal = 0;
  feedbackTotalPages = 0;
  statusOptions = ['OPEN', 'IN_PROGRESS', 'CLOSED'];

  showForm = false;
  formType = '';
  editingId: number | null = null;
  form!: FormGroup;
  loading = false;
  successMessage = '';
  errorMessage = '';

  relationships = ['Spouse', 'Child', 'Parent', 'Sibling', 'Grandparent', 'Grandchild', 'Other'];
  speciesList   = ['Dog', 'Cat', 'Bird', 'Fish', 'Rabbit', 'Hamster', 'Turtle', 'Other'];
  genderList    = ['Male', 'Female', 'Unknown'];

  constructor(
    private fb: FormBuilder,
    private feedbackService: FeedbackService,
    private familyService: FamilyService,
    private petService: PetService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const stored = sessionStorage.getItem('loggedInUser');
    if (!stored) { this.router.navigate(['/login']); return; }
    this.adminUser = JSON.parse(stored);
    if (this.adminUser?.role !== 'ADMIN') { this.router.navigate(['/profile']); return; }
    this.feedbackService.getStats().subscribe(s => this.stats = s);
    this.loadFeedback();
    this.loadUsers();
  }

  // ── Load data ─────────────────────────────────────────────

  loadUsers(): void {
    this.feedbackService.getAllUsers(
      this.userSearch || undefined,
      this.userRoleFilter || undefined,
      this.userPage, this.userSize
    ).subscribe(res => {
      this.userList       = res.content;
      this.userTotal      = res.totalElements;
      this.userTotalPages = res.totalPages;
    });
  }

  loadFeedback(): void {
    this.feedbackService.getAllFeedback(
      this.feedbackSearch || undefined,
      this.feedbackStatusFilter || undefined,
      this.feedbackCategoryFilter || undefined,
      this.feedbackPage, this.feedbackSize
    ).subscribe(res => {
      this.feedbackList       = res.content;
      this.feedbackTotal      = res.totalElements;
      this.feedbackTotalPages = res.totalPages;
    });
  }

  // ── Search handlers ────────────────────────────────────────

  onUserSearch(): void     { this.userPage = 0;     this.loadUsers(); }
  onFeedbackSearch(): void { this.feedbackPage = 0; this.loadFeedback(); }

  // ── Pagination ─────────────────────────────────────────────

  userPrevPage(): void     { if (this.userPage     > 0) { this.userPage--;     this.loadUsers(); } }
  userNextPage(): void     { if (this.userPage     < this.userTotalPages - 1) { this.userPage++;     this.loadUsers(); } }
  feedbackPrevPage(): void { if (this.feedbackPage > 0) { this.feedbackPage--; this.loadFeedback(); } }
  feedbackNextPage(): void { if (this.feedbackPage < this.feedbackTotalPages - 1) { this.feedbackPage++; this.loadFeedback(); } }

  pageArray(total: number): number[] { return Array.from({ length: total }, (_, i) => i); }

  // ── User selection ─────────────────────────────────────────

  selectUser(u: any): void {
    this.selectedUser = u;
    this.userTab = 'family';
    this.closeForm();
    this.loadUserData();
  }

  clearUser(): void { this.selectedUser = null; this.closeForm(); }

  loadUserData(): void {
    const uid = this.selectedUser.id;
    forkJoin({
      family: this.familyService.getAll(uid),
      pets:   this.petService.getAll(uid)
    }).subscribe(d => {
      this.familyMembers = d.family;
      this.pets          = d.pets;
    });
  }

  // ── Form builders ──────────────────────────────────────────

  openFamilyForm(member?: FamilyMember): void {
    this.formType = 'family';
    this.editingId = member?.id ?? null;
    this.form = this.fb.group({
      firstName:    [member?.firstName    ?? '', Validators.required],
      lastName:     [member?.lastName     ?? '', Validators.required],
      relationship: [member?.relationship ?? '', Validators.required],
      dateOfBirth:  [member?.dateOfBirth  ?? '']
    });
    this.showForm = true; this.successMessage = ''; this.errorMessage = '';
  }

  openPetForm(pet?: Pet): void {
    this.formType = 'pet';
    this.editingId = pet?.id ?? null;
    this.form = this.fb.group({
      name:        [pet?.name        ?? '', Validators.required],
      species:     [pet?.species     ?? '', Validators.required],
      breed:       [pet?.breed       ?? ''],
      dateOfBirth: [pet?.dateOfBirth ?? ''],
      gender:      [pet?.gender      ?? 'Unknown', Validators.required]
    });
    this.showForm = true; this.successMessage = ''; this.errorMessage = '';
  }

  closeForm(): void { this.showForm = false; this.editingId = null; this.formType = ''; }

  // ── Submit ────────────────────────────────────────────────

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }
    this.loading = true;
    const uid = this.selectedUser.id;
    const val = this.form.value;
    let req$: any;

    if (this.formType === 'family') {
      req$ = this.editingId
        ? this.familyService.update(uid, this.editingId, val)
        : this.familyService.add(uid, val);
    } else if (this.formType === 'pet') {
      req$ = this.editingId
        ? this.petService.update(uid, this.editingId, val)
        : this.petService.add(uid, val);
    }

    req$.subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = this.editingId ? 'Updated successfully!' : 'Added successfully!';
        this.closeForm();
        this.loadUserData();
      },
      error: (err: any) => { this.loading = false; this.errorMessage = err.error?.message || err.error || 'Error saving.'; }
    });
  }

  // ── Delete ────────────────────────────────────────────────

  deleteFamily(id: number): void {
    if (!confirm('Delete this family member?')) return;
    this.familyService.delete(this.selectedUser.id, id).subscribe(() => this.loadUserData());
  }
  deletePet(id: number): void {
    if (!confirm('Delete this pet?')) return;
    this.petService.delete(this.selectedUser.id, id).subscribe(() => this.loadUserData());
  }

  // ── Feedback actions ──────────────────────────────────────

  updateFeedbackStatus(id: number, status: string): void {
    this.feedbackService.updateStatus(id, status).subscribe(() => this.loadFeedback());
  }
  deleteFeedback(id: number): void {
    if (!confirm('Delete this feedback?')) return;
    this.feedbackService.deleteFeedback(id).subscribe(() => this.loadFeedback());
  }

  // ── Helpers ───────────────────────────────────────────────

  statusClass(s: string): string {
    return s === 'CLOSED' || s === 'COMPLETED' ? 'closed' : s === 'IN_PROGRESS' || s === 'SCHEDULED' ? 'scheduled' : 'open';
  }
  stars(n: number | undefined): string { return n ? '★'.repeat(n) + '☆'.repeat(5 - n) : '—'; }
  logout(): void { sessionStorage.removeItem('loggedInUser'); this.router.navigate(['/login']); }
}
