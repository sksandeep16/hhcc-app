import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
}

export interface RegisterResponse {
  id: number;
  username: string;
  email: string;
  role: string;
}

export interface LoginRequest {
  username: string;
  password: string;
}

export interface LoginResponse {
  id: number;
  username: string;
  email: string;
  role: string;
}

export interface UpdateProfileRequest {
  username: string;
  email: string;
}

export interface ChangePasswordRequest {
  currentPassword: string;
  newPassword: string;
}

@Injectable({ providedIn: 'root' })
export class UserService {

  private baseUrl = '/api/users';

  constructor(private http: HttpClient) {}

  register(data: RegisterRequest): Observable<RegisterResponse> {
    return this.http.post<RegisterResponse>(`${this.baseUrl}/register`, data);
  }

  login(data: LoginRequest): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, data);
  }

  getProfile(id: number): Observable<LoginResponse> {
    return this.http.get<LoginResponse>(`${this.baseUrl}/${id}`);
  }

  updateProfile(id: number, data: UpdateProfileRequest): Observable<LoginResponse> {
    return this.http.put<LoginResponse>(`${this.baseUrl}/${id}`, data);
  }

  changePassword(id: number, data: ChangePasswordRequest): Observable<string> {
    return this.http.put(`${this.baseUrl}/${id}/password`, data, { responseType: 'text' });
  }
}
