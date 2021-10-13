jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IPelicula, Pelicula } from '../pelicula.model';
import { PeliculaService } from '../service/pelicula.service';

import { PeliculaRoutingResolveService } from './pelicula-routing-resolve.service';

describe('Pelicula routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: PeliculaRoutingResolveService;
  let service: PeliculaService;
  let resultPelicula: IPelicula | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(PeliculaRoutingResolveService);
    service = TestBed.inject(PeliculaService);
    resultPelicula = undefined;
  });

  describe('resolve', () => {
    it('should return IPelicula returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPelicula = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultPelicula).toEqual({ id: 123 });
    });

    it('should return new IPelicula if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPelicula = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultPelicula).toEqual(new Pelicula());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Pelicula })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultPelicula = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultPelicula).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
