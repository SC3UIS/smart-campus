import { Component, Input, ViewChild, ViewContainerRef } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { HelpDialogComponent } from '../help-dialog/help-dialog.component';

@Component({
  selector: 'sc-help',
  templateUrl: './help.component.html',
  styleUrls: ['./help.component.css']
})
export class HelpComponent {

  /**
   * References of the title inputted from the parent component.
   */
  @Input()
  title: string;

  /**
   * References of the content inputted from the parent component.
   */
  @Input()
  content: ViewContainerRef;

  /**
   * Custom options
   */
  @Input()
  dialogOptions: MatDialogConfig;

  @ViewChild('contentTemplate') contentTemplate: ViewContainerRef;

  /**
   * Creates an instance of HelpComponent.
   */
  constructor(
    private dialog: MatDialog) { }

  /**
   * Open dialog
   */
  openDialog() {
    this.dialog.open(HelpDialogComponent, {
      ...this.dialogOptions,
      data: {
        title: this.title,
        content: this.content
      }
    });
  }
}
