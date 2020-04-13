import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { OrganizationsComponent } from './organizations/organizations.component';
import { PlacesComponent } from './places/places.component';
import { NewplaceComponent } from './newplace/newplace.component';
import { TracksComponent } from './tracks/tracks.component';
import { PlacedetailComponent } from './placedetail/placedetail.component';
import { OrganizationdetailComponent } from './organizationdetail/organizationdetail.component';
import { NewOrganizationComponent } from './new-organization/new-organization.component';
import { AdministratorsComponent } from './administrators/administrators.component';
import { AdministratorsListComponent } from './administrators-list/administrators-list.component';
import { NewAdministratorComponent } from './new-administrator/new-administrator.component';
import { LoginComponent } from './login/login.component';
import { AdministratorDetailsComponent } from './administrator-details/administrator-details.component';
import { AuthGuard } from './_helpers/auth.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'administrator', component: AdministratorDetailsComponent },
  { path: 'administrators', component: AdministratorsComponent,
     children: [
       {path: ':organizationID', component:AdministratorsListComponent},
       {path: ':organizationID/newAdministrator', component: NewAdministratorComponent}
     ]
  },
  { path: 'organizations', component: OrganizationsComponent },
  { path: 'newOrganization', component: NewOrganizationComponent },
  { path: 'organizations/:organization_id/organizationdetail', component: OrganizationdetailComponent },
  { path: 'organizations/:organization_id/places', component: PlacesComponent },
  { path: 'organizations/:organization_id/newplace', component: NewplaceComponent },
  { path: 'organizations/:organization_id/places/:place_id/placedetail', component: PlacedetailComponent },
  { path: 'organizations/:organization_id/places/:place_id/tracks', component: TracksComponent },
  // otherwise redirect to login
  { path: '**', redirectTo: 'login' }
  
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
