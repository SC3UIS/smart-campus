import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { takeUntil, take } from 'rxjs/operators';

import { ApplicationService } from 'src/app/core/services/application.service';
import { Application } from 'src/app/shared/models/application';
import { AppService } from 'src/app/app.service';
import { Subscribable } from 'src/app/shared/utils/subscribable';

@Component({
  selector: 'sc-application',
  templateUrl: './application.component.html',
  styleUrls: ['./application.component.css']
})
export class ApplicationComponent extends Subscribable implements OnInit {

  public applicationId: number;
  public application: Application;

  constructor(
    public appService: AppService,
    private applicationService: ApplicationService,
    private activatedRoute: ActivatedRoute,
    private router: Router) {
      super();
      this.application = new Application();
    }

  ngOnInit() {
    this.applicationId = Number(this.activatedRoute.snapshot.params.id);
    if (this.applicationId) {
      this.getApplication();
    }
  }

  private getApplication(): void {
    this.applicationService.getApplication(this.applicationId)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (application: Application) => this.application = application,
        (err: HttpErrorResponse) => {
          this.appService.handleGenericError(err);
          this.router.navigate([ '..' ], { relativeTo: this.activatedRoute });
        });
  }

  public saveOrUpdateApplication(): void {
    if (this.applicationId) {
      this.updateApplication();
    } else {
      this.createApplication();
    }
  }

  private createApplication(): void {
    this.application.userId = this.appService.user.id;
    this.applicationService.createApplication(this.application)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (application: Application) => {
        this.router.navigate([ '..' ], { relativeTo: this.activatedRoute });
        this.appService.showSnack('Aplicación creada correctamente.');
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err)
    );
  }

  private updateApplication(): void {
    this.applicationService.updateApplication(this.application)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (application: Application) => {
        this.application = application;
        this.appService.showSnack('Aplicación actualizada correctamente.');
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err)
    );
  }

}
