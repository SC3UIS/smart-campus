import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatDialog } from '@angular/material';
import { Router, ActivatedRoute } from '@angular/router';
import { take, takeUntil } from 'rxjs/operators';

import { ApiResponse } from 'src/app/shared/models/api-response';
import { Application } from 'src/app/shared/models/application';
import { ApplicationService } from 'src/app/core/services/application.service';
import { AppService } from 'src/app/app.service';
import { ApplicationsFilter } from 'src/app/shared/models/types';
import { ConfirmDialogComponent } from 'src/app/shared/components/confirm-dialog/confirm-dialog.component';
import { DialogData } from 'src/app/shared/components/confirm-dialog/dialog-data';
import { DataTable } from 'src/app/shared/utils/data-table';
import { Util } from 'src/app/shared/utils/util';

/**
 * Page to manage all the user's applications.
 *
 * @date 2019-04-03
 * @export
 */
@Component({
  selector: 'sc-applications',
  templateUrl: './applications.component.html',
  styleUrls: ['./applications.component.css']
})
export class ApplicationsComponent extends DataTable<Application, ApplicationsFilter> implements OnInit {

  /**
   * Creates an instance of ApplicationsComponent.
   * @date 2019-04-03
   * @param activatedRoute - current Route.
   * @param applicationService - Application's related service.
   * @param router - Angular router.
   */
  constructor(
    protected activatedRoute: ActivatedRoute,
    private appService: AppService,
    public applicationService: ApplicationService,
    private dialog: MatDialog,
    protected router: Router) {
      super(activatedRoute, router);
      this.displayedColumns = [ 'id', 'name', 'description', 'actions' ];
  }

  ngOnInit() {
    super.initDataTable();
    this.getApplications();
    this.defaultSort();
  }

  /**
   * Triggered when pressing "Delete" application button.
   *
   * @date 2019-04-04
   * @param id - id of the application to be deleted.
   */
  public onDeleteRecord(id: number, name: string): void {
    const deleteDialog = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: new DialogData(
        'Eliminar aplicación',
        `Está seguro que desea eliminar la aplicación ${ name }`,
        id)
    });

    deleteDialog.afterClosed()
      .pipe(
        take(1),
        takeUntil(this.destroyed))
      .subscribe(result => result ? this.deleteApplication(result) : null);
  }

  /**
   *  Deletes the application identified by its id.
   *
   * @date 2019-04-05
   * @param id - id of the application to be deleted.
   */
  private deleteApplication(id: number) {
    this.applicationService.deleteApplication(id)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (res: ApiResponse) => {
          this.appService.showSnack(res.message);
          if (res.sucessful) {
            this.applicationService.applications.splice(
              this.applicationService.applications.findIndex(application => application.id === id), 1);
            this.afterRecordDeleted();
          }
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err)
      );
  }

  /**
   * Retrieves the application for the user logged in.
   *
   * @date 2019-04-04
   */
  private getApplications(): void {
    this.applicationService.getApplicationsForUser(this.appService.user.id)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (applications: Application[]) => {
        this.applicationService.applications = applications;
        this.dataSource.data = applications;
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err));
  }

  protected filterPredicate: (data: Application, filter: string) => boolean = (data: Application, filter: string) => {
    switch (this.filterType) {
      case 'ID':
        return Util.stringContains(String(data.id), filter);
      case 'NAME':
        return Util.stringContains(data.name, filter);
      case 'DESCRIPTION':
        return Util.stringContains(data.description, filter);
      case 'NONE':
        return true;
    }
  }
}
