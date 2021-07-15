import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { MdbModule } from './mdb.module';
import { NotificationModule } from './notification.module';

import { AuthenticationGuard } from '../guards/authentication.guard';
import { AuthInterceptor } from '../interceptors/auth.interceptor';

import { AuthenticationService } from '../services/authentication.service';
import { UserService } from '../services/user.service';
import { NotificationService } from '../services/notification.service';

import { LoginComponent } from '@users-management/pages/login/login.component';
import { RegisterComponent } from '@users-management/pages/register/register.component';

const MODULES = [
  CommonModule,
  FormsModule,
  HttpClientModule,
  ReactiveFormsModule,
  AppRoutingModule,
  MdbModule,
  NotificationModule,
];

@NgModule({
  declarations: [LoginComponent, RegisterComponent],
  exports: [LoginComponent, RegisterComponent, ...MODULES],
  imports: MODULES,
  providers: [
    NotificationService,
    AuthenticationGuard,
    AuthenticationService,
    UserService,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
  ],
})
export class CoreModule {}
