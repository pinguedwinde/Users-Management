import { NotificationService } from './../../../shared/services/notification.service';
import { UserService } from '@users-management/shared/services/user.service';
import { Component, OnInit } from '@angular/core';
import { MdbModalRef } from 'mdb-angular-ui-kit/modal';
import { FileUploadStatus } from '@users-management/shared/models/file-upload.status';
import {
  HttpErrorResponse,
  HttpEvent,
  HttpEventType,
} from '@angular/common/http';
import { NgForm } from '@angular/forms';
import { NotificationType } from '@users-management/shared/enums/notification-type.enum';
import { User } from '@users-management/shared/models/user.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-user-add',
  templateUrl: './user-add.component.html',
  styleUrls: ['./user-add.component.scss'],
})
export class UserAddComponent implements OnInit {
  public fileName: string;
  public profileImage: File;
  public fileStatus = new FileUploadStatus();
  private subscriptions: Subscription[] = [];
  public users: User[];
  public user: User;
  public refreshing: boolean;

  constructor(
    public modalRef: MdbModalRef<UserAddComponent>,
    public userService: UserService,
    private notificationService: NotificationService
  ) {}
  ngOnInit(): void {}

  close(): void {
    this.modalRef.close();
  }

  public onAddNewUser(userForm: NgForm): void {
    const formData = this.userService.createUserFormDate(
      null,
      userForm.value,
      this.profileImage
    );
    this.subscriptions.push(
      this.userService.addUser(formData).subscribe(
        (response: User) => {
          this.close();
          this.getUsers(false);
          this.fileName = null;
          this.profileImage = null;
          userForm.reset();
          this.sendNotification(
            NotificationType.SUCCESS,
            `${response.firstName} ${response.lastName} added successfully`
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

  public saveNewUser(): void {
    this.clickButton('new-user-save');
  }

  public onProfileImageChange(fileName: string, profileImage: File): void {
    this.fileName = fileName;
    this.profileImage = profileImage;
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
  private clickButton(buttonId: string): void {
    document.getElementById(buttonId).click();
  }
}
