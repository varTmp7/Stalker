import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { LoginComponent } from './login/login.component';
import { AppRoutingModule } from './app-routing.module';
import {FormBuilder, FormsModule, ReactiveFormsModule} from '@angular/forms';
import {HTTP_INTERCEPTORS, HttpClient, HttpClientModule} from '@angular/common/http';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatTableModule} from '@angular/material/table';
import {MatSelectModule} from '@angular/material/select';
import {MatIconModule} from '@angular/material/icon';
import {MatListModule} from '@angular/material/list';
import {MatCardModule} from '@angular/material/card';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatInputModule} from '@angular/material/input';
import {MatButtonModule} from '@angular/material/button';
import { DashboardComponent } from './dashboard/dashboard.component';
import {AuthGuard} from './guards/AuthGuard';
import {TokenInterceptor} from './interceptors/token-interceptor.service';
import { AdminDetailComponent } from './admin-detail/admin-detail.component';
import { AdminListComponent } from './admin-list/admin-list.component';
import { OrganizationDetailComponent } from './organization-detail/organization-detail.component';
import {MatSortModule} from '@angular/material/sort';
import { PlacesListComponent } from './places-list/places-list.component';
import { PlaceDetailComponent } from './place-detail/place-detail.component';
import { TracksListComponent } from './tracks-list/tracks-list.component';
import { PasswordResetComponent } from './password-reset/password-reset.component';
import { ReportComponent } from './report/report.component';
import {MatTabsModule} from "@angular/material/tabs";
import { ReportForPlaceComponent } from './report-for-place/report-for-place.component';
import { ReportForEmployeeComponent } from './report-for-employee/report-for-employee.component';
import {ErrorInterceptor} from "./interceptors/error-interceptor.service";
import {MatDatepickerModule} from "@angular/material/datepicker";
import {MatNativeDateModule} from "@angular/material/core";
import {SatDatepickerModule, SatNativeDateModule} from "saturn-datepicker";
import {MatSlideToggleModule} from "@angular/material/slide-toggle";
import {MatSnackBarModule} from "@angular/material/snack-bar";
import { ApprovePlacesComponent } from './approve-places/approve-places.component';
import {A11yModule} from "@angular/cdk/a11y";
import { MapComponent } from './map/map.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    DashboardComponent,
    AdminDetailComponent,
    AdminListComponent,
    OrganizationDetailComponent,
    PlacesListComponent,
    PlaceDetailComponent,
    PasswordResetComponent,
    TracksListComponent,
    ReportComponent,
    ReportForPlaceComponent,
    ReportForEmployeeComponent,
    MapComponent,
    ApprovePlacesComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    ReactiveFormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatSidenavModule,
    MatIconModule,
    MatToolbarModule,
    MatListModule,
    MatSelectModule,
    FormsModule,
    MatTableModule,
    MatSortModule,
    MatTabsModule,
    MatDatepickerModule,
    MatNativeDateModule,
    SatDatepickerModule,
    SatNativeDateModule,
    MatSlideToggleModule,
    MatSnackBarModule,
    A11yModule
  ],
  providers: [
    AuthGuard,
    { provide: HTTP_INTERCEPTORS, useClass: TokenInterceptor, multi: true},
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true}
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
