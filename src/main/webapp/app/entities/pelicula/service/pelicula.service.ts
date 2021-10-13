import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IPelicula, getPeliculaIdentifier } from '../pelicula.model';

export type EntityResponseType = HttpResponse<IPelicula>;
export type EntityArrayResponseType = HttpResponse<IPelicula[]>;

@Injectable({ providedIn: 'root' })
export class PeliculaService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/peliculas');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(pelicula: IPelicula): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pelicula);
    return this.http
      .post<IPelicula>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(pelicula: IPelicula): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pelicula);
    return this.http
      .put<IPelicula>(`${this.resourceUrl}/${getPeliculaIdentifier(pelicula) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(pelicula: IPelicula): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(pelicula);
    return this.http
      .patch<IPelicula>(`${this.resourceUrl}/${getPeliculaIdentifier(pelicula) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IPelicula>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IPelicula[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addPeliculaToCollectionIfMissing(peliculaCollection: IPelicula[], ...peliculasToCheck: (IPelicula | null | undefined)[]): IPelicula[] {
    const peliculas: IPelicula[] = peliculasToCheck.filter(isPresent);
    if (peliculas.length > 0) {
      const peliculaCollectionIdentifiers = peliculaCollection.map(peliculaItem => getPeliculaIdentifier(peliculaItem)!);
      const peliculasToAdd = peliculas.filter(peliculaItem => {
        const peliculaIdentifier = getPeliculaIdentifier(peliculaItem);
        if (peliculaIdentifier == null || peliculaCollectionIdentifiers.includes(peliculaIdentifier)) {
          return false;
        }
        peliculaCollectionIdentifiers.push(peliculaIdentifier);
        return true;
      });
      return [...peliculasToAdd, ...peliculaCollection];
    }
    return peliculaCollection;
  }

  protected convertDateFromClient(pelicula: IPelicula): IPelicula {
    return Object.assign({}, pelicula, {
      fechaEstreno: pelicula.fechaEstreno?.isValid() ? pelicula.fechaEstreno.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.fechaEstreno = res.body.fechaEstreno ? dayjs(res.body.fechaEstreno) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((pelicula: IPelicula) => {
        pelicula.fechaEstreno = pelicula.fechaEstreno ? dayjs(pelicula.fechaEstreno) : undefined;
      });
    }
    return res;
  }
}
