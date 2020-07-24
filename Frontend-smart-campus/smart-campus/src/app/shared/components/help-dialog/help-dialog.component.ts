import { Component, OnInit, Input, ViewChild, ViewContainerRef, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material';

@Component({
  selector: 'sc-help-dialog',
  templateUrl: './help-dialog.component.html',
  styleUrls: ['./help-dialog.component.css']
})
export class HelpDialogComponent implements OnInit {

  @ViewChild('content', {read: ViewContainerRef}) content: ViewContainerRef;

  constructor(
    public dialogRef: MatDialogRef<HelpDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit() {
    if (this.data.content) {
      this.content.createEmbeddedView(this.data.content);
    }
  }

  /**
   * Close dialog.
   */
  closeDialog() {
    this.dialogRef.close();
  }
}
