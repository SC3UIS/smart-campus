import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { takeUntil, take } from 'rxjs/operators';

import { Application } from 'src/app/shared/models/application';
import { AppService } from 'src/app/app.service';
import { Subscribable } from 'src/app/shared/utils/subscribable';
import { User } from 'src/app/shared/models/user';
import { UserService } from 'src/app/core/services/user.service';

@Component({
  selector: 'sc-user',
  templateUrl: './user.component.html',
  styleUrls: ['./user.component.css']
})
export class UserComponent extends Subscribable implements OnInit {

  public userId: number;
  public user: User;
  public confirmPassword: string;

  constructor(
    public appService: AppService,
    private userService: UserService,
    private activatedRoute: ActivatedRoute,
    private router: Router) {
    super();
    this.user = new User();
  }

  ngOnInit() {
    this.userId = Number(this.activatedRoute.snapshot.params.id);
    if (this.userId) {
      this.getUser();
    }
  }

  private getUser(): void {
    this.userService.getUser(this.userId)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (user: User) => this.user = user,
        (err: HttpErrorResponse) => {
          this.appService.handleGenericError(err);
          this.router.navigate(['..'], { relativeTo: this.activatedRoute });
        });
  }

  public createUser(): void {
    this.user.id = this.appService.user.id;
    this.userService.createUser(this.user)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (user: User) => {
          this.router.navigate(['..'], { relativeTo: this.activatedRoute });
          this.appService.showSnack('Usuario creado correctamente.');
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err)
      );
  }

}
