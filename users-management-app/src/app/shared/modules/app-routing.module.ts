import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { LoginComponent } from '@users-management/pages/login/login.component';
import { RegisterComponent } from '@users-management/pages/register/register.component';
import { AuthenticationGuard } from '@users-management/shared/guards/authentication.guard';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  {
    path: 'user/management',
    canActivate: [AuthenticationGuard],
    loadChildren: () =>
      import('@users-management/modules/users/users.module').then(
        (m) => m.UsersModule
      ),
  },
  { path: '', redirectTo: '/login', pathMatch: 'full' },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}
