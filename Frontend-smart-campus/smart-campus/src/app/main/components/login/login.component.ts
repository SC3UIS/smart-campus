import { Component } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { takeUntil, take } from 'rxjs/operators';

import { AppService } from 'src/app/app.service';
import { Subscribable } from 'src/app/shared/utils/subscribable';
import { User } from 'src/app/shared/models/user';
import { UserService } from 'src/app/core/services/user.service';

/**
 * Login component, embedded inside Authentication component.
 *
 * @date 2019-04-09
 * @export
 */
@Component({
  selector: 'sc-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent extends Subscribable {

  /**
   * User used as input for the login form.
   *
   */
  public login: User = new User();

  /**
   * Creates an instance of LoginComponent.
   * @date 2019-04-09
   * @param appService - Application main service.
   * @param userService - Users management service.
   */
  constructor(public appService: AppService, public userService: UserService) {
    super();
  }

  /**
   * Executed when pressing the 'Login' button.
   * It consumes the login REST service to authenticate the user,
   * if the authentication succeeded the user is redirected to the dashboard.
   *
   * @date 2019-04-09
   */
  public doLogin(): void {
    this.appService.isBusy = true;
    this.userService.login(this.login)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (user: User) => this.appService.authenticate(user),
        (err: HttpErrorResponse) => this.appService.handleGenericError(err));
  }

}
