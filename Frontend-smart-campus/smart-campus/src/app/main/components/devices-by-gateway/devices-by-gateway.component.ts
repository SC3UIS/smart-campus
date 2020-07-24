import { Component, OnChanges, Input } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { MatDialog } from '@angular/material';
import { HttpErrorResponse } from '@angular/common/http';
import { take, takeUntil } from 'rxjs/operators';

import { AppService } from 'src/app/app.service';
import { DataTable } from 'src/app/shared/utils/data-table';
import { Device } from 'src/app/shared/models/device';
import { DevicesFilter } from 'src/app/shared/models/types';
import { DeviceService } from 'src/app/core/services/device.service';
import { Gateway } from 'src/app/shared/models/gateway';
import { GatewayService } from 'src/app/core/services/gateway.service';
import { ConfirmDialogComponent } from 'src/app/shared/components/confirm-dialog/confirm-dialog.component';
import { DialogData } from 'src/app/shared/components/confirm-dialog/dialog-data';
import { ApiResponse } from 'src/app/shared/models/api-response';
import { Util } from 'src/app/shared/utils/util';

@Component({
  selector: 'sc-devices-by-gateway',
  templateUrl: './devices-by-gateway.component.html',
  styleUrls: ['./devices-by-gateway.component.css']
})
export class DevicesByGatewayComponent extends DataTable<Device, DevicesFilter> implements OnChanges {

  /**
   * Lists of gateways associated to the current devices.
   */
  @Input() devices: Device[];

  /**
   * Indicates if the called web service to get the list of devices has finished.
   */
  public devicesReady: boolean;

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
  }

  ngOnChanges() {
    super.initDataTable();
    this.deviceService.devices = this.devices;
    this.dataSource.data = this.devices;
    this.devicesReady = true;
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
          this.devices.splice(
            this.devices.findIndex(device => device.id === id), 1);
          this.afterRecordDeleted();
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err)
      );
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
      case 'NONE':
        return true;
    }
  }

}
