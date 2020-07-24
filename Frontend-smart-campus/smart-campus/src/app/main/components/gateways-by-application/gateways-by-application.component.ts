import { Component, OnInit, Input, OnChanges } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatTableDataSource, MatDialog } from '@angular/material';
import { take, takeUntil } from 'rxjs/operators';

import { ApiResponse } from 'src/app/shared/models/api-response';
import { ApplicationService } from 'src/app/core/services/application.service';
import { AppService } from 'src/app/app.service';
import { ConfirmDialogComponent } from 'src/app/shared/components/confirm-dialog/confirm-dialog.component';
import { DialogData } from 'src/app/shared/components/confirm-dialog/dialog-data';
import { DataTable } from 'src/app/shared/utils/data-table';
import { Gateway } from 'src/app/shared/models/gateway';
import { GatewayService } from 'src/app/core/services/gateway.service';
import { GatewaysFilter } from 'src/app/shared/models/types';
import { GatewaySelectionDialogComponent } from 'src/app/main/components/gateway-selection-dialog/gateway-selection-dialog.component';
import { Util } from 'src/app/shared/utils/util';

/**
 * Displays a Datatable with all the Gateways assigned to an Application.
 *
 * @date 2019-04-09
 * @export
 */
@Component({
  selector: 'sc-gateways-by-application',
  templateUrl: './gateways-by-application.component.html',
  styleUrls: ['./gateways-by-application.component.css']
})
export class GatewaysByApplicationComponent extends DataTable<Gateway, GatewaysFilter> implements OnInit, OnChanges {

  /**
   * Gateways assigned to the application.
   *
   */
  @Input() gateways: Gateway[];

  /**
   * Id of the application.
   *
   */
  @Input() applicationId: number;

  /**
   * Creates an instance of GatewaysByApplicationComponent.
   * @date 2019-04-09
   * @param appService - Main Service.
   * @param applicationService - Applications management Service.
   * @param gatewayService - Gateways management Service.
   * @param dialog - Material dialog reference.
   */
  constructor(
    private appService: AppService,
    private applicationService: ApplicationService,
    private gatewayService: GatewayService,
    private dialog: MatDialog) {
    super(null, null);
    this.displayedColumns = [ 'name', 'description', 'ip', 'alive', 'actions' ];
  }

  ngOnInit() {
    super.initDataTable();
  }

  ngOnChanges() {
    this.dataSource = new MatTableDataSource(this.gateways);
  }

  /**
   * Executed when pressing the 'Assign Gateway' button.
   * Loads the gateways of the user stored in the Gateway service or retrieving it from the backend if they are not present yet.
   * Then shows the Assignment dialog (GatewaySelectionDialogComponent) with all the assignable gateways and listens for the selection
   * of a Gateway to assign it.
   *
   * @date 2019-04-09
   */
  public onAssignGateway(): void {
    if (this.gatewayService.gateways) {
      this.prepareAssignmentsDialog(this.gatewayService.gateways);
    } else {
      this.gatewayService.getGatewaysByUserId(this.appService.user.id)
        .pipe(take(1), takeUntil(this.destroyed))
        .subscribe(
          (gateways: Gateway[]) => this.prepareAssignmentsDialog(gateways),
          (err: HttpErrorResponse) => this.appService.handleGenericError(err));
    }
  }

  /**
   * Shows the assignment dialog with the Gateways assignables as options,
   * when any is selected then it's asigned (calling the REST service from the Bacend).
   *
   * @date 2019-04-09
   * @param userGateways - Gateways that belong to the user.
   */
  private prepareAssignmentsDialog(userGateways: Gateway[]): void {
    // filter the gateways excluding the ones that are already assigned.
    let assignableGateways = [ ...userGateways ];
    assignableGateways = assignableGateways
      .filter(gateway => this.gateways.findIndex(g => g.id === gateway.id) < 0);
    const assignDialog = this.dialog.open(GatewaySelectionDialogComponent, {
      width: '500px',
      data: assignableGateways
    });

    assignDialog.afterClosed()
      .pipe(
        take(1),
        takeUntil(this.destroyed))
      .subscribe((gateway: Gateway) => gateway ? this.assignGateway(gateway.id, true, gateway) : null);
  }

  /**
   * Executed when pressing the 'Unassign gateway' button for a given Gateway.
   * Opens the ConfirmationDialogComponent and if confirmed then the Gateway is unassigned.
   *
   * @date 2019-04-09
   * @param gateway - Gateway to be unassigned.
   */
  public onUnassignGateway(gateway: Gateway): void {
    const unassignDialog = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: new DialogData(
        'Desasignar Gateway',
        `Está seguro que desea desasignar el Gateway: ${ gateway.name }`,
        gateway.id)
    });

    unassignDialog.afterClosed()
      .pipe(
        take(1),
        takeUntil(this.destroyed))
      .subscribe(result => result ? this.assignGateway(result, false) : null);
  }

  /**
   * Assigns or unassigns a gateway to the current application calling the REST service.
   *
   * @date 2019-04-09
   * @param gatewayId - id of the gateway to be assigned/unassigned.
   * @param assign - true to assign the gateway, false to unassign it.
   * @param [gateway] - gateway to be assigned (only necessary for assignment).
   */
  private assignGateway(gatewayId: number, assign: boolean, gateway?: Gateway): void {
    this.applicationService.assignGatewayToApplication(this.applicationId, gatewayId, assign)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (res: ApiResponse) => {
          this.appService.showSnack(res.message);
          if (assign) {
            this.gateways.push(gateway);
          } else {
            this.gateways.splice(this.gateways.findIndex(g => g.id === gatewayId), 1);
          }
          this.dataSource.data = this.gateways;
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err)
      );
  }

  protected filterPredicate: (data: Gateway, filter: string) => boolean = (data: Gateway, filter: string) => {
    switch (this.filterType) {
      case 'NAME': return Util.stringContains(data.name, filter);
      case 'DESCRIPTION': return Util.stringContains(data.description, filter);
      case 'IP': return Util.stringContains(data.ip, filter);
      case 'IS_ALIVE': return data.alive === (filter === 'true');
      default: return true;
    }
  }

}
