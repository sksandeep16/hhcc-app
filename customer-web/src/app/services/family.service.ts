import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface FamilyMember {
  id?: number;
  userId?: number;
  firstName: string;
  lastName: string;
  relationship: string;
  dateOfBirth?: string;
}

@Injectable({ providedIn: 'root' })
export class FamilyService {

  private baseUrl = '/api/users';

  constructor(private http: HttpClient) {}

  getAll(userId: number, search?: string, relationship?: string): Observable<FamilyMember[]> {
    let params = new HttpParams();
    if (search)       params = params.set('search', search);
    if (relationship) params = params.set('relationship', relationship);
    return this.http.get<FamilyMember[]>(`${this.baseUrl}/${userId}/family-members`, { params });
  }

  add(userId: number, member: FamilyMember): Observable<FamilyMember> {
    return this.http.post<FamilyMember>(`${this.baseUrl}/${userId}/family-members`, member);
  }

  update(userId: number, memberId: number, member: FamilyMember): Observable<FamilyMember> {
    return this.http.put<FamilyMember>(`${this.baseUrl}/${userId}/family-members/${memberId}`, member);
  }

  delete(userId: number, memberId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${userId}/family-members/${memberId}`);
  }
}
