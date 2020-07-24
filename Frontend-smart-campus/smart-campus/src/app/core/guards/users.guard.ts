import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, RouterStateSnapshot, CanActivate, Router } from '@angular/router';

import { AppService } from 'src/app/app.service';
import { CoreModule } from 'src/app/core/core.module';

/**
 * Guard to control the navigation over the user management section it's activated only for administrators..
 *
 * @date 2019-04-09
 * @export
 */
@Injectable({
  providedIn: CoreModule
})
export class UsersGuard implements CanActivate {

  /**
   * Creates an instance of UsersGuard.
   * @date 2019-04-09
   * @param appService - Main Application Service.
   * @param router - Angular's main router.
   */
  constructor(private appService: AppService, private router: Router) {}

  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    if (this.appService.isUserAuthenticated() && this.appService.user.admin) {
      return true;
    }

    this.router.navigate(['/dashboard']);
    return false;
  }
}
