import { HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { MatSnackBar, MatSnackBarConfig } from '@angular/material';
import { NgModel, NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';

import { ApiError } from 'src/app/shared/models/api-error';
import { User } from 'src/app/shared/models/user';
import { Util } from 'src/app/shared/utils/util';

/**
 * Application glogal service, used to store general information and some utility methods required in a singleton class.
 *
 * @date 2019-04-09
 * @export
 */
@Injectable({
  providedIn: 'root'
})
export class AppService {

  /**
   * User logged in, null if no user is logged in yet.
   *
   */
  public user: User;

  /**
   * Indicates if the user's interaction is blocked in the app showing the robot loader if set to true.
   *
   */
  public isBusy: boolean;

  /**
   * Subject emited any time it's necessary to subscribe to the notifications (after authentication succeedd).
   *
   */
  public subscribeToNotification: Subject<any> = new Subject<any>();

  /**
   * Creates an instance of AppService.
   * @date 2019-04-09
   * @param router - Angular router.
   * @param snackBar - Angular snackbar reference.
   */
  constructor(private router: Router, private snackBar: MatSnackBar) {
    this.isBusy = false;
  }

  /**
   * Verifies if the user is authenticated or not, checking first in the user stored in memory,
   * if the user is not found there then it checks if exists in the browser's session storage,
   * if it's obtained from there is also set in the user variable.
   *
   * @date 2019-04-09
   * @returns true if the user is authenticated, false otherwise.
   */
  public isUserAuthenticated(): boolean {
    if (this.user) {
      return true;
    }
    try {
      this.user = JSON.parse(sessionStorage.getItem('user'));
      return this.user !== null;
    } catch (error) {
      return false;
    }
  }

  /**
   * Determines if the given form (NgForm) is invalid or not.
   *
   * @date 2019-01-10
   * @param form - {@link NgForm} to be evaluated.
   * @returns true if the form is invalid, false otherwise.
   */
  public isFormInvalid(form: NgForm): boolean {
    return form.form.invalid;
  }

  /**
   * Determines if the given model (NgModel) is invalid or not.
   *
   * @date 2019-01-10
   * @param model - {@link NgModel} to be evaluated.
   * @returns true if the model is invalid, false otherwise.
   */
  public isModelInvalid(model: NgModel): boolean {
    return model.invalid && (model.dirty || model.touched);
  }

  /**
   * Handles a general HttpErrorResponse (client side error, timeout, server-side error).
   *
   * @date 2019-04-09
   * @param err - Error to be handled.
   * @param [showError=true] - Show the message as a Snackbar (using Material's snackbar).
   */
  public handleGenericError(err: HttpErrorResponse, showError: boolean = true): void {
    let error: ApiError;
    if (err.error instanceof Error) {
      // A client-side or network error occurred. Handle it accordingly.
      error = ApiError.fromClientError(err.error);
    } else if (err.error instanceof ProgressEvent) {
      // Timeout error
      error = ApiError.fromTimeout(err.error);
    } else {
      // The backend returned an unsuccessful response code.
      // The response body may contain clues as to what went wrong,
      try {
        error = err.error as ApiError;
      } catch (err) {
        error = ApiError.fromGeneric(err);
      }
    }
    console.error(error);
    this.isBusy = false;

    if (showError) {
      this.showSnack(error.message, 'OK', Util.snackOptions(), true);
    }
  }

  /**
   * Authenticates the user in the platform.
   *
   * @date 2019-04-09
   * @param user - user to be authenticated.
   */
  public authenticate(user: User): void {
    this.user = user;
    sessionStorage.clear();
    sessionStorage.setItem('user', JSON.stringify(user));
    this.subscribeToNotification.next();
    this.router.navigate(['/dashboard']);
  }

  /**
   * Shows a Snackbar (Angular Material's) with a given message.
   *
   * @date 2019-04-09
   * @param message - Message to be show.
   * @param [action='OK'] - Button action.
   * @param [config=Util.snackOptions()] - Snackbar configuration.
   */
  public showSnack(message: string, action: string = 'OK', config: MatSnackBarConfig = Util.snackOptions(), isError?: boolean): void {
    this.snackBar.dismiss();
    if (isError) {
      config.panelClass = 'error-message';
    }
    this.snackBar.open(message, action, config);
  }

}
