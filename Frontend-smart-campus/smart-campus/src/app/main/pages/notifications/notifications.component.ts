import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatSortable, MatDialog, MatDatepickerInputEvent } from '@angular/material';
import { trigger, state, style, transition, animate } from '@angular/animations';
import { take, takeUntil } from 'rxjs/operators';

import { ActivatedRoute, Params } from '@angular/router';
import { ApiResponse } from 'src/app/shared/models/api-response';
import { AppService } from 'src/app/app.service';
import { ConfirmDialogComponent } from 'src/app/shared/components/confirm-dialog/confirm-dialog.component';
import { DataTable } from 'src/app/shared/utils/data-table';
import { DialogData } from 'src/app/shared/components/confirm-dialog/dialog-data';
import { Gateway } from 'src/app/shared/models/gateway';
import { GatewayService } from 'src/app/core/services/gateway.service';
import { Notification } from 'src/app/shared/models/notification';
import { NotificationService } from 'src/app/core/services/notification.service';
import { NotificationsFilter } from 'src/app/shared/models/types';
import { Process } from 'src/app/shared/models/process';
import { ProcessService } from 'src/app/core/services/process.service';
import { Util } from 'src/app/shared/utils/util';
import { BreakpointObserver, Breakpoints, BreakpointState } from '@angular/cdk/layout';
import { NotificationDialogComponent } from '../../components/notification-dialog/notification-dialog.component';

@Component({
  selector: 'sc-notifications',
  templateUrl: './notifications.component.html',
  styleUrls: ['./notifications.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0', display: 'none'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ]
})
export class NotificationsComponent extends DataTable<Notification, NotificationsFilter> implements OnInit {

  /**
   * Lists of gateways associated to the user.
   */
  public gatewaysSelect: Gateway[];

  /**
   * List of processes associated to the user.
   */
  public processSelect: Process[];

  /**
   * Indicates if the called web service to get the list of processes has finished.
   */
  public processesReady: boolean;

  /**
   * Indicates if the called web service to get the list of gateways has finished.
   */
  public gatewaysReady: boolean;

  /**
   * Stores the filter for Start Date.
   *
   */
  public startDate: Date;

  /**
   * Stores the filter for Start Date.
   *
   */
  public endDate: Date;

  /**
   * Store the item expanded in the table when opening the details of a notification.
   *
   */
  public expandedElement: Notification | null;

  public mobileRows: string[];

  private isMobile: boolean;

  /**
   * Creates an instance of NotificationsComponent.
   * @date 2019-04-15
   * @param appService - Application main service.
   * @param dialog - Material Dialog reference.
   * @param gatewayService - Gateway management service.
   * @param notificationService - Notification management service.
   * @param processService - Process management service.
   */
  constructor(
    protected activatedRoute: ActivatedRoute,
    private appService: AppService,
    private breakpointObserver: BreakpointObserver,
    private dialog: MatDialog,
    private gatewayService: GatewayService,
    private notificationService: NotificationService,
    private processService: ProcessService) {
    super(activatedRoute);
    this.displayedColumns = [ 'gateway', 'process', 'alive', 'read', 'timestamp', 'actions' ];
    this.mobileRows = [ ...this.displayedColumns, 'message' ];
    this.gatewaysSelect = [];
    this.processSelect = [ new Process(0, 'Ninguno') ];
  }

  ngOnInit() {
    super.initDataTable();
    this.observeMobile();

    // by default sort by timestamp.
    this.sort.sort(({ id: 'timestamp', start: 'desc' }) as MatSortable);
    this.dataSource.sort = this.sort;

    // load the notifications and all the objects needed for filters.
    this.getNotifications();
    this.getGateways();
    this.getProcesses();
  }

  private observeMobile(): void {
    this.breakpointObserver.observe([ Breakpoints.XSmall ])
      .pipe(takeUntil(this.destroyed))
      .subscribe((bp: BreakpointState) => this.isMobile = bp.matches);
  }

