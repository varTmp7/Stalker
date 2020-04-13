import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { MainNavComponent } from './main-nav/main-nav.component';
import { LayoutModule } from '@angular/cdk/layout';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatIconModule } from '@angular/material/icon';
import { MatListModule } from '@angular/material/list';
import { MatTableModule } from '@angular/material/table';
import { MatCardModule } from '@angular/material/card';
import { MatExpansionModule } from '@angular/material/expansion';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { OrganizationsComponent } from './organizations/organizations.component';
import { PlacesComponent } from './places/places.component';
import { TracksComponent } from './tracks/tracks.component';
import { PlacedetailComponent } from './placedetail/placedetail.component';
import { OrganizationdetailComponent } from './organizationdetail/organizationdetail.component';
import {ReactiveFormsModule} from '@angular/forms';
//impor { AdministratorsComponent } from './administrators/administrators.component';
import { AdministratorsListComponent } from './administrators-list/administrators-list.component';
import { MatSelectModule } from '@angular/material/select';
import { NewAdministratorComponent } from './new-administrator/new-administrator.component';
import { LoginComponent } from './login/login.component';
import { JwtInterceptor } from './_helpers/jwt.interceptor';
import { ErrorInterceptor } from './_helpers/error.interceptor';

// used to create fake backend
import { fakeBackendProvider } from './_helpers/fake-backend';
//import { AdministratordetailComponent } from './administratordetail/administratordetail.component';
import { AdministratorsComponent } from './administrators/administrators.component';
import { AdministratorDetailsComponent } from './administrator-details/administrator-details.component';
import { NewOrganizationComponent } from './new-organization/new-organization.component';
import { NewplaceComponent } from './newplace/newplace.component';

@NgModule({
  declarations: [
    AppComponent,
    MainNavComponent,
    OrganizationsComponent,
    PlacesComponent,
    TracksComponent,
    PlacedetailComponent,
    OrganizationdetailComponent,
    AdministratorsComponent,
    AdministratorsListComponent,
    NewAdministratorComponent,
    LoginComponent,
    AdministratorDetailsComponent,
    NewOrganizationComponent,
    NewplaceComponent,
   // AdministratordetailComponent,
    //AdministratornewComponent,
    //AdministratorsComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    LayoutModule,
    MatToolbarModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatListModule,
    MatTableModule,
    MatCardModule,
    MatExpansionModule,
    MatFormFieldModule,
    MatSelectModule,
    MatInputModule,
    ReactiveFormsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },

    // provider used to create fake backend
    // fakeBackendProvider
    ],
  bootstrap: [AppComponent]
})
export class AppModule { }
