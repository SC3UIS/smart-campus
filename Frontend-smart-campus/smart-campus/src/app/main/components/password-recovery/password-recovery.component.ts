import { Component } from '@angular/core';
import { Subscribable } from 'src/app/shared/utils/subscribable';
import { AppService } from 'src/app/app.service';
import { UserService } from 'src/app/core/services/user.service';
import { take, takeUntil } from 'rxjs/operators';
import { ApiResponse } from 'src/app/shared/models/api-response';
import { HttpErrorResponse } from '@angular/common/http';

/**
 * Password recovery component, embedded inside Authentication component.
 *
 * @date 2019-04-09
 * @export
 */
@Component({
  selector: 'sc-password-recovery',
  templateUrl: './password-recovery.component.html',
  styleUrls: ['./password-recovery.component.css']
})
export class PasswordRecoveryComponent extends Subscribable {

  /**
   * Email of the user that desires that it's password is recovered by an email.
   *
   */
  public email: string;

  /**
   * Creates an instance of PasswordRecoveryComponent.
   * @date 2019-04-09
   * @param appService - Application main service.
   * @param userService - Users managemente service.
   */
  constructor(public appService: AppService, private userService: UserService) {
    super();
  }

  /**
   * Executed when confirmed the password recovery,
   * it consumes the password recovery REST service.
   *
   * @date 2019-04-09
   */
  public doRetrievePassword(): void {
    this.appService.isBusy = true;
    this.userService.recoverPassword(this.email)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (res: ApiResponse) => {
          this.appService.showSnack(res.message);
          this.appService.isBusy = false;
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err));
  }

}
