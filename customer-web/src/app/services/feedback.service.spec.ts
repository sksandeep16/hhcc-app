import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FeedbackService, Feedback, AdminStats } from './feedback.service';
import { PageResponse } from './page-response';

describe('FeedbackService', () => {
  let service: FeedbackService;
  let http: HttpTestingController;
  const base = 'http://localhost:8081/api';

  const mockFeedback: Feedback = {
    id: 1, userId: 10, name: 'John Doe', email: 'john@example.com',
    category: 'FEEDBACK', message: 'Great service!', status: 'OPEN'
  };

  const mockStats: AdminStats = {
    totalUsers: 10, totalFamilyMembers: 30, totalPets: 23, totalFeedback: 18
  };

  const mockFeedbackPage: PageResponse<Feedback> = {
    content: [mockFeedback],
    page: 0, size: 20,
    totalElements: 1, totalPages: 1, last: true
  };

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(FeedbackService);
    http    = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('should be created', () => expect(service).toBeTruthy());

  it('submit() should POST feedback', () => {
    service.submit(mockFeedback).subscribe(f => {
      expect(f.name).toBe('John Doe');
      expect(f.category).toBe('FEEDBACK');
    });
    const req = http.expectOne(`${base}/feedback`);
    expect(req.request.method).toBe('POST');
    req.flush(mockFeedback);
  });

  it('getAllFeedback() should GET paginated feedback and return PageResponse', () => {
    service.getAllFeedback().subscribe(res => {
      expect(res.content.length).toBe(1);
      expect(res.content[0].message).toBe('Great service!');
      expect(res.totalElements).toBe(1);
    });
    // Service appends ?page=0&size=20 by default
    const req = http.expectOne(r => r.url === `${base}/admin/feedback`);
    expect(req.request.method).toBe('GET');
    expect(req.request.params.get('page')).toBe('0');
    expect(req.request.params.get('size')).toBe('20');
    req.flush(mockFeedbackPage);
  });

  it('getAllFeedback() should pass search and filter params when provided', () => {
    service.getAllFeedback('problem', 'OPEN', 'SUPPORT', 1, 10).subscribe();
    const req = http.expectOne(r => r.url === `${base}/admin/feedback`);
    expect(req.request.params.get('search')).toBe('problem');
    expect(req.request.params.get('status')).toBe('OPEN');
    expect(req.request.params.get('category')).toBe('SUPPORT');
    expect(req.request.params.get('page')).toBe('1');
    expect(req.request.params.get('size')).toBe('10');
    req.flush(mockFeedbackPage);
  });

  it('updateStatus() should PATCH feedback status', () => {
    service.updateStatus(1, 'CLOSED').subscribe();
    const req = http.expectOne(`${base}/admin/feedback/1/status`);
    expect(req.request.method).toBe('PATCH');
    expect(req.request.body).toEqual({ status: 'CLOSED' });
    req.flush('Status updated');
  });

  it('deleteFeedback() should DELETE feedback by id', () => {
    service.deleteFeedback(1).subscribe();
    const req = http.expectOne(`${base}/admin/feedback/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });

  it('getStats() should GET admin stats', () => {
    service.getStats().subscribe(stats => {
      expect(stats.totalUsers).toBe(10);
      expect(stats.totalPets).toBe(23);
    });
    const req = http.expectOne(`${base}/admin/stats`);
    expect(req.request.method).toBe('GET');
    req.flush(mockStats);
  });

  it('getAllUsers() should GET paginated users and return PageResponse', () => {
    const mockUsers = [{ id: 1 }, { id: 2 }];
    const mockUserPage: PageResponse<any> = {
      content: mockUsers, page: 0, size: 20,
      totalElements: 2, totalPages: 1, last: true
    };
    service.getAllUsers().subscribe(res => {
      expect(res.content.length).toBe(2);
      expect(res.totalElements).toBe(2);
    });
    const req = http.expectOne(r => r.url === `${base}/admin/users`);
    expect(req.request.method).toBe('GET');
    req.flush(mockUserPage);
  });
});
