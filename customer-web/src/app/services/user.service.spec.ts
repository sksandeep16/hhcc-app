import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { UserService, LoginRequest, LoginResponse, RegisterRequest, RegisterResponse } from './user.service';

describe('UserService', () => {
  let service: UserService;
  let http: HttpTestingController;
  const base = 'http://localhost:8081/api/users';

  const mockUser: LoginResponse = {
    id: 1, username: 'john_doe', email: 'john@example.com', role: 'USER'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(UserService);
    http    = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('should be created', () => expect(service).toBeTruthy());

  // ── login ────────────────────────────────────────────────────────────────

  it('login() should POST credentials and return LoginResponse', () => {
    const req: LoginRequest = { username: 'john_doe', password: 'pass1234' };

    service.login(req).subscribe(res => {
      expect(res.id).toBe(1);
      expect(res.username).toBe('john_doe');
      expect(res.role).toBe('USER');
    });

    const r = http.expectOne(`${base}/login`);
    expect(r.request.method).toBe('POST');
    expect(r.request.body).toEqual(req);
    r.flush(mockUser);
  });

  // ── register ─────────────────────────────────────────────────────────────

  it('register() should POST registration data and return RegisterResponse', () => {
    const req: RegisterRequest = { username: 'new_user', email: 'new@example.com', password: 'pass1234' };
    const mockReg: RegisterResponse = { id: 2, username: 'new_user', email: 'new@example.com', role: 'USER' };

    service.register(req).subscribe(res => {
      expect(res.id).toBe(2);
      expect(res.username).toBe('new_user');
    });

    const r = http.expectOne(`${base}/register`);
    expect(r.request.method).toBe('POST');
    r.flush(mockReg);
  });

  // ── getProfile ───────────────────────────────────────────────────────────

  it('getProfile() should GET /users/{id} and return LoginResponse', () => {
    service.getProfile(1).subscribe(res => {
      expect(res.username).toBe('john_doe');
      expect(res.email).toBe('john@example.com');
    });

    const r = http.expectOne(`${base}/1`);
    expect(r.request.method).toBe('GET');
    r.flush(mockUser);
  });

  // ── updateProfile ────────────────────────────────────────────────────────

  it('updateProfile() should PUT /users/{id} with new username and email', () => {
    const update = { username: 'jane_doe', email: 'jane@example.com' };
    const updated: LoginResponse = { id: 1, username: 'jane_doe', email: 'jane@example.com', role: 'USER' };

    service.updateProfile(1, update).subscribe(res => {
      expect(res.username).toBe('jane_doe');
      expect(res.email).toBe('jane@example.com');
    });

    const r = http.expectOne(`${base}/1`);
    expect(r.request.method).toBe('PUT');
    expect(r.request.body).toEqual(update);
    r.flush(updated);
  });

  // ── changePassword ────────────────────────────────────────────────────────

  it('changePassword() should PUT /users/{id}/password and return text response', () => {
    const payload = { currentPassword: 'pass1234', newPassword: 'newPass99' };

    service.changePassword(1, payload).subscribe(res => {
      expect(res).toBe('Password changed successfully');
    });

    const r = http.expectOne(`${base}/1/password`);
    expect(r.request.method).toBe('PUT');
    expect(r.request.body).toEqual(payload);
    r.flush('Password changed successfully');
  });
});
