import { Component, OnInit } from '@angular/core';
import { User } from '@users-management/shared/models/user.model';
import { MdbModalRef } from 'mdb-angular-ui-kit/modal';

@Component({
  selector: 'app-user-info',
  templateUrl: './user-info.component.html',
  styleUrls: ['./user-info.component.scss'],
})
export class UserInfoComponent implements OnInit {
  public selectedUser: User;

  constructor(public modalRef: MdbModalRef<UserInfoComponent>) {}
  ngOnInit(): void {}

  close(): void {
    this.modalRef.close();
  }
}
