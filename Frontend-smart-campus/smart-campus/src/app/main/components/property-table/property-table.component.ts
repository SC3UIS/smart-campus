import { Component, OnInit, Input, OnChanges } from '@angular/core';
import { MatTableDataSource, MatDialog } from '@angular/material';
import { take, takeUntil } from 'rxjs/operators';

import { ConfirmDialogComponent } from 'src/app/shared/components/confirm-dialog/confirm-dialog.component';
import { DataTable } from 'src/app/shared/utils/data-table';
import { DialogData } from 'src/app/shared/components/confirm-dialog/dialog-data';
import { Property } from 'src/app/shared/models/property';
import { PropertyEditionDialogComponent } from '../property-edition-dialog/property-edition-dialog.component';
import { PropertiesFilter } from 'src/app/shared/models/types';
import { PropertyDialog } from '../property-edition-dialog/property-dialog';
import { Util } from 'src/app/shared/utils/util';
import { PropertyRule } from './property-rule.class';

@Component({
  selector: 'sc-property-table',
  templateUrl: './property-table.component.html',
  styleUrls: ['./property-table.component.css']
})
export class PropertyTableComponent extends DataTable<Property, PropertiesFilter> implements OnInit, OnChanges {

  /**
   * Receives the list of the properties to be displayed.
   */
  @Input() properties: Property[];

  /**
   * Receives the list properties which has rules.
   */
  @Input() propertyRules: PropertyRule[];

  /**
   * Creates an instance of PropertyTableComponent.
   */
  constructor(private dialog: MatDialog) {
      super();
      this.displayedColumns = [ 'type', 'name', 'value', 'actions' ];
      this.propertyRules = [];
  }

  ngOnInit() {
    super.initDataTable();
  }

  ngOnChanges() {
    if (!this.properties) {
      this.properties = [];
    }
    this.dataSource.data = this.properties;
  }

  /**
   * Triggered when pressing "Create app" button.
   *
   * @date 2019-04-03
   */
  public onCreateRecord(): void {
    const createDialog = this.dialog.open(PropertyEditionDialogComponent, {
      width: '500px',
      data: new PropertyDialog(this.dataSource.data, new Property(), null, true)
    });

    createDialog.afterClosed()
      .pipe(
        take(1),
        takeUntil(this.destroyed))
      .subscribe(result => {
        if (result) {
          this.filterType = 'NONE';
          this.filterValue = '';
          this.dataSource = new MatTableDataSource(this.dataSource.data);
          this.dataSource.paginator = this.paginator;
          this.applyFilter();
          setTimeout(() => this.dataSource.paginator.lastPage(), 100);
        } else {
          return null;
        }
      });
  }

  /**
   * Triggered when pressing "Edit" button.
   *
   * @date 2019-04-04
   * @param property - property to be updated.
   */
  public onEditProperty(property: Property): void {
    if (!this.propertyCanBeEdited(property) || property.type === 'REPORTED') {
      return;
    }

    const editDialog = this.dialog.open(PropertyEditionDialogComponent, {
      width: '500px',
      data: new PropertyDialog(this.dataSource.data, new Property(property.name, property.type, property.value),
        property, false, this.propertyOnlyValueModifiable(property))
    });

    editDialog.afterClosed()
      .pipe(
        take(1),
        takeUntil(this.destroyed))
      .subscribe(result => {
        if (result) {
          this.dataSource = new MatTableDataSource(this.dataSource.data);
          this.dataSource.paginator = this.paginator;
        } else {
          return null;
        }
      });
  }

  /**
   * Triggered when pressing "Delete" button.
   *
   * @date 2019-04-04
   * @param index - index of the property to be deleted.
   * @param property - property to be deleted.
   */
  public onDeleteRecord(property: Property): void {
    if (!this.propertyCanBeDeleted(property) || property.type === 'REPORTED') {
      return;
    }

    const deleteDialog = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: new DialogData(
        'Eliminar propiedad',
        `Está seguro que desea eliminar la propiedad ${ property.name }`,
        property)
    });

    deleteDialog.afterClosed()
      .pipe(
        take(1),
        takeUntil(this.destroyed))
      .subscribe(result => {
        if (result) {
          const propertyIndex = this.dataSource.data.findIndex(dataSourceProperty => dataSourceProperty.type === property.type
          && dataSourceProperty.name === property.name);
          this.dataSource.data.splice(propertyIndex, 1);
          this.afterRecordDeleted();
        } else {
          return null;
        }
      });
  }

  /**
   * Indicates if the passed property can be deleted or not.
   *
   * @param property - Property to be analyzed.
   */
  public propertyCanBeDeleted(property: Property): boolean {
    const foundPropertyRule = this.propertyRules.find(propertyRule => propertyRule.type === property.type &&
      propertyRule.name === property.name);

    if (foundPropertyRule && !foundPropertyRule.canBeDeleted) {
      return false;
    }

    return true;
  }

  /**
   * Indicates if the passed property can be edited or not.
   *
   * @param property - Property to be analyzed.
   */
  public propertyCanBeEdited(property: Property): boolean {
    const foundPropertyRule = this.propertyRules.find(propertyRule => propertyRule.type === property.type &&
      propertyRule.name === property.name);

    if (foundPropertyRule && !foundPropertyRule.canBeEdited) {
      return false;
    }

    return true;
  }

  /**
   * Indicates if only the value can be modified of the passed property.
   *
   * @param property - Property to be analyzed.
   */
  private propertyOnlyValueModifiable(property: Property): boolean {
    const foundPropertyRule = this.propertyRules.find(propertyRule => propertyRule.type === property.type &&
      propertyRule.name === property.name);

    if (foundPropertyRule && foundPropertyRule.onlyValueModifiable) {
      return true;
    }

    return false;
  }

  /**
   * Filter function to be applied to the table.
   */
  protected filterPredicate: (data: Property, filter: string) => boolean = (data: Property, filter: string) => {
    switch (this.filterType) {
      case 'TYPE':
      return Util.stringContains(data.type, filter);
      case 'NAME':
        return Util.stringContains(data.name, filter);
      case 'VALUE':
        return Util.stringContains(data.value, filter);
      case 'NONE':
        return true;
    }
  }
}
