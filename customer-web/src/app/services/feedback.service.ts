import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PageResponse } from './page-response';

export interface Feedback {
  id?: number;
  userId?: number;
  name: string;
  email: string;
  category: string;
  supportType?: string;
  rating?: number;
  message: string;
  status?: string;
  createdAt?: string;
}

export interface AdminStats {
  totalUsers: number;
  totalFamilyMembers: number;
  totalPets: number;
  totalFeedback: number;
}

@Injectable({ providedIn: 'root' })
export class FeedbackService {

  private base = '/api';

  constructor(private http: HttpClient) {}

  submit(fb: Feedback): Observable<Feedback> {
    return this.http.post<Feedback>(`${this.base}/feedback`, fb);
  }

  getAllFeedback(search?: string, status?: string, category?: string,
                page = 0, size = 20): Observable<PageResponse<Feedback>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (search)   params = params.set('search', search);
    if (status)   params = params.set('status', status);
    if (category) params = params.set('category', category);
    return this.http.get<PageResponse<Feedback>>(`${this.base}/admin/feedback`, { params });
  }

  updateStatus(id: number, status: string): Observable<any> {
    return this.http.patch(`${this.base}/admin/feedback/${id}/status`, { status });
  }

  deleteFeedback(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/admin/feedback/${id}`);
  }

  getStats(): Observable<AdminStats> {
    return this.http.get<AdminStats>(`${this.base}/admin/stats`);
  }

  getAllUsers(search?: string, role?: string, page = 0, size = 20): Observable<PageResponse<any>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    if (search) params = params.set('search', search);
    if (role)   params = params.set('role', role);
    return this.http.get<PageResponse<any>>(`${this.base}/admin/users`, { params });
  }
}
