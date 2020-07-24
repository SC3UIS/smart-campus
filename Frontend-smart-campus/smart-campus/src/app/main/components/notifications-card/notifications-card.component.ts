import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { take, takeUntil } from 'rxjs/operators';

import { ApiResponse } from 'src/app/shared/models/api-response';
import { AppService } from 'src/app/app.service';
import { Notification } from 'src/app/shared/models/notification';
import { NotificationService } from 'src/app/core/services/notification.service';
import { Router } from '@angular/router';
import { Subscribable } from 'src/app/shared/utils/subscribable';
import { Util } from 'src/app/shared/utils/util';
import { DashboardService } from 'src/app/core/services/dashboard.service';

/**
 * Notifications card component.
 * Shows the last 10 notifications for the user and an option to see all the notifications.
 *
 * @date 2019-04-15
 * @export
 */
@Component({
  selector: 'sc-notifications-card',
  templateUrl: './notifications-card.component.html',
  styleUrls: ['./notifications-card.component.css']
})
export class NotificationsCardComponent extends Subscribable implements OnInit {

  /**
   * Creates an instance of NotificationsCardComponent.
   * @date 2019-04-15
   * @param appService - Main service.
   * @param notificationService - Notifications management service.
   * @param router - Angular router.
   */
  constructor(
    private appService: AppService,
    private dashboardService: DashboardService,
    public notificationService: NotificationService,
    private router: Router) {
      super();
    }

  ngOnInit() {
    this.getNotifications();
  }

  /**
   * Retrieves the notifications for the user consuming the get notifications REST service.
   *
   * @date 2019-04-15
   */
  private getNotifications(): void {
    this.notificationService.getNotificationsByUser(this.appService.user.id)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (notifications: Notification[]) => this.notificationService.notifications = notifications,
        (err: HttpErrorResponse) => this.appService.handleGenericError(err)
      );
  }

  /**
   * Checks if a date (in Date or String UTC) is today.
   *
   * @date 2019-04-15
   * @param date - date to be verified.
   * @returns true if the date is today, false if not or the date is null.
   */
  public isToday(date: string | Date): boolean {
    if (typeof(date) === 'string') {
      return Util.isToday(new Date(date));
    }
    return Util.isToday(date);
  }

  /**
   * Navigates to Notifications section.
   *
   * @date 2019-04-15
   */
  public seeAll(): void {
    this.router.navigate([ '/dashboard', 'notifications' ]);
    this.dashboardService.isNotificationsCardOpened = false;
  }

  public openNotification(notificationId: number): void {
    this.router.navigate([ '/dashboard', 'notifications' ], { queryParams: { selected: String(notificationId) }});
  }

  public markAsRead(notification: Notification): void {
    notification.read = true;
    this.notificationService.unreadNotificationsCount--;
    this.markNotificationAsRead(this.appService.user.id, [ notification.id ]);
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
   * Callback used to filter notifications to hide the read or hidden ones.
   *
   * @date 2019-04-19
   * @param notification - notification to be filtered.
   * @returns true if the notification matches the filter, false otherwise.
   */
  public filterCallback(notification: Notification): boolean {
    return !notification.read && !notification.hidden;
  }

}
