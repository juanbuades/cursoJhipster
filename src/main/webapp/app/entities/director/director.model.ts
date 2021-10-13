import { IPelicula } from 'app/entities/pelicula/pelicula.model';

export interface IDirector {
  id?: number;
  nombre?: string | null;
  apellidos?: string | null;
  peliculas?: IPelicula[] | null;
}

export class Director implements IDirector {
  constructor(public id?: number, public nombre?: string | null, public apellidos?: string | null, public peliculas?: IPelicula[] | null) {}
}

export function getDirectorIdentifier(director: IDirector): number | undefined {
  return director.id;
}
