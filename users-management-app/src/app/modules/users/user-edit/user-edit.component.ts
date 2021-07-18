import {
  HttpErrorResponse,
  HttpEvent,
  HttpEventType,
} from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { NotificationType } from '@users-management/shared/enums/notification-type.enum';
import { FileUploadStatus } from '@users-management/shared/models/file-upload.status';
import { User } from '@users-management/shared/models/user.model';
import { NotificationService } from '@users-management/shared/services/notification.service';
import { UserService } from '@users-management/shared/services/user.service';
import { MdbModalRef } from 'mdb-angular-ui-kit/modal';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-user-edit',
  templateUrl: './user-edit.component.html',
  styleUrls: ['./user-edit.component.scss'],
})
export class UserEditComponent implements OnInit {
  public title: string;
  public fileName: string;
  public profileImage: File;
  public fileStatus = new FileUploadStatus();
  private subscriptions: Subscription[] = [];
  public users: User[];
  public user: User;
  public editUser;
  private currentUsername: string;
  public refreshing: boolean;

  constructor(
    public modalRef: MdbModalRef<UserEditComponent>,
    public userService: UserService,
    private notificationService: NotificationService
  ) {}
  ngOnInit(): void {
    this.title = `${this.editUser.firstName} ${this.editUser.lastName}`;
  }

  close(): void {
    this.modalRef.close();
  }

  public onUpdateUser(): void {
    const formData = this.userService.createUserFormDate(
      this.currentUsername,
      this.editUser,
      this.profileImage
    );
    this.subscriptions.push(
      this.userService.updateUser(formData).subscribe(
        (response: User) => {
          this.close();
          this.getUsers(false);
          this.fileName = null;
          this.profileImage = null;
          this.sendNotification(
            NotificationType.SUCCESS,
            `${response.firstName} ${response.lastName} updated successfully`
          );
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
          this.profileImage = null;
        }
      )
    );
  }

  public onProfileImageChange(fileName: string, profileImage: File): void {
    this.fileName = fileName;
    this.profileImage = profileImage;
  }

  public onUpdateProfileImage(): void {
    const formData = new FormData();
    formData.append('username', this.user.username);
    formData.append('profileImage', this.profileImage);
    this.subscriptions.push(
      this.userService.updateProfileImage(formData).subscribe(
        (event: HttpEvent<any>) => {
          this.reportUploadProgress(event);
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
          this.fileStatus.status = 'done';
        }
      )
    );
  }

  private reportUploadProgress(event: HttpEvent<any>): void {
    switch (event.type) {
      case HttpEventType.UploadProgress:
        this.fileStatus.percentage = Math.round(
          (100 * event.loaded) / event.total
        );
        this.fileStatus.status = 'progress';
        break;
      case HttpEventType.Response:
        if (event.status === 200) {
          this.user.profileImageUrl = `${
            event.body.profileImageUrl
          }?time=${new Date().getTime()}`;
          this.sendNotification(
            NotificationType.SUCCESS,
            `${event.body.firstName}\'s profile image updated successfully`
          );
          this.fileStatus.status = 'done';
          break;
        } else {
          this.sendNotification(
            NotificationType.ERROR,
            `Unable to upload image. Please try again`
          );
          break;
        }
      default:
        `Finished all processes`;
    }
  }

  public getUsers(showNotification: boolean): void {
    this.refreshing = true;
    this.subscriptions.push(
      this.userService.getUsers().subscribe(
        (response: User[]) => {
          this.userService.addUsersToLocalCache(response);
          this.users = response;
          this.refreshing = false;
          if (showNotification) {
            this.sendNotification(
              NotificationType.SUCCESS,
              `${response.length} user(s) loaded successfully.`
            );
          }
        },
        (errorResponse: HttpErrorResponse) => {
          this.sendNotification(
            NotificationType.ERROR,
            errorResponse.error.message
          );
          this.refreshing = false;
        }
      )
    );
  }

  private sendNotification(
    notificationType: NotificationType,
    message: string
  ): void {
    if (message) {
      this.notificationService.notify(notificationType, message);
    } else {
      this.notificationService.notify(
        notificationType,
        'An error occurred. Please try again.'
      );
    }
  }
}
