import { NgModule } from '@angular/core';
import { RouterModule } from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'categoria',
        data: { pageTitle: 'Categorias' },
        loadChildren: () => import('./categoria/categoria.module').then(m => m.CategoriaModule),
      },
      {
        path: 'pelicula',
        data: { pageTitle: 'Peliculas' },
        loadChildren: () => import('./pelicula/pelicula.module').then(m => m.PeliculaModule),
      },
      {
        path: 'estreno',
        data: { pageTitle: 'Estrenos' },
        loadChildren: () => import('./estreno/estreno.module').then(m => m.EstrenoModule),
      },
      {
        path: 'director',
        data: { pageTitle: 'Directors' },
        loadChildren: () => import('./director/director.module').then(m => m.DirectorModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
