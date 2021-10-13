import { Injectable } from '@angular/core';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';

import { isPresent } from 'app/core/util/operators';
import { ApplicationConfigService } from 'app/core/config/application-config.service';
import { createRequestOption } from 'app/core/request/request-util';
import { IDirector, getDirectorIdentifier } from '../director.model';

export type EntityResponseType = HttpResponse<IDirector>;
export type EntityArrayResponseType = HttpResponse<IDirector[]>;

@Injectable({ providedIn: 'root' })
export class DirectorService {
  protected resourceUrl = this.applicationConfigService.getEndpointFor('api/directors');

  constructor(protected http: HttpClient, protected applicationConfigService: ApplicationConfigService) {}

  create(director: IDirector): Observable<EntityResponseType> {
    return this.http.post<IDirector>(this.resourceUrl, director, { observe: 'response' });
  }

  update(director: IDirector): Observable<EntityResponseType> {
    return this.http.put<IDirector>(`${this.resourceUrl}/${getDirectorIdentifier(director) as number}`, director, { observe: 'response' });
  }

  partialUpdate(director: IDirector): Observable<EntityResponseType> {
    return this.http.patch<IDirector>(`${this.resourceUrl}/${getDirectorIdentifier(director) as number}`, director, {
      observe: 'response',
    });
  }

  find(id: number): Observable<EntityResponseType> {
    return this.http.get<IDirector>(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  query(req?: any): Observable<EntityArrayResponseType> {
    const options = createRequestOption(req);
    return this.http.get<IDirector[]>(this.resourceUrl, { params: options, observe: 'response' });
  }

  delete(id: number): Observable<HttpResponse<{}>> {
    return this.http.delete(`${this.resourceUrl}/${id}`, { observe: 'response' });
  }

  addDirectorToCollectionIfMissing(directorCollection: IDirector[], ...directorsToCheck: (IDirector | null | undefined)[]): IDirector[] {
    const directors: IDirector[] = directorsToCheck.filter(isPresent);
    if (directors.length > 0) {
      const directorCollectionIdentifiers = directorCollection.map(directorItem => getDirectorIdentifier(directorItem)!);
      const directorsToAdd = directors.filter(directorItem => {
        const directorIdentifier = getDirectorIdentifier(directorItem);
        if (directorIdentifier == null || directorCollectionIdentifiers.includes(directorIdentifier)) {
          return false;
        }
        directorCollectionIdentifiers.push(directorIdentifier);
        return true;
      });
      return [...directorsToAdd, ...directorCollection];
    }
    return directorCollection;
  }
}
