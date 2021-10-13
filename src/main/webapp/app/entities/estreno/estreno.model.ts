import * as dayjs from 'dayjs';
import { IPelicula } from 'app/entities/pelicula/pelicula.model';

export interface IEstreno {
  id?: number;
  fecha?: dayjs.Dayjs | null;
  lugar?: string | null;
  pelicula?: IPelicula | null;
}

export class Estreno implements IEstreno {
  constructor(public id?: number, public fecha?: dayjs.Dayjs | null, public lugar?: string | null, public pelicula?: IPelicula | null) {}
}

export function getEstrenoIdentifier(estreno: IEstreno): number | undefined {
  return estreno.id;
}
