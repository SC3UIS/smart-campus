import { ProcessService } from './../../../core/services/process.service';
import { Component, OnChanges, Input } from '@angular/core';
import { MatTableDataSource, MatDialog } from '@angular/material';

import { DataTable } from 'src/app/shared/utils/data-table';
import { Process } from 'src/app/shared/models/process';
import { ProcessesFilter } from 'src/app/shared/models/types';
import { Gateway } from 'src/app/shared/models/gateway';
import { ActivatedRoute, Router } from '@angular/router';
import { AppService } from 'src/app/app.service';
import { GatewayService } from 'src/app/core/services/gateway.service';
import { ConfirmDialogComponent } from 'src/app/shared/components/confirm-dialog/confirm-dialog.component';
import { DialogData } from 'src/app/shared/components/confirm-dialog/dialog-data';
import { take, takeUntil } from 'rxjs/operators';
import { ApiResponse } from 'src/app/shared/models/api-response';
import { HttpErrorResponse } from '@angular/common/http';
import { Util } from 'src/app/shared/utils/util';

@Component({
  selector: 'sc-processes-by-gateway',
  templateUrl: './processes-by-gateway.component.html',
  styleUrls: ['./processes-by-gateway.component.css']
})
export class ProcessesByGatewayComponent extends DataTable<Process, ProcessesFilter> implements OnChanges {

  @Input() processes: Process[];

  /**
   * Lists of gateways associated to the current processes.
   */
  public gatewaysSelect: Gateway[];

  /**
   * Indicates if the called web service to get the list of processes has finished.
   */
  public processesReady: boolean;

  /**
   * Indicates if the called web service to get the list of gateways has finished.
   */
  public gatewaysReady: boolean;

  /**
   * Creates an instance of ProcessesComponent.
   */
  constructor(
    protected activatedRoute: ActivatedRoute,
    private appService: AppService,
    public gatewayService: GatewayService,
    public processService: ProcessService,
    private dialog: MatDialog,
    protected router: Router) {
      super(activatedRoute, router);
      this.displayedColumns = [ 'id', 'name', 'description', 'alive', 'actions' ];
      this.gatewaysSelect = [];
      this.processes = [];
  }

  ngOnChanges() {
    if (this.processes && this.processes.length > 0) {
      super.initDataTable();
      this.processesReady = true;
      this.dataSource.data = this.processes;
      this.getGateways();
    }
  }

  /**
   * Triggered when pressing "Delete" button.
   *
   * @date 2019-04-04
   * @param id - id of the process to be deleted.
   */
  public onDeleteRecord(id: number, name: string): void {
    const deleteDialog = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: new DialogData(
        'Eliminar proceso',
        `Está seguro que desea eliminar el proceso ${ name }`,
        id)
    });

    deleteDialog.afterClosed()
      .pipe(
        take(1),
        takeUntil(this.destroyed))
      .subscribe(result => result ? this.deleteRecord(result) : null);
  }

  /**
   *  Deletes the process identified by its id.
   *
   * @date 2019-04-05
   * @param id - id of the process to be deleted.
   */
  private deleteRecord(id: number) {
    this.processService.deleteProcessById(id)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (res: ApiResponse) => {
          this.dataSource.data.splice(
            this.dataSource.data.findIndex(process => process.id === id), 1);
          this.afterRecordDeleted();
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err)
      );
  }

  /**
   * Retrieves the gateway for the user logged in.
   *
   * @date 2019-04-04
   */
  private getGateways(): void {
    this.gatewayService.getGatewaysByUserId(this.appService.user.id)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (gateways: Gateway[]) => {
        this.gatewayService.gateways = gateways;
        this.buildGatewaysSelect();
        this.gatewaysReady = true;
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err));
  }

  protected filterPredicate: (data: Process, filter: string) => boolean = (data: Process, filter: string) => {
    switch (this.filterType) {
      case 'ID':
        return Util.stringContains(String(data.id), filter);
      case 'NAME':
        return Util.stringContains(data.name, filter);
      case 'DESCRIPTION':
        return Util.stringContains(data.description, filter);
      case 'IS_ALIVE':
        return data.alive === (filter === 'true');
      case 'GATEWAY': {
        return data.gatewayId === Number(filter);
      }
      case 'NONE':
        return true;
    }
  }

  /**
   * Builds the list of selectable gateways for the filter.
   *
   */
  private buildGatewaysSelect(): void {
    if (!this.gatewayService.gateways) {
      return;
    }

    this.gatewayService.gateways.forEach(gateway => this.gatewaysSelect.push(gateway));
  }

}
