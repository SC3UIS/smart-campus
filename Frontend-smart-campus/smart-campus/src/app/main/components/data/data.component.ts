import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { MatDatepickerInputEvent } from '@angular/material';

import { take, takeUntil } from 'rxjs/operators';

import { Data } from 'src/app/shared/models/data';
import { Application } from 'src/app/shared/models/application';
import { DataTable } from 'src/app/shared/utils/data-table';
import { DataFilter } from 'src/app/shared/models/types';
import { Gateway } from 'src/app/shared/models/gateway';
import { Process } from 'src/app/shared/models/process';
import { ApplicationService } from 'src/app/core/services/application.service';
import { AppService } from 'src/app/app.service';
import { GatewayService } from 'src/app/core/services/gateway.service';
import { ProcessService } from 'src/app/core/services/process.service';
import { Util } from 'src/app/shared/utils/util';
import { DataService } from 'src/app/core/services/data.service';

@Component({
  selector: 'sc-data',
  templateUrl: './data.component.html',
  styleUrls: ['./data.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0', display: 'none'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ]
})
export class DataComponent extends DataTable<Data, DataFilter> implements OnInit {

  /**
   * List of platform's applications.
   */
  applications: Application[];

  /**
   * List of platform's gateways.
   */
  gateways: Gateway[];

  /**
   * List of platform's processes.
   */
  processes: Process[];

  /**
   * List of platform's topics.
   */
  topics: string[];

  /**
   * Start query date.
   */
  startDate: Date;

  /**
   * End query date.
   */
  endDate: Date;

  /**
   * Data element that's displayed.
   */
  expandedElement: Data;

  /**
   * True after the first search, otherwise is false.
   */
  searchAlreadyDone: boolean;

  protected filterPredicate: (data: Data, filter: string) => boolean;

  constructor(
    public applicationService: ApplicationService,
    private appService: AppService,
    public gatewayService: GatewayService,
    public processService: ProcessService,
    public dataService: DataService,
    private cdr: ChangeDetectorRef
  ) {
    super();
    this.displayedColumns = [ 'gatewayName', 'processName', 'timestamp' ];
    this.applications = new Array();
    this.gateways = new Array();
    this.processes = new Array();
    this.topics = new Array();
    this.searchAlreadyDone = false;
    this.filterType = 'APPLICATION';
  }

  ngOnInit() {
    super.initDataTable();
    this.getApplications();
    this.getGateways();
  }


  /**
   * Get the data from the server.
   */
  getData() {
    this.dataService
      .getData(this.filterType, this.filterValue, this.startDate, this.endDate)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
      (data: Data[]) => {
        const gatewayMap = new Map();
        const processMap = new Map();
        for (const gateway of this.gateways) {
          gatewayMap.set(gateway.id, gateway.name);
        }
        for (const process of this.processes) {
          processMap.set(process.id, process.name);
        }
        this.dataSource.data = data.map(dataE => {
          dataE.gatewayName = gatewayMap.get(Number(dataE.gatewayId));
          dataE.processName = processMap.get(Number(dataE.processId));
          return dataE;
        });
        this.searchAlreadyDone = true;
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err));
  }

  /**
   * Updates the filterType
   */
  onFilterTypeChange(filterType) {
    this.filterType = filterType;
    this.filterValue = undefined;
    this.cdr.detectChanges();
  }

  /**
   * Retrieves the application for the user logged in.
   */
  private getApplications(): void {
    this.applicationService.getApplicationsForUser(this.appService.user.id)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (applications: Application[]) => {
        this.applications = applications;
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err));
  }

  /**
   * Retrieves the gateway for the user logged in.
   *
   */
  private getGateways(): void {
    this.gatewayService.getGatewaysByUserId(this.appService.user.id)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (gateways: Gateway[]) => {
        this.gateways = gateways;
        if (gateways.length > 0) {
          for (const gateway of gateways) {
            for (const process of gateway.processes) {
              this.processes.push(process);
              const property = process.properties.find(e => e.name === 'TOPIC' && e.type === 'CONFIG');
              if (property) {
                const topic = property.value;
                this.topics.push(topic);
              }
            }
          }
        }
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err));
  }

  /**
   * Triggered when the Start Date filter is changed.
   *
   * @date 2019-04-15
   * @param event - MatDatepickerInputEvent event.
   */
  public startDateChanged(event: MatDatepickerInputEvent<Date>): void {
    this.startDate = event.value;
  }

  /**
   * Triggered when the End Date filter is changed.
   *
   * @date 2019-04-15
   * @param event - MatDatepickerInputEvent event.
   */
  public endDateChanged(event: MatDatepickerInputEvent<Date>): void {
    this.endDate = Util.endOfDay(event.value);
  }

  /**
   * Show or hide a payload.
   */
  public toggleExpansion(data: Data): void {
    this.expandedElement =  this.expandedElement === data ? null : data;
  }

  /**
   * Used for callback in trackBy for topics.
   *
   * @date 2019-04-20
   * @param index - index of the element.
   * @param item - item to be tracked.
   * @returns - the identifier, in this case, the topic name.
   */
  public topicIdentity(index: number, item: string): string {
    return item;
  }

}
