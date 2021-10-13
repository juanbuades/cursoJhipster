import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { DirectorComponent } from './list/director.component';
import { DirectorDetailComponent } from './detail/director-detail.component';
import { DirectorUpdateComponent } from './update/director-update.component';
import { DirectorDeleteDialogComponent } from './delete/director-delete-dialog.component';
import { DirectorRoutingModule } from './route/director-routing.module';

@NgModule({
  imports: [SharedModule, DirectorRoutingModule],
  declarations: [DirectorComponent, DirectorDetailComponent, DirectorUpdateComponent, DirectorDeleteDialogComponent],
  entryComponents: [DirectorDeleteDialogComponent],
})
export class DirectorModule {}
