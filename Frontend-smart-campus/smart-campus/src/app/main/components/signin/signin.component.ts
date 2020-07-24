import { Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { take, takeUntil } from 'rxjs/operators';

import { AppService } from 'src/app/app.service';
import { Subscribable } from 'src/app/shared/utils/subscribable';
import { User } from 'src/app/shared/models/user';
import { UserService } from 'src/app/core/services/user.service';

/**
 * Signin component embedded inside Authentication component.
 * Used to create a new User in the system.
 *
 * @date 2019-04-09
 * @export
 */
@Component({
  selector: 'sc-signin',
  templateUrl: './signin.component.html',
  styleUrls: ['./signin.component.css']
})
export class SigninComponent extends Subscribable {

  /**
   * User used as input for signing in.
   *
   */
  public signin: User = new User();

  /**
   * Stores the password in the confirmation input.
   *
   */
  public passwordCheck: string;

  /**
   * Creates an instance of SigninComponent.
   * @date 2019-04-09
   * @param appService - Application service.
   * @param userService - Users managemente service.
   */
  constructor(public appService: AppService, private userService: UserService) {
    super();
  }

  /**
   * Executed when signing in. Calls the signing (create user) REST service.
   *
   * @date 2019-04-09
   */
  public doSignin(): void {
    this.appService.isBusy = true;
    this.userService.signin(this.signin)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (user: User) => this.appService.authenticate(user),
        (err: HttpErrorResponse) => this.appService.handleGenericError(err));
  }

}
