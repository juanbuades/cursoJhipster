import { Injectable } from '@angular/core';
import { HttpResponse } from '@angular/common/http';
import { Resolve, ActivatedRouteSnapshot, Router } from '@angular/router';
import { Observable, of, EMPTY } from 'rxjs';
import { mergeMap } from 'rxjs/operators';

import { IPelicula, Pelicula } from '../pelicula.model';
import { PeliculaService } from '../service/pelicula.service';

@Injectable({ providedIn: 'root' })
export class PeliculaRoutingResolveService implements Resolve<IPelicula> {
  constructor(protected service: PeliculaService, protected router: Router) {}

  resolve(route: ActivatedRouteSnapshot): Observable<IPelicula> | Observable<never> {
    const id = route.params['id'];
    if (id) {
      return this.service.find(id).pipe(
        mergeMap((pelicula: HttpResponse<Pelicula>) => {
          if (pelicula.body) {
            return of(pelicula.body);
          } else {
            this.router.navigate(['404']);
            return EMPTY;
          }
        })
      );
    }
    return of(new Pelicula());
  }
}
