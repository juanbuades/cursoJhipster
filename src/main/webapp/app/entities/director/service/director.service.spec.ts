import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { IDirector, Director } from '../director.model';

import { DirectorService } from './director.service';

describe('Director Service', () => {
  let service: DirectorService;
  let httpMock: HttpTestingController;
  let elemDefault: IDirector;
  let expectedResult: IDirector | IDirector[] | boolean | null;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(DirectorService);
    httpMock = TestBed.inject(HttpTestingController);

    elemDefault = {
      id: 0,
      nombre: 'AAAAAAA',
      apellidos: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign({}, elemDefault);

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Director', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.create(new Director()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Director', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nombre: 'BBBBBB',
          apellidos: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Director', () => {
      const patchObject = Object.assign(
        {
          nombre: 'BBBBBB',
        },
        new Director()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign({}, returnedFromService);

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Director', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          nombre: 'BBBBBB',
          apellidos: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign({}, returnedFromService);

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Director', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addDirectorToCollectionIfMissing', () => {
      it('should add a Director to an empty array', () => {
        const director: IDirector = { id: 123 };
        expectedResult = service.addDirectorToCollectionIfMissing([], director);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(director);
      });

      it('should not add a Director to an array that contains it', () => {
        const director: IDirector = { id: 123 };
        const directorCollection: IDirector[] = [
          {
            ...director,
          },
          { id: 456 },
        ];
        expectedResult = service.addDirectorToCollectionIfMissing(directorCollection, director);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Director to an array that doesn't contain it", () => {
        const director: IDirector = { id: 123 };
        const directorCollection: IDirector[] = [{ id: 456 }];
        expectedResult = service.addDirectorToCollectionIfMissing(directorCollection, director);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(director);
      });

      it('should add only unique Director to an array', () => {
        const directorArray: IDirector[] = [{ id: 123 }, { id: 456 }, { id: 95154 }];
        const directorCollection: IDirector[] = [{ id: 123 }];
        expectedResult = service.addDirectorToCollectionIfMissing(directorCollection, ...directorArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const director: IDirector = { id: 123 };
        const director2: IDirector = { id: 456 };
        expectedResult = service.addDirectorToCollectionIfMissing([], director, director2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(director);
        expect(expectedResult).toContain(director2);
      });

      it('should accept null and undefined values', () => {
        const director: IDirector = { id: 123 };
        expectedResult = service.addDirectorToCollectionIfMissing([], null, director, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(director);
      });

      it('should return initial array if no Director is added', () => {
        const directorCollection: IDirector[] = [{ id: 123 }];
        expectedResult = service.addDirectorToCollectionIfMissing(directorCollection, undefined, null);
        expect(expectedResult).toEqual(directorCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
