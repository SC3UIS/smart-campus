import { ProcessService } from './../../../core/services/process.service';
import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';

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
  selector: 'sc-processes',
  templateUrl: './processes.component.html',
  styleUrls: ['./processes.component.css']
})
export class ProcessesComponent extends DataTable<Process, ProcessesFilter> implements OnInit {

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
  }

  ngOnInit() {
    super.initDataTable();
    this.getProcesses();
    this.getGateways();
    this.defaultSort();
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
          this.processService.processes.splice(
            this.processService.processes.findIndex(process => process.id === id), 1);
          this.afterRecordDeleted();
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err)
      );
  }

  /**
   * Retrieves the processes for the user logged in.
   *
   * @date 2019-04-04
   */
  private getProcesses(): void {
    this.processService.getProcessesByUserId(this.appService.user.id)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (processes: Process[]) => {
        this.processService.processes = processes;
        this.processesReady = true;
        this.dataSource.data = processes;
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err));
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

  /**
   * Redirects to the clone page.
   *
   * @param id - id of the process to be cloned.
   */
  public onCloneRecord(id: string): void {
    this.router.navigate(['/dashboard/processes/' + id], {
      queryParams: {
        clone: true
      }
    });
  }

  /**
   * Checks if the given process is deployable, checking if the process jar property exists
   *
   * @date 2019-04-27
   * @param process - process to be deployed.
   * @returns true if the process is deployable, false otherwise.
   */
  public isDeployable(process: Process): boolean {
    if (!process.properties) {
      return false;
    }
    return process.properties
      .some(property => property.name.trim().toLowerCase() === 'process_jar' && property.type === 'CONFIG');
  }

  /**
   * Executed when the user deploys/redeploys/undeploys the given process.
   *
   * @date 2019-04-27
   * @param process - process to be deployed.
   * @param deploy true if the process must be deployed/re-deployed false to stop it.
   */
  public onDeployProcess(process: Process, deploy: boolean): void {
    this.processService.deployProcess(process.id, deploy)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (res: ApiResponse) => {
          if (res.sucessful) {
            process.alive = deploy;
            this.appService.showSnack(deploy ? 'Proceso desplegado correctamente' : 'Proceso detenido correctamente',
            'OK', Util.snackOptions(), false);
          } else {
            this.appService.showSnack(res.message);
          }
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err));
  }

}
