import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import * as dayjs from 'dayjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IEstreno, getEstrenoIdentifier } from '../estreno.model';

export type EntityResponseType = HttpResponse<IEstreno>;
export type EntityArrayResponseType = HttpResponse<IEstreno[]>;

@Injectable({ providedIn: 'root' })
export class EstrenoService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/estrenos');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(estreno: IEstreno): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(estreno);
    return this.http
      .post<IEstreno>(this.resourceUrl, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  update(estreno: IEstreno): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(estreno);
    return this.http
      .put<IEstreno>(`${this.resourceUrl}/${getEstrenoIdentifier(estreno) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  partialUpdate(estreno: IEstreno): Observable<EntityResponseType> {
    const copy = this.convertDateFromClient(estreno);
    return this.http
      .patch<IEstreno>(`${this.resourceUrl}/${getEstrenoIdentifier(estreno) as number}`, copy, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http
      .get<IEstreno>(`${this.resourceUrl}/${id}`, { observe: 'response' })
      .pipe(map((res: EntityResponseType) => this.convertDateFromServer(res)));
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http
      .get<IEstreno[]>(this.resourceUrl, { params: options, observe: 'response' })
      .pipe(map((res: EntityArrayResponseType) => this.convertDateArrayFromServer(res)));
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addEstrenoToCollectionIfMissing(estrenoCollection: IEstreno[], ...estrenosToCheck: (IEstreno | null | undefined)[]): IEstreno[] {
    const estrenos: IEstreno[] = estrenosToCheck.filter(isPresent);
    if (estrenos.length > 0) {
      const estrenoCollectionIdentifiers = estrenoCollection.map(estrenoItem => getEstrenoIdentifier(estrenoItem)!);
      const estrenosToAdd = estrenos.filter(estrenoItem => {
        const estrenoIdentifier = getEstrenoIdentifier(estrenoItem);
        if (estrenoIdentifier == null || estrenoCollectionIdentifiers.includes(estrenoIdentifier)) {
          return false;
        }
        estrenoCollectionIdentifiers.push(estrenoIdentifier);
        return true;
      });
      return [...estrenosToAdd, ...estrenoCollection];
    }
    return estrenoCollection;
  }

  protected convertDateFromClient(estreno: IEstreno): IEstreno {
    return Object.assign({}, estreno, {
      fecha: estreno.fecha?.isValid() ? estreno.fecha.toJSON() : undefined,
    });
  }

  protected convertDateFromServer(res: EntityResponseType): EntityResponseType {
    if (res.body) {
      res.body.fecha = res.body.fecha ? dayjs(res.body.fecha) : undefined;
    }
    return res;
  }

  protected convertDateArrayFromServer(res: EntityArrayResponseType): EntityArrayResponseType {
    if (res.body) {
      res.body.forEach((estreno: IEstreno) => {
        estreno.fecha = estreno.fecha ? dayjs(estreno.fecha) : undefined;
      });
    }
    return res;
  }
}
