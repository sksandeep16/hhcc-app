import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { FamilyService, FamilyMember } from './family.service';

describe('FamilyService', () => {
  let service: FamilyService;
  let http: HttpTestingController;
  const base = 'http://localhost:8081/api/users';

  const mockMember: FamilyMember = {
    id: 1, userId: 10, firstName: 'Mary', lastName: 'Doe', relationship: 'Spouse'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(FamilyService);
    http    = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('should be created', () => expect(service).toBeTruthy());

  it('getAll() should GET family members for a user', () => {
    service.getAll(10).subscribe(list => {
      expect(list.length).toBe(1);
      expect(list[0].firstName).toBe('Mary');
    });
    const req = http.expectOne(`${base}/10/family-members`);
    expect(req.request.method).toBe('GET');
    req.flush([mockMember]);
  });

  it('add() should POST a new family member', () => {
    const newMember: FamilyMember = { firstName: 'Tom', lastName: 'Doe', relationship: 'Child' };
    service.add(10, newMember).subscribe(m => {
      expect(m.firstName).toBe('Tom');
    });
    const req = http.expectOne(`${base}/10/family-members`);
    expect(req.request.method).toBe('POST');
    req.flush({ ...newMember, id: 2, userId: 10 });
  });

  it('update() should PUT updated family member', () => {
    const updated: FamilyMember = { firstName: 'Mary Updated', lastName: 'Doe', relationship: 'Spouse' };
    service.update(10, 1, updated).subscribe(m => {
      expect(m.firstName).toBe('Mary Updated');
    });
    const req = http.expectOne(`${base}/10/family-members/1`);
    expect(req.request.method).toBe('PUT');
    req.flush({ ...mockMember, ...updated });
  });

  it('delete() should DELETE family member by id', () => {
    service.delete(10, 1).subscribe();
    const req = http.expectOne(`${base}/10/family-members/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
