import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';

import { AppService } from './../../../app.service';
import { PropertyDialog } from './property-dialog';

@Component({
  selector: 'sc-property-edition-dialog',
  templateUrl: './property-edition-dialog.component.html',
  styleUrls: ['./property-edition-dialog.component.css']
})
export class PropertyEditionDialogComponent implements OnInit {

  /**
   * Indicates if the property already exists.
   *
   */
  public duplicatedProperty: boolean;

  /**
   * Creates an instance of PropertyEditionDialogComponent.
   * @date 2019-04-09
   * @param dialogRef - Material's Dialog Reference.
   * @param data - Data pased to the dialog (assignable Gateways).
   */
  constructor(
    public appService: AppService,
    public dialogRef: MatDialogRef<PropertyEditionDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: PropertyDialog) { }

  ngOnInit() {

  }

  /**
   * Indicates if the filled property already exists.
   */
  public checkIfPropertyExist(): void {

    if (!this.data.isCreation
        && this.data.oldProperty.type === this.data.newProperty.type
        && this.data.oldProperty.name === this.data.newProperty.name) {
          this.duplicatedProperty = false;
          return;
    }

    this.duplicatedProperty = this.data.currentProperties.some(currentProperty => currentProperty.type === this.data.newProperty.type &&
      currentProperty.name === this.data.newProperty.name);
  }

  /**
   * Creates or updates a property.
   *
   */
  public saveOrUpdateProperty(): void {
    if (this.data.isCreation) {
      this.data.currentProperties.push(this.data.newProperty);
    } else {
      if (this.data.onlyValueModifiable) {
        this.data.oldProperty.value = this.data.newProperty.value;
        return;
      }
      this.data.oldProperty.name = this.data.newProperty.name;
      this.data.oldProperty.type = this.data.newProperty.type;
    }
  }

}
