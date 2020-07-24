import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material';

import { AppService } from 'src/app/app.service';
import { DataTable } from 'src/app/shared/utils/data-table';
import { Device } from 'src/app/shared/models/device';
import { DevicesFilter } from 'src/app/shared/models/types';
import { DeviceService } from 'src/app/core/services/device.service';
import { Gateway } from 'src/app/shared/models/gateway';
import { GatewayService } from 'src/app/core/services/gateway.service';
import { ConfirmDialogComponent } from 'src/app/shared/components/confirm-dialog/confirm-dialog.component';
import { DialogData } from 'src/app/shared/components/confirm-dialog/dialog-data';
import { take, takeUntil } from 'rxjs/operators';
import { ApiResponse } from 'src/app/shared/models/api-response';
import { HttpErrorResponse } from '@angular/common/http';
import { Util } from 'src/app/shared/utils/util';

@Component({
  selector: 'sc-devices',
  templateUrl: './devices.component.html',
  styleUrls: ['./devices.component.css']
})
export class DevicesComponent extends DataTable<Device, DevicesFilter> implements OnInit {

  /**
   * Lists of gateways associated to the current devices.
   */
  public gatewaysSelect: Gateway[];

  /**
   * Indicates if the called web service to get the list of devices has finished.
   */
  public devicesReady: boolean;

  /**
   * Indicates if the called web service to get the list of gateways has finished.
   */
  public gatewaysReady: boolean;

  /**
   * Creates an instance of DevicesComponent.
   */
  constructor(
    protected activatedRoute: ActivatedRoute,
    private appService: AppService,
    public gatewayService: GatewayService,
    public deviceService: DeviceService,
    private dialog: MatDialog,
    protected router: Router) {
      super(activatedRoute, router);
      this.displayedColumns = [ 'id', 'type', 'name', 'description', 'actions' ];
      this.gatewaysSelect = [];
  }

  ngOnInit() {
    super.initDataTable();
    this.getDevices();
    this.getGateways();
    this.defaultSort();
  }

  /**
   * Triggered when pressing "Delete" button.
   *
   * @date 2019-04-04
   * @param id - id of the device to be deleted.
   */
  public onDeleteRecord(id: number, name: string): void {
    const deleteDialog = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: new DialogData(
        'Eliminar dispositivo',
        `Está seguro que desea eliminar el dispositivo ${ name }`,
        id)
    });

    deleteDialog.afterClosed()
      .pipe(
        take(1),
        takeUntil(this.destroyed))
      .subscribe(result => result ? this.deleteRecord(result) : null);
  }

  /**
   * Redirects to the clone page.
   *
   * @param id - id of the device to be cloned.
   */
  public onCloneRecord(id: string) {
    this.router.navigate(['/dashboard/devices/' + id], {
      queryParams: {
        clone: true
      }
    });
  }

  /**
   *  Deletes the device identified by its id.
   *
   * @date 2019-04-05
   * @param id - id of the device to be deleted.
   */
  private deleteRecord(id: number) {
    this.deviceService.deleteDeviceById(id)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (res: ApiResponse) => {
          this.deviceService.devices.splice(
            this.deviceService.devices.findIndex(device => device.id === id), 1);
          this.afterRecordDeleted();
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err)
      );
  }

  /**
   * Retrieves the devices for the user logged in.
   *
   * @date 2019-04-04
   */
  private getDevices(): void {
    this.deviceService.getDevicesByUserId(this.appService.user.id)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (devices: Device[]) => {
        this.deviceService.devices = devices;
        this.dataSource.data = devices;
        this.devicesReady = true;
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

  protected filterPredicate: (data: Device, filter: string) => boolean = (data: Device, filter: string) => {
    switch (this.filterType) {
      case 'ID':
        return Util.stringContains(String(data.id), filter);
      case 'NAME':
        return Util.stringContains(data.name, filter);
      case 'DESCRIPTION':
        return Util.stringContains(data.description, filter);
      case 'TYPE':
        return Util.stringContains(data.type, filter);
      case 'GATEWAY': {
        return data.gatewayId === Number(filter);
      }
      case 'NONE':
        return true;
    }
  }

  /**
   * Builds the list of selectable gateways for the filter.
   */
  private buildGatewaysSelect(): void {
    if (!this.gatewayService.gateways) {
      return;
    }

    this.gatewayService.gateways.forEach(gateway => this.gatewaysSelect.push(gateway));
  }

}
