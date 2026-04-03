import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ServiceBullet {
  id: number;
  bulletText: string;
  sortOrder: number;
}

export interface CareService {
  id: number;
  name: string;
  icon: string;
  description: string;
  imageUrl: string | null;
  sortOrder: number;
  bullets: ServiceBullet[];
}

@Injectable({ providedIn: 'root' })
export class CareServiceApiService {

  private base = '/api/services';

  constructor(private http: HttpClient) {}

  getAll(): Observable<CareService[]> {
    return this.http.get<CareService[]>(this.base);
  }

  getById(id: number): Observable<CareService> {
    return this.http.get<CareService>(`${this.base}/${id}`);
  }
}
