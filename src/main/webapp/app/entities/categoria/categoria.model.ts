export interface ICategoria {
  id?: number;
  nombre?: string;
  imagenContentType?: string | null;
  imagen?: string | null;
}

export class Categoria implements ICategoria {
  constructor(public id?: number, public nombre?: string, public imagenContentType?: string | null, public imagen?: string | null) {}
}

export function getCategoriaIdentifier(categoria: ICategoria): number | undefined {
  return categoria.id;
}
