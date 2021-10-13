import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IPelicula, Pelicula } from '../pelicula.model';

import { PeliculaService } from './pelicula.service';

describe('Pelicula Service', () => {
  let service: PeliculaService;
  let httpMock: HttpTestingController;
  let elemDefault: IPelicula;
  let expectedResult: IPelicula | IPelicula[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(PeliculaService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      titulo: 'AAAAAAA',
      fechaEstreno: currentDate,
      decripcion: 'AAAAAAA',
      enCines: false,
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          fechaEstreno: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Pelicula', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          fechaEstreno: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          fechaEstreno: currentDate,
        },
        returnedFromService
      );

      service.create(new Pelicula()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Pelicula', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          titulo: 'BBBBBB',
          fechaEstreno: currentDate.format(DATE_TIME_FORMAT),
          decripcion: 'BBBBBB',
          enCines: true,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          fechaEstreno: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Pelicula', () => {
      const patchObject = Object.assign(
        {
          enCines: true,
        },
        new Pelicula()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          fechaEstreno: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Pelicula', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          titulo: 'BBBBBB',
          fechaEstreno: currentDate.format(DATE_TIME_FORMAT),
          decripcion: 'BBBBBB',
          enCines: true,
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          fechaEstreno: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Pelicula', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addPeliculaToCollectionIfMissing', () => {
      it('should add a Pelicula to an empty array', () => {
        const pelicula: IPelicula = { id: 123 };
        expectedResult = service.addPeliculaToCollectionIfMissing([], pelicula);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pelicula);
      });

      it('should not add a Pelicula to an array that contains it', () => {
        const pelicula: IPelicula = { id: 123 };
        const peliculaCollection: IPelicula[] = [
          {
            ...pelicula,
          },
          { id: 456 },
        ];
        expectedResult = service.addPeliculaToCollectionIfMissing(peliculaCollection, pelicula);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Pelicula to an array that doesn't contain it", () => {
        const pelicula: IPelicula = { id: 123 };
        const peliculaCollection: IPelicula[] = [{ id: 456 }];
        expectedResult = service.addPeliculaToCollectionIfMissing(peliculaCollection, pelicula);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pelicula);
      });

      it('should add only unique Pelicula to an array', () => {
        const peliculaArray: IPelicula[] = [{ id: 123 }, { id: 456 }, { id: 38136 }];
        const peliculaCollection: IPelicula[] = [{ id: 123 }];
        expectedResult = service.addPeliculaToCollectionIfMissing(peliculaCollection, ...peliculaArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const pelicula: IPelicula = { id: 123 };
        const pelicula2: IPelicula = { id: 456 };
        expectedResult = service.addPeliculaToCollectionIfMissing([], pelicula, pelicula2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(pelicula);
        expect(expectedResult).toContain(pelicula2);
      });

      it('should accept null and undefined values', () => {
        const pelicula: IPelicula = { id: 123 };
        expectedResult = service.addPeliculaToCollectionIfMissing([], null, pelicula, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(pelicula);
      });

      it('should return initial array if no Pelicula is added', () => {
        const peliculaCollection: IPelicula[] = [{ id: 123 }];
        expectedResult = service.addPeliculaToCollectionIfMissing(peliculaCollection, undefined, null);
        expect(expectedResult).toEqual(peliculaCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
