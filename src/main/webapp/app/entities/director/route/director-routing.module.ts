import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { UserRouteAccessService } from 'app/core/auth/user-route-access.service';
import { DirectorComponent } from '../list/director.component';
import { DirectorDetailComponent } from '../detail/director-detail.component';
import { DirectorUpdateComponent } from '../update/director-update.component';
import { DirectorRoutingResolveService } from './director-routing-resolve.service';

const directorRoute: Routes = [
  {
    path: '',
    component: DirectorComponent,
    data: {
      defaultSort: 'id,asc',
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/view',
    component: DirectorDetailComponent,
    resolve: {
      director: DirectorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: 'new',
    component: DirectorUpdateComponent,
    resolve: {
      director: DirectorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
  {
    path: ':id/edit',
    component: DirectorUpdateComponent,
    resolve: {
      director: DirectorRoutingResolveService,
    },
    canActivate: [UserRouteAccessService],
  },
];

@NgModule({
  imports: [RouterModule.forChild(directorRoute)],
  exports: [RouterModule],
})
export class DirectorRoutingModule {}
