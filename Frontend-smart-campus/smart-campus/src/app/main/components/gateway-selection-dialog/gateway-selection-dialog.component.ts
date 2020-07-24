import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import { Gateway } from 'src/app/shared/models/gateway';
import { Subscribable } from 'src/app/shared/utils/subscribable';

/**
 * Dialog to select a Gateway to assign it to an application.
 *
 * @date 2019-04-09
 * @export
 */
@Component({
  selector: 'sc-gateway-selection-dialog',
  templateUrl: './gateway-selection-dialog.component.html',
  styleUrls: ['./gateway-selection-dialog.component.css']
})
export class GatewaySelectionDialogComponent extends Subscribable implements OnInit {

  /**
   * Gateway selected in the current dialog to be assigned.
   *
   */
  public selectedGateway: Gateway;

  /**
   * Contains the gateways assignable to the application
   * (they were created by the user owner of the application and are not already assigned to it.)
   *
   */
  public gateways: Gateway[];

  /**
   * Creates an instance of GatewaySelectionDialogComponent.
   * @date 2019-04-09
   * @param dialogRef - Material's Dialog Reference.
   * @param data - Data pased to the dialog (assignable Gateways).
   */
  constructor(
    public dialogRef: MatDialogRef<GatewaySelectionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: Gateway[]) {
      super();
    }

  ngOnInit() {
    this.gateways = this.data;
  }

}
