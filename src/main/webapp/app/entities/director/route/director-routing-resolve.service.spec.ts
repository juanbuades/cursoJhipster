jest.mock('@angular/router');

import { TestBed } from '@angular/core/testing';
import { HttpResponse } from '@angular/common/http';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRouteSnapshot, Router } from '@angular/router';
import { of } from 'rxjs';

import { IDirector, Director } from '../director.model';
import { DirectorService } from '../service/director.service';

import { DirectorRoutingResolveService } from './director-routing-resolve.service';

describe('Director routing resolve service', () => {
  let mockRouter: Router;
  let mockActivatedRouteSnapshot: ActivatedRouteSnapshot;
  let routingResolveService: DirectorRoutingResolveService;
  let service: DirectorService;
  let resultDirector: IDirector | undefined;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [Router, ActivatedRouteSnapshot],
    });
    mockRouter = TestBed.inject(Router);
    mockActivatedRouteSnapshot = TestBed.inject(ActivatedRouteSnapshot);
    routingResolveService = TestBed.inject(DirectorRoutingResolveService);
    service = TestBed.inject(DirectorService);
    resultDirector = undefined;
  });

  describe('resolve', () => {
    it('should return IDirector returned by find', () => {
      // GIVEN
      service.find = jest.fn(id => of(new HttpResponse({ body: { id } })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultDirector = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultDirector).toEqual({ id: 123 });
    });

    it('should return new IDirector if id is not provided', () => {
      // GIVEN
      service.find = jest.fn();
      mockActivatedRouteSnapshot.params = {};

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultDirector = result;
      });

      // THEN
      expect(service.find).not.toBeCalled();
      expect(resultDirector).toEqual(new Director());
    });

    it('should route to 404 page if data not found in server', () => {
      // GIVEN
      jest.spyOn(service, 'find').mockReturnValue(of(new HttpResponse({ body: null as unknown as Director })));
      mockActivatedRouteSnapshot.params = { id: 123 };

      // WHEN
      routingResolveService.resolve(mockActivatedRouteSnapshot).subscribe(result => {
        resultDirector = result;
      });

      // THEN
      expect(service.find).toBeCalledWith(123);
      expect(resultDirector).toEqual(undefined);
      expect(mockRouter.navigate).toHaveBeenCalledWith(['404']);
    });
  });
});
