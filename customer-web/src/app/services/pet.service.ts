import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Pet {
  id?: number;
  userId?: number;
  name: string;
  species: string;
  breed?: string;
  dateOfBirth?: string;
  gender: string;
}

@Injectable({ providedIn: 'root' })
export class PetService {

  private baseUrl = '/api/users';

  constructor(private http: HttpClient) {}

  getAll(userId: number, search?: string, species?: string): Observable<Pet[]> {
    let params = new HttpParams();
    if (search)  params = params.set('search', search);
    if (species) params = params.set('species', species);
    return this.http.get<Pet[]>(`${this.baseUrl}/${userId}/pets`, { params });
  }

  add(userId: number, pet: Pet): Observable<Pet> {
    return this.http.post<Pet>(`${this.baseUrl}/${userId}/pets`, pet);
  }

  update(userId: number, petId: number, pet: Pet): Observable<Pet> {
    return this.http.put<Pet>(`${this.baseUrl}/${userId}/pets/${petId}`, pet);
  }

  delete(userId: number, petId: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${userId}/pets/${petId}`);
  }
}
