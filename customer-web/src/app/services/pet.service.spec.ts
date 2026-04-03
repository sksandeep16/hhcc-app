import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { PetService, Pet } from './pet.service';

describe('PetService', () => {
  let service: PetService;
  let http: HttpTestingController;
  const base = 'http://localhost:8081/api/users';

  const mockPet: Pet = {
    id: 1, userId: 10, name: 'Buddy', species: 'Dog', breed: 'Golden Retriever', gender: 'Male'
  };

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(PetService);
    http    = TestBed.inject(HttpTestingController);
  });

  afterEach(() => http.verify());

  it('should be created', () => expect(service).toBeTruthy());

  it('getAll() should GET all pets for a user', () => {
    service.getAll(10).subscribe(list => {
      expect(list.length).toBe(1);
      expect(list[0].name).toBe('Buddy');
    });
    const req = http.expectOne(`${base}/10/pets`);
    expect(req.request.method).toBe('GET');
    req.flush([mockPet]);
  });

  it('add() should POST a new pet', () => {
    const newPet: Pet = { name: 'Whiskers', species: 'Cat', gender: 'Female' };
    service.add(10, newPet).subscribe(p => {
      expect(p.name).toBe('Whiskers');
    });
    const req = http.expectOne(`${base}/10/pets`);
    expect(req.request.method).toBe('POST');
    req.flush({ ...newPet, id: 2, userId: 10 });
  });

  it('update() should PUT updated pet data', () => {
    const updated: Pet = { name: 'Buddy Updated', species: 'Dog', breed: 'Labrador', gender: 'Male' };
    service.update(10, 1, updated).subscribe(p => {
      expect(p.breed).toBe('Labrador');
    });
    const req = http.expectOne(`${base}/10/pets/1`);
    expect(req.request.method).toBe('PUT');
    req.flush({ ...mockPet, ...updated });
  });

  it('delete() should DELETE pet by id', () => {
    service.delete(10, 1).subscribe();
    const req = http.expectOne(`${base}/10/pets/1`);
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
