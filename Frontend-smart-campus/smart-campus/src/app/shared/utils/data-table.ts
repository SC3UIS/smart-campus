import { MatTableDataSource, MatPaginator, MatSort, MatSortable } from '@angular/material';
import { ViewChild } from '@angular/core';
import { Router, ActivatedRoute } from '@angular/router';

import { Subscribable } from './subscribable';
import { Entity } from '../models/entity';

/**
 * General Datatable class, used to show all the data tables in the application.
 * Uses Angular's material Table with sorting, filtering and pagination.
 * Extend this class in all components that contain a Datatable
 * and in their OnInit lifecycle method call the initDataTable() method.
 *
 * @date 2019-04-09
 * @export
 * @template T - Object to be desplayed in the table.
 * @template U - Type of the filter to be applied, by default the 'NONE' filter is applied.
 */
export abstract class DataTable<T, U> extends Subscribable {

  /**
   * Columns to be displayed in the table.
   *
   */
  public displayedColumns: string[];

  /**
   * Angular Material Table Data Source, stores the information displayed in the table.
   *
   */
  public dataSource: MatTableDataSource<T>;

  /**
   * Filter to be applied as a string, by default it's empy.
   *
   */
  public filterValue = '';

  /**
   * Type of the filter to be applied, by default 'NONE' it's applied.
   *
   */
  public filterType: U | 'NONE' = 'NONE';

  /**
   * Reference to Material paginator.
   *
   */
  @ViewChild(MatPaginator) paginator: MatPaginator;

  /**
   * Reference to Material sort.
   *
   */
  @ViewChild(MatSort) sort: MatSort;

  /**
   * Filtering funtion to be applied over the data.
   * Use the filterType to know which filter is selected to generate your own filtering function.
   * Return true in the predicate when the element matches the filter currently applied.
   *
   */
  protected abstract filterPredicate: (data: T, filter: string) => boolean;

  /**
   * Creates an instance of DataTable.
   * @date 2019-04-09
   * @param activatedRoute - Angular's activated route.
   * @param router - Angular's router.
   */
  constructor(
    protected activatedRoute: ActivatedRoute = null,
    protected router: Router = null) {
    super();
    this.dataSource = new MatTableDataSource();
  }

  /**
   * Inits all the configurations for the data table.
   *
   */
  public initDataTable(): void {
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
    this.dataSource.sortingDataAccessor = (data, attribute) => data[attribute];
    this.dataSource.filterPredicate = this.filterPredicate;
  }

  /**
   * Sort by name asc.
   *
   * @date 2019-04-19
   */
  protected defaultSort(): void {
    this.sort.sort(({ id: 'name', start: 'asc' }) as MatSortable);
    this.dataSource.sort = this.sort;
  }

  /**
   * Triggered when pressing "Create app" button.
   *
   * @date 2019-04-03
   */
  public onCreateRecord(): void {
    this.router.navigate([ '0' ], { relativeTo: this.activatedRoute });
  }

  /**
   * Triggered when pressing "Edit" button.
   *
   * @date 2019-04-04
   * @param id - id of the application to be edited.
   */
  public onEditRecord(id: number): void {
    this.router.navigate([ id ], { relativeTo: this.activatedRoute });
  }

  /**
   * Executed when the filter type is changed.
   *
   * @date 2019-04-09
   * @param newFilterType - New filter type selected.
   */
  public onFilterTypeChange(newFilterType: U): void {
    this.filterValue = '';
    this.applyFilter();
    this.filterType = newFilterType;
  }

  /**
   * Applies the Filter definition for the current datasource using as value the content of filterValue.
   *
   * @date 2019-04-05
   */
  public applyFilter(): void {
    this.dataSource.filter = this.filterValue;
  }

  /**
   * Updates the data to be shown, goes to the previous page if necessary and applies the current filter.
   */
  public afterRecordDeleted(): void {
    this.dataSource = new MatTableDataSource(this.dataSource.data);
    this.dataSource.paginator = this.paginator;
    if (this.dataSource.data.length % this.dataSource.paginator.pageSize === 0) {
      this.dataSource.paginator.previousPage();
    }
    this.applyFilter();
  }
}
