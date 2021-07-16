import { AuthenticationService } from './../services/authentication.service';
import { InjectFlags } from '@angular/core';
import { Role } from '../enums/role.enum';

export class User {
  public userId: string;
  public firstName: string;
  public lastName: string;
  public username: string;
  public email: string;
  public password: string;
  public lastLoginDate: Date;
  public lastLoginDateDisplay: Date;
  public joinDate: Date;
  public profileImageUrl: string;
  public enabled: boolean;
  public nonLocked: boolean;
  public role: string;
  public authorities: [];

  private authenticationService: AuthenticationService;

  constructor() {
    this.userId = '';
    this.firstName = '';
    this.lastName = '';
    this.username = '';
    this.email = '';
    this.lastLoginDate = null;
    this.lastLoginDateDisplay = null;
    this.joinDate = null;
    this.profileImageUrl = '';
    this.enabled = false;
    this.nonLocked = false;
    this.role = '';
    this.authorities = [];
  }
}
