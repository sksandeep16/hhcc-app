import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { ProfileComponent } from './profile.component';
import { UserService } from '../services/user.service';
import { FamilyService } from '../services/family.service';
import { PetService } from '../services/pet.service';

describe('ProfileComponent', () => {
  let component: ProfileComponent;
  let fixture: ComponentFixture<ProfileComponent>;

  const userSpy   = jasmine.createSpyObj('UserService',   ['updateProfile', 'changePassword']);
  const familySpy = jasmine.createSpyObj('FamilyService', ['getAll']);
  const petSpy    = jasmine.createSpyObj('PetService',    ['getAll']);

  const storedUser = { id: 10, username: 'john_doe', email: 'john@example.com', role: 'USER' };

  beforeEach(async () => {
    familySpy.getAll.and.returnValue(of([{ id: 1 }, { id: 2 }]));
    petSpy.getAll.and.returnValue(of([{ id: 1 }]));

    await TestBed.configureTestingModule({
      imports: [ProfileComponent, RouterTestingModule, ReactiveFormsModule],
      providers: [
        { provide: UserService,   useValue: userSpy   },
        { provide: FamilyService, useValue: familySpy },
        { provide: PetService,    useValue: petSpy    }
      ]
    }).compileComponents();

    sessionStorage.setItem('loggedInUser', JSON.stringify(storedUser));

    fixture   = TestBed.createComponent(ProfileComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  afterEach(() => sessionStorage.clear());

  // ── Creation & initialisation ───────────────────────────────────────────

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should read user from sessionStorage on init', () => {
    expect(component.user?.username).toBe('john_doe');
    expect(component.user?.email).toBe('john@example.com');
    expect(component.user?.role).toBe('USER');
  });

  it('should set initial from first letter of username', () => {
    expect(component.initial).toBe('J');
  });

  it('should populate stats from family and pet services', () => {
    expect(component.stats.familyMembers).toBe(2);
    expect(component.stats.pets).toBe(1);
  });

  // ── Edit Profile ────────────────────────────────────────────────────────

  it('startEdit() should set editingProfile to true', () => {
    expect(component.editingProfile).toBeFalse();
    component.startEdit();
    expect(component.editingProfile).toBeTrue();
  });

  it('cancelEdit() should set editingProfile back to false', () => {
    component.startEdit();
    component.cancelEdit();
    expect(component.editingProfile).toBeFalse();
  });

  it('saveProfile() should call userService.updateProfile with form values', fakeAsync(() => {
    const updatedUser = { id: 10, username: 'jane_doe', email: 'jane@example.com', role: 'USER' };
    userSpy.updateProfile.and.returnValue(of(updatedUser));

    component.startEdit();
    component.profileForm.setValue({ username: 'jane_doe', email: 'jane@example.com' });
    component.saveProfile();
    tick();

    expect(userSpy.updateProfile).toHaveBeenCalledWith(10, { username: 'jane_doe', email: 'jane@example.com' });
    expect(component.profileSuccess).toContain('successfully');
    expect(component.user?.username).toBe('jane_doe');
    expect(component.editingProfile).toBeFalse();
  }));

  it('saveProfile() should set profileError on service failure', fakeAsync(() => {
    userSpy.updateProfile.and.returnValue(throwError(() => ({ error: 'Username already taken' })));

    component.startEdit();
    component.profileForm.setValue({ username: 'taken', email: 'john@example.com' });
    component.saveProfile();
    tick();

    expect(component.profileError).toBeTruthy();
    expect(component.profileSuccess).toBe('');
  }));

  it('saveProfile() should not submit when form is invalid', () => {
    component.startEdit();
    component.profileForm.setValue({ username: '', email: 'bad' });
    component.saveProfile();

    expect(userSpy.updateProfile).not.toHaveBeenCalled();
  });

  // ── Change Password ──────────────────────────────────────────────────────

  it('savePassword() should call userService.changePassword with current and new password', fakeAsync(() => {
    userSpy.changePassword.and.returnValue(of('Password changed successfully'));

    component.passwordForm.setValue({
      currentPassword: 'pass1234',
      newPassword:     'newPass99',
      confirmPassword: 'newPass99'
    });
    component.savePassword();
    tick();

    expect(userSpy.changePassword).toHaveBeenCalledWith(10, {
      currentPassword: 'pass1234',
      newPassword:     'newPass99'
    });
    expect(component.passwordSuccess).toContain('successfully');
  }));

  it('savePassword() should set passwordError on service failure', fakeAsync(() => {
    userSpy.changePassword.and.returnValue(
      throwError(() => ({ error: 'Current password is incorrect' }))
    );

    component.passwordForm.setValue({
      currentPassword: 'wrongPass',
      newPassword:     'newPass99',
      confirmPassword: 'newPass99'
    });
    component.savePassword();
    tick();

    expect(component.passwordError).toBeTruthy();
    expect(component.passwordSuccess).toBe('');
  }));

  it('savePassword() should not submit when passwords do not match', () => {
    component.passwordForm.setValue({
      currentPassword: 'pass1234',
      newPassword:     'newPass99',
      confirmPassword: 'different'
    });
    component.savePassword();

    expect(userSpy.changePassword).not.toHaveBeenCalled();
  });

  // ── Logout ───────────────────────────────────────────────────────────────

  it('logout() should remove user from sessionStorage', () => {
    component.logout();
    expect(sessionStorage.getItem('loggedInUser')).toBeNull();
  });
});
