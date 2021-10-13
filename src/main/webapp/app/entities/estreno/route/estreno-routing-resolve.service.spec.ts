jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IEstreno, Estreno } from '../estreno.model';
import { EstrenoService } from '../service/estreno.service';

import { EstrenoRoutingResolveService } from './estreno-routing-resolve.service';

describe('Estreno routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: EstrenoRoutingResolveService;
  let service: EstrenoService;
  let resultEstreno: IEstreno | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(EstrenoRoutingResolveService);
    service = TestBed.inject(EstrenoService);
    resultEstreno = undefined;
  });

  describe('resolve', () => {
    it('should return IEstreno returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultEstreno = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultEstreno).toEqual({ id: 123 });
    });

    it('should return new IEstreno if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultEstreno = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultEstreno).toEqual(new Estreno());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Estreno })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultEstreno = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultEstreno).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
