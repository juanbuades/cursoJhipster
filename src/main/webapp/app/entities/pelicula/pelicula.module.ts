import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { PeliculaComponent } from './list/pelicula.component';
import { PeliculaDetailComponent } from './detail/pelicula-detail.component';
import { PeliculaUpdateComponent } from './update/pelicula-update.component';
import { PeliculaDeleteDialogComponent } from './delete/pelicula-delete-dialog.component';
import { PeliculaRoutingModule } from './route/pelicula-routing.module';

@NgModule({
  imports: [SharedModule, PeliculaRoutingModule],
  declarations: [PeliculaComponent, PeliculaDetailComponent, PeliculaUpdateComponent, PeliculaDeleteDialogComponent],
  entryComponents: [PeliculaDeleteDialogComponent],
})
export class PeliculaModule {}
