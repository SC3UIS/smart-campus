import { Component, Inject} from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import { DialogData } from './dialog-data';

/**
 * Reusable component to show a confirmation dialog.
 *
 * @export
 */
@Component({
  selector: 'sc-confirm-dialog',
  templateUrl: './confirm-dialog.component.html',
  styleUrls: ['./confirm-dialog.component.css']
})
export class ConfirmDialogComponent {

  /**
   * Creates an instance of ConfirmDialogComponent.
   * @param dialogRef - reference to the dialog.
   * @param data - to be passed to the dialog.
   */
  constructor(
    public dialogRef: MatDialogRef<ConfirmDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: DialogData) { }
}
