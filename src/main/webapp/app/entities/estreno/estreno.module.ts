import { NgModule } from '@angular/core';
import { SharedModule } from 'app/shared/shared.module';
import { EstrenoComponent } from './list/estreno.component';
import { EstrenoDetailComponent } from './detail/estreno-detail.component';
import { EstrenoUpdateComponent } from './update/estreno-update.component';
import { EstrenoDeleteDialogComponent } from './delete/estreno-delete-dialog.component';
import { EstrenoRoutingModule } from './route/estreno-routing.module';

@NgModule({
  imports: [SharedModule, EstrenoRoutingModule],
  declarations: [EstrenoComponent, EstrenoDetailComponent, EstrenoUpdateComponent, EstrenoDeleteDialogComponent],
  entryComponents: [EstrenoDeleteDialogComponent],
})
export class EstrenoModule {}
