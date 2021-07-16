import { MdbModule } from './../../shared/modules/mdb.module';
import { RouterModule } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { NgModule } from '@angular/core';
import { UserComponent } from './user/user.component';
import { CommonModule } from '@angular/common';

import { USERS_ROUTES } from './users.routing';
import { UserInfoComponent } from './user-info/user-info.component';
import { UserAddComponent } from './user-add/user-add.component';
import { UserEditComponent } from './user-edit/user-edit.component';

@NgModule({
  declarations: [UserComponent, UserInfoComponent, UserAddComponent, UserEditComponent],
  imports: [
    FormsModule,
    CommonModule,
    MdbModule,
    RouterModule.forChild(USERS_ROUTES),
  ],
})
export class UsersModule {}
