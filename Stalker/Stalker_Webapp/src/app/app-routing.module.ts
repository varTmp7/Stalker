import { NgModule } from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {LoginComponent} from './login/login.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {AuthGuard} from './guards/AuthGuard';
import {AdminDetailComponent} from './admin-detail/admin-detail.component';
import {AdminListComponent} from './admin-list/admin-list.component';
import {OrganizationDetailComponent} from './organization-detail/organization-detail.component';
import {PlaceDetailComponent} from './place-detail/place-detail.component';
import {PlacesListComponent} from './places-list/places-list.component';
import {ReportComponent} from './report/report.component';
import { PasswordResetComponent } from './password-reset/password-reset.component';
import {ApprovePlacesComponent} from "./approve-places/approve-places.component";

const routes: Routes = [
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard], children: [
      { path: 'dashboard', redirectTo: 'login', pathMatch: 'full'},
      { path: 'create-admin', component: AdminDetailComponent, canActivate: [AuthGuard]},
      { path: 'admin-detail', component: AdminDetailComponent, canActivate: [AuthGuard]},
      { path: 'admins-list', component: AdminListComponent, canActivate: [AuthGuard]},
      { path: 'organization-detail/:organization_id', component: OrganizationDetailComponent, canActivate: [AuthGuard]},
      { path: 'organization-detail/:organization_id/place-list', component: PlacesListComponent, canActivate: [AuthGuard]},
      { path: 'create-organization', component: OrganizationDetailComponent, canActivate: [AuthGuard]},
      { path: 'organization-detail/:organization_id/place-list/:place_id', component: PlaceDetailComponent, canActivate: [AuthGuard]},
      { path: 'create-place', component: PlaceDetailComponent, canActivate: [AuthGuard]},
      { path: 'report', component: ReportComponent, canActivate: [AuthGuard]},
      { path: 'approve-places', component: ApprovePlacesComponent, canActivate: [AuthGuard]}
    ]},
  { path: 'login', component: LoginComponent},
  { path: 'reset', component: PasswordResetComponent},
  { path: '**', redirectTo: 'dashboard'}
];


@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})
export class AppRoutingModule {}
