import * as dayjs from 'dayjs';
import { IEstreno } from 'app/entities/estreno/estreno.model';
import { IDirector } from 'app/entities/director/director.model';

export interface IPelicula {
  id?: number;
  titulo?: string;
  fechaEstreno?: dayjs.Dayjs | null;
  decripcion?: string | null;
  enCines?: boolean | null;
  estreno?: IEstreno | null;
  director?: IDirector | null;
}

export class Pelicula implements IPelicula {
  constructor(
    public id?: number,
    public titulo?: string,
    public fechaEstreno?: dayjs.Dayjs | null,
    public decripcion?: string | null,
    public enCines?: boolean | null,
    public estreno?: IEstreno | null,
    public director?: IDirector | null
  ) {
    this.enCines = this.enCines ?? false;
  }
}

export function getPeliculaIdentifier(pelicula: IPelicula): number | undefined {
  return pelicula.id;
}
