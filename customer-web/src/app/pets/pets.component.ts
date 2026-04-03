import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { PetService, Pet } from '../services/pet.service';

@Component({
  selector: 'app-pets',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './pets.component.html',
  styleUrl: './pets.component.css'
})
export class PetsComponent implements OnInit {

  user: { id: number; username: string; email: string; role: string } | null = null;
  pets: Pet[] = [];
  form!: FormGroup;

  showForm = false;
  editingId: number | null = null;
  loading = false;
  successMessage = '';
  errorMessage = '';

  speciesList = ['Dog', 'Cat', 'Bird', 'Fish', 'Rabbit', 'Hamster', 'Turtle', 'Other'];
  genderList  = ['Male', 'Female', 'Unknown'];

  constructor(
    private fb: FormBuilder,
    private petService: PetService,
    private router: Router
  ) {}

  ngOnInit(): void {
    const stored = sessionStorage.getItem('loggedInUser');
    if (!stored) { this.router.navigate(['/login']); return; }
    this.user = JSON.parse(stored);
    this.buildForm();
    this.loadPets();
  }

  buildForm(): void {
    this.form = this.fb.group({
      name:        ['', Validators.required],
      species:     ['', Validators.required],
      breed:       [''],
      dateOfBirth: [''],
      gender:      ['Unknown', Validators.required]
    });
  }

  loadPets(): void {
    this.petService.getAll(this.user!.id).subscribe({
      next: (data) => this.pets = data,
      error: () => this.errorMessage = 'Failed to load pets.'
    });
  }

  openAddForm(): void {
    this.editingId = null;
    this.form.reset({ gender: 'Unknown' });
    this.showForm = true;
    this.successMessage = '';
    this.errorMessage = '';
  }

  openEditForm(pet: Pet): void {
    this.editingId = pet.id!;
    this.form.patchValue({
      name:        pet.name,
      species:     pet.species,
      breed:       pet.breed ?? '',
      dateOfBirth: pet.dateOfBirth ?? '',
      gender:      pet.gender
    });
    this.showForm = true;
    this.successMessage = '';
    this.errorMessage = '';
  }

  cancelForm(): void {
    this.showForm = false;
    this.editingId = null;
    this.form.reset({ gender: 'Unknown' });
  }

  onSubmit(): void {
    if (this.form.invalid) { this.form.markAllAsTouched(); return; }

    this.loading = true;
    this.successMessage = '';
    this.errorMessage = '';

    const payload: Pet = this.form.value;

    const request$ = this.editingId
      ? this.petService.update(this.user!.id, this.editingId, payload)
      : this.petService.add(this.user!.id, payload);

    request$.subscribe({
      next: () => {
        this.loading = false;
        this.successMessage = this.editingId ? 'Pet updated successfully!' : 'Pet added successfully!';
        this.showForm = false;
        this.editingId = null;
        this.form.reset({ gender: 'Unknown' });
        this.loadPets();
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage = err.error || 'Something went wrong.';
      }
    });
  }

  deletePet(petId: number): void {
    if (!confirm('Are you sure you want to remove this pet?')) return;
    this.petService.delete(this.user!.id, petId).subscribe({
      next: () => {
        this.successMessage = 'Pet removed successfully.';
        this.loadPets();
      },
      error: () => this.errorMessage = 'Failed to delete pet.'
    });
  }

  speciesIcon(species: string): string {
    const icons: Record<string, string> = {
      Dog: '🐶', Cat: '🐱', Bird: '🐦', Fish: '🐟',
      Rabbit: '🐰', Hamster: '🐹', Turtle: '🐢', Other: '🐾'
    };
    return icons[species] ?? '🐾';
  }

  logout(): void {
    sessionStorage.removeItem('loggedInUser');
    this.router.navigate(['/login']);
  }
}
