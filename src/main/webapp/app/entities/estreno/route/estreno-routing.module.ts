import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { EstrenoComponent } from '../list/estreno.component';
import { EstrenoDetailComponent } from '../detail/estreno-detail.component';
import { EstrenoUpdateComponent } from '../update/estreno-update.component';
import { EstrenoRoutingResolveService } from './estreno-routing-resolve.service';

const estrenoRoute: Routes = [
  {
    path: '',
    component: EstrenoComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: EstrenoDetailComponent,
    resolve: {
      estreno: EstrenoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: EstrenoUpdateComponent,
    resolve: {
      estreno: EstrenoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: EstrenoUpdateComponent,
    resolve: {
      estreno: EstrenoRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(estrenoRoute)],
  exports: [RouterModule],
})
export class EstrenoRoutingModule {}
