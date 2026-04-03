import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { RouterTestingModule } from '@angular/router/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { LoginComponent } from './login.component';

describe('LoginComponent', () => {
  let component: LoginComponent;
  let fixture: ComponentFixture<LoginComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [LoginComponent, ReactiveFormsModule, RouterTestingModule, HttpClientTestingModule]
    }).compileComponents();

    fixture   = TestBed.createComponent(LoginComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should initialise with invalid form', () => {
    expect(component.form.valid).toBeFalse();
  });

  it('should be invalid when username is empty', () => {
    component.form.patchValue({ username: '', password: 'pass1234' });
    expect(component.form.get('username')?.invalid).toBeTrue();
  });

  it('should be invalid when password is empty', () => {
    component.form.patchValue({ username: 'john_doe', password: '' });
    expect(component.form.get('password')?.invalid).toBeTrue();
  });

  it('should be valid when both fields are filled', () => {
    component.form.patchValue({ username: 'john_doe', password: 'pass1234' });
    expect(component.form.valid).toBeTrue();
  });

  it('should mark form as touched on submit when invalid', () => {
    component.onSubmit();
    expect(component.form.get('username')?.touched).toBeTrue();
    expect(component.form.get('password')?.touched).toBeTrue();
  });
});
