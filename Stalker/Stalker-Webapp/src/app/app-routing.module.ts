import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { OrganizationsComponent } from './organizations/organizations.component';
import {PlacesComponent} from './places/places.component';
import {TracksComponent} from './tracks/tracks.component';
import {PlacedetailComponent} from './placedetail/placedetail.component';
import { OrganizationdetailComponent } from './organizationdetail/organizationdetail.component';

const routes: Routes = [
  { path: 'organizations', component: OrganizationsComponent },
  { path: 'organizations/:organization_id/organizationdetail', component: OrganizationdetailComponent },
  { path: 'organizations/:organization_id/places', component: PlacesComponent },
  { path: 'organizations/:organization_id/places/:place_id/placedetail', component: PlacedetailComponent },
  { path: 'organizations/:organization_id/places/:place_id/tracks', component: TracksComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
