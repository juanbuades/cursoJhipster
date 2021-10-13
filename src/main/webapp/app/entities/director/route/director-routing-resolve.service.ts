import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IDirector, Director } from '../director.model';
import { DirectorService } from '../service/director.service';

@Injectable({ providedIn: 'root' })
export class DirectorRoutingResolveService implements Resolve<IDirector> {
  constructor(protected service: DirectorService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IDirector> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((director: HttpResponse<Director>) => {
          if (director.body) {
            return of(director.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Director());
  }
}
