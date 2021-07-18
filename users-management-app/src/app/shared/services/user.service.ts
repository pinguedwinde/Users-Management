import { AuthenticationService } from './authentication.service';
import { Injectable } from '@angular/core';
import {
  HttpClient,
  HttpResponse,
  HttpErrorResponse,
  HttpEvent,
} from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from '@users-management-env/environment';
import { User } from '../models/user.model';
import { CustomHttpRespone } from '../models/custom-http-response.model';
import { Role } from '../enums/role.enum';

@Injectable({ providedIn: 'root' })
export class UserService {
  constructor(
    private http: HttpClient,
    private authenticationService: AuthenticationService
  ) {}

  public getUsers(): Observable<User[]> {
    return this.http.get<User[]>(`${environment.API_USER_BASE_URL}/all`);
  }

  public addUser(formData: FormData): Observable<User> {
    return this.http.post<User>(
      `${environment.API_USER_BASE_URL}/add`,
      formData
    );
  }

  public updateUser(formData: FormData): Observable<User> {
    return this.http.put<User>(
      `${environment.API_USER_BASE_URL}/update`,
      formData
    );
  }

  public resetPassword(email: string): Observable<CustomHttpRespone> {
    return this.http.get<CustomHttpRespone>(
      `${environment.API_USER_BASE_URL}/reset-password/${email}`
    );
  }

  public updateProfileImage(formData: FormData): Observable<HttpEvent<User>> {
    return this.http.put<User>(
      `${environment.API_USER_BASE_URL}/update/profile/image`,
      formData,
      { reportProgress: true, observe: 'events' }
    );
  }

  public deleteUser(username: string): Observable<CustomHttpRespone> {
    return this.http.delete<CustomHttpRespone>(
      `${environment.API_USER_BASE_URL}/delete/${username}`
    );
  }

  public addUsersToLocalCache(users: User[]): void {
    localStorage.setItem('users', JSON.stringify(users));
  }

  public getUsersFromLocalCache(): User[] {
    if (localStorage.getItem('users')) {
      return JSON.parse(localStorage.getItem('users'));
    }
    return null;
  }

  public createUserFormDate(
    loggedInUsername: string,
    user: User,
    profileImage: File
  ): FormData {
    const formData = new FormData();
    formData.append('currentUsername', loggedInUsername);
    formData.append('firstName', user.firstName);
    formData.append('lastName', user.lastName);
    formData.append('username', user.username);
    formData.append('email', user.email);
    formData.append('password', user.password);
    formData.append('role', user.role);
    formData.append('profileImage', profileImage);
    formData.append('isEnabled', JSON.stringify(user.enabled));
    formData.append('isNonLocked', JSON.stringify(user.nonLocked));
    return formData;
  }

  public get isAdmin(): boolean {
    return (
      this.getUserRole() === Role.ADMIN ||
      this.getUserRole() === Role.SUPER_ADMIN
    );
  }

  public get isManager(): boolean {
    return this.isAdmin || this.getUserRole() === Role.MANAGER;
  }

  public get isAdminOrManager(): boolean {
    return this.isAdmin || this.isManager;
  }

  private getUserRole(): string {
    return this.authenticationService.getUserFromLocalCache().role;
  }
}