  /**
   * Retrieves the notifications for the current user.
   *
   * @date 2019-04-15
   */
  private getNotifications(): void {
    this.notificationService.getNotificationsByUser(this.appService.user.id)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (notifications: Notification[]) => {
          this.notificationService.notifications = notifications;
          this.dataSource.data = notifications;
          this.openSelectedNotification();
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err)
      );
  }

  /**
   * Retrieves the gateway for the user logged in.
   *
   * @date 2019-04-04
   */
  private getGateways(): void {
    this.gatewayService.getGatewaysByUserId(this.appService.user.id)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (gateways: Gateway[]) => {
          this.gatewayService.gateways = gateways;
          this.buildGatewaysSelect();
          this.gatewaysReady = true;
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err));
  }

  /**
   * Fills the gatewaysSelect field used to show the gateways that belong to the user when applying the Gateways filter.
   *
   * @date 2019-04-15
   */
  private buildGatewaysSelect(): void {
    if (!this.gatewayService.gateways) {
      return;
    }

    this.gatewayService.gateways.forEach(gateway => this.gatewaysSelect.push(gateway));
  }

  /**
   * Retrieves the processes for the user logged in.
   *
   * @date 2019-04-15
   */
  private getProcesses(): void {
    this.processService.getProcessesByUserId(this.appService.user.id)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (processes: Process[]) => {
          this.processService.processes = processes;
          this.buildProcessesSelect();
          this.processesReady = true;
        }
      );
  }

  /**
   * Fills the processSelect field used to show the processes that belong to the user when applying the Process filter.
   *
   * @date 2019-04-15
   */
  private buildProcessesSelect(): void {
    if (!this.processService.processes) {
      return;
    }

    this.processService.processes.forEach(process => this.processSelect.push(process));
  }

  /**
   * Triggered when pressing "Delete" application button.
   *
   * @date 2019-04-04
   * @param id - id of the application to be deleted.
   */
  public onDeleteRecord(id: number): void {
    const deleteDialog = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: new DialogData(
        'Eliminar notificación',
        `Está seguro que desea eliminar la notificación`,
        id)
    });

    deleteDialog.afterClosed()
      .pipe(
        take(1),
        takeUntil(this.destroyed))
      .subscribe(result => result ? this.deleteNotification(result) : null);
  }

  /**
   * Deletes a notification consuming the REST Service, removing it from the in-memory list.
   *
   * @date 2019-04-15
   * @param id - id of the notification to be removed.
   */
  private deleteNotification(id: number): void {
    this.notificationService.deleteNotification(id)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (res: ApiResponse) => {
          this.appService.showSnack(res.message);
          this.notificationService.notifications.splice(
            this.notificationService.notifications.findIndex(notification => notification.id === id), 1);
          this.dataSource.data = this.notificationService.notifications;
          this.afterRecordDeleted();
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err)
      );
  }

  /**
   * Opens the selected notification if the page was accessed using a 'selected' query param.
   *
   * @date 2019-04-18
   */
  private openSelectedNotification(): void {
    this.activatedRoute.queryParams
      .pipe(takeUntil(this.destroyed))
      .subscribe(
        (params: Params) => {
          const selectedNotification = Number(params.selected);
          const notification = this.notificationService.notifications.find(notif => notif.id === selectedNotification);
          if (notification) {
            this.toggleExpansion(notification);
            const index = this.dataSource.filteredData.findIndex(not => not.id === selectedNotification);
            this.paginator.pageIndex = Math.floor(index / this.paginator.pageSize);
            this.dataSource.paginator = this.paginator;
          }
        }
      );
  }

  /**
   * Opens or closses the details of a given notification.
   *
   * @date 2019-04-15
   * @param notification - notification to be opened.
   */
  public toggleExpansion(notification: Notification): void {
    if (this.expandedElement === notification) {
      this.expandedElement = null;
    } else {
      this.expandedElement = notification;
      if (this.isMobile) {
        this.openNotificationDetail(notification);
      }
      // changed selection, then mark the notification as read.
      if (!notification.read) {
        this.notificationService.unreadNotificationsCount--;
        notification.read = true;
        this.markNotificationAsRead(this.appService.user.id, [ notification.id ]);
      }
    }
  }

  private openNotificationDetail(notification: Notification): void {
    this.dialog.open(NotificationDialogComponent, {
      width: '350px',
      data: notification
    });
  }

  /**
   * Marks a given notification as read.
   *
   * @date 2019-04-15
   * @param userId - id of the user logged in.
   * @param notificationIds - ids of the notifications to be marked as read.
   */
  private markNotificationAsRead(userId: number, notificationIds: number[]): void {
    this.notificationService.markNotificationsAsRead(userId, notificationIds)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (res: ApiResponse) => {},
        (err: HttpErrorResponse) => this.appService.handleGenericError(err)
      );
  }

  /**
   * Triggered when the Start Date filter is changed.
   *
   * @date 2019-04-15
   * @param event - MatDatepickerInputEvent event.
   */
  public startDateChanged(event: MatDatepickerInputEvent<Date>): void {
    this.startDate = event.value;
    this.filterValue =
      `${ this.startDate ? this.startDate.toUTCString() : 'null' } - ${ this.endDate ? this.endDate.toUTCString() : 'null' }`;
    this.applyFilter();
  }

  /**
   * Triggered when the End Date filter is changed.
   *
   * @date 2019-04-15
   * @param event - MatDatepickerInputEvent event.
   */
  public endDateChanged(event: MatDatepickerInputEvent<Date>): void {
    this.endDate = Util.endOfDay(event.value);
    this.filterValue =
      `${ this.startDate ? this.startDate.toUTCString() : 'null' } - ${ this.endDate ? this.endDate.toUTCString() : 'null' }`;
    this.applyFilter();
  }

  /**
   * Executed when the filter type is changed. Overrided because it's necessary to clean also the dates filters.
   *
   * @date 2019-04-15
   * @param newFilterType - the new Filter applied.
   */
  public onFilterTypeChange(newFilterType: NotificationsFilter): void {
    this.startDate = null;
    this.endDate = null;
    super.onFilterTypeChange(newFilterType);
  }

  /**
   * Filtering function applied over the elements in the table.
   *
   */
  protected filterPredicate: (data: Notification, filter: string) => boolean = (data: Notification, filter: string) => {
    switch (this.filterType) {
      case 'NONE': return true;
      case 'MESSAGE': return Util.stringContains(data.message, filter);
      case 'IS_ALIVE': return data.alive === (filter === 'true');
      case 'READ': return data.alive === (filter === 'true');
      case 'GATEWAY': return data.gatewayId === Number(filter);
      case 'PROCESS': return data.processId === Number(filter);
      case 'TIMESTAMP':
        const timestampUTC = new Date(data.timestamp).getTime();
        const startUTC = Util.toMilisUTC(this.startDate);
        const endUTC = Util.toMilisUTC(this.endDate);
        if (!startUTC && !endUTC) {
          return true;
        } else if (startUTC && !endUTC) {
          // since one specific day
          return startUTC <= timestampUTC;
        } else if (!this.startDate && this.endDate) {
          // until one specific day
          return timestampUTC <= endUTC;
        } else {
          // a range of dates
          return startUTC <= timestampUTC && timestampUTC <= endUTC;
        }
    }
  }

}
