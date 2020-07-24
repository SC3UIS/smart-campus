import { Component, OnInit } from '@angular/core';
import { User } from 'src/app/shared/models/user';
import { AppService } from 'src/app/app.service';
import { UserService } from 'src/app/core/services/user.service';
import { takeUntil, take } from 'rxjs/operators';
import { Subscribable } from 'src/app/shared/utils/subscribable';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'sc-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent extends Subscribable implements OnInit {

  public user: User;
  public confirmPassword: string;

  constructor(public appService: AppService, private userService: UserService) {
    super();
  }

  ngOnInit() {
    this.user = { ... this.appService.user };
  }

  public updateProfile(): void {
    this.userService.updateUser(this.user)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (user: User) => {
          this.appService.showSnack('Perfil actualizado satisfactoriamente');
          this.appService.user = user;
          sessionStorage.setItem('user', JSON.stringify(user));
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err)
      );
  }

}
