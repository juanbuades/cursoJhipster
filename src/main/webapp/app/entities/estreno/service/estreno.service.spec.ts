import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import * as dayjs from 'dayjs';

import { DATE_TIME_FORMAT } from 'app/config/input.constants';
import { IEstreno, Estreno } from '../estreno.model';

import { EstrenoService } from './estreno.service';

describe('Estreno Service', () => {
  let service: EstrenoService;
  let httpMock: HttpTestingController;
  let elemDefault: IEstreno;
  let expectedResult: IEstreno | IEstreno[] | boolean | null;
  let currentDate: dayjs.Dayjs;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
    });
    expectedResult = null;
    service = TestBed.inject(EstrenoService);
    httpMock = TestBed.inject(HttpTestingController);
    currentDate = dayjs();

    elemDefault = {
      id: 0,
      fecha: currentDate,
      lugar: 'AAAAAAA',
    };
  });

  describe('Service methods', () => {
    it('should find an element', () => {
      const returnedFromService = Object.assign(
        {
          fecha: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      service.find(123).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(elemDefault);
    });

    it('should create a Estreno', () => {
      const returnedFromService = Object.assign(
        {
          id: 0,
          fecha: currentDate.format(DATE_TIME_FORMAT),
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          fecha: currentDate,
        },
        returnedFromService
      );

      service.create(new Estreno()).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'POST' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should update a Estreno', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          fecha: currentDate.format(DATE_TIME_FORMAT),
          lugar: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          fecha: currentDate,
        },
        returnedFromService
      );

      service.update(expected).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PUT' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should partial update a Estreno', () => {
      const patchObject = Object.assign(
        {
          lugar: 'BBBBBB',
        },
        new Estreno()
      );

      const returnedFromService = Object.assign(patchObject, elemDefault);

      const expected = Object.assign(
        {
          fecha: currentDate,
        },
        returnedFromService
      );

      service.partialUpdate(patchObject).subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'PATCH' });
      req.flush(returnedFromService);
      expect(expectedResult).toMatchObject(expected);
    });

    it('should return a list of Estreno', () => {
      const returnedFromService = Object.assign(
        {
          id: 1,
          fecha: currentDate.format(DATE_TIME_FORMAT),
          lugar: 'BBBBBB',
        },
        elemDefault
      );

      const expected = Object.assign(
        {
          fecha: currentDate,
        },
        returnedFromService
      );

      service.query().subscribe(resp => (expectedResult = resp.body));

      const req = httpMock.expectOne({ method: 'GET' });
      req.flush([returnedFromService]);
      httpMock.verify();
      expect(expectedResult).toContainEqual(expected);
    });

    it('should delete a Estreno', () => {
      service.delete(123).subscribe(resp => (expectedResult = resp.ok));

      const req = httpMock.expectOne({ method: 'DELETE' });
      req.flush({ status: 200 });
      expect(expectedResult);
    });

    describe('addEstrenoToCollectionIfMissing', () => {
      it('should add a Estreno to an empty array', () => {
        const estreno: IEstreno = { id: 123 };
        expectedResult = service.addEstrenoToCollectionIfMissing([], estreno);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(estreno);
      });

      it('should not add a Estreno to an array that contains it', () => {
        const estreno: IEstreno = { id: 123 };
        const estrenoCollection: IEstreno[] = [
          {
            ...estreno,
          },
          { id: 456 },
        ];
        expectedResult = service.addEstrenoToCollectionIfMissing(estrenoCollection, estreno);
        expect(expectedResult).toHaveLength(2);
      });

      it("should add a Estreno to an array that doesn't contain it", () => {
        const estreno: IEstreno = { id: 123 };
        const estrenoCollection: IEstreno[] = [{ id: 456 }];
        expectedResult = service.addEstrenoToCollectionIfMissing(estrenoCollection, estreno);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(estreno);
      });

      it('should add only unique Estreno to an array', () => {
        const estrenoArray: IEstreno[] = [{ id: 123 }, { id: 456 }, { id: 92643 }];
        const estrenoCollection: IEstreno[] = [{ id: 123 }];
        expectedResult = service.addEstrenoToCollectionIfMissing(estrenoCollection, ...estrenoArray);
        expect(expectedResult).toHaveLength(3);
      });

      it('should accept varargs', () => {
        const estreno: IEstreno = { id: 123 };
        const estreno2: IEstreno = { id: 456 };
        expectedResult = service.addEstrenoToCollectionIfMissing([], estreno, estreno2);
        expect(expectedResult).toHaveLength(2);
        expect(expectedResult).toContain(estreno);
        expect(expectedResult).toContain(estreno2);
      });

      it('should accept null and undefined values', () => {
        const estreno: IEstreno = { id: 123 };
        expectedResult = service.addEstrenoToCollectionIfMissing([], null, estreno, undefined);
        expect(expectedResult).toHaveLength(1);
        expect(expectedResult).toContain(estreno);
      });

      it('should return initial array if no Estreno is added', () => {
        const estrenoCollection: IEstreno[] = [{ id: 123 }];
        expectedResult = service.addEstrenoToCollectionIfMissing(estrenoCollection, undefined, null);
        expect(expectedResult).toEqual(estrenoCollection);
      });
    });
  });

  afterEach(() => {
    httpMock.verify();
  });
});
