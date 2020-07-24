import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot } from '@angular/router';
import { Injectable } from '@angular/core';

import { AppService } from 'src/app/app.service';
import { CoreModule } from 'src/app/core/core.module';

/**
 * Guard to control the navigation over authentication pages (login, signin, password-recovery).
 *
 * @date 2019-04-09
 * @export
 */
@Injectable({
  providedIn: CoreModule
})
export class AuthenticationGuard implements CanActivate {

  /**
   * Creates an instance of AuthenticationGuard.
   * @date 2019-04-09
   * @param appService - Main Application Service.
   * @param router - Angular's main router.
   */
  constructor(private appService: AppService, private router: Router) {}

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (!this.appService.isUserAuthenticated()) {
      return true;
    }

    this.router.navigate(['/dashboard']);
    return false;
  }

}
