import { Component, Input } from '@angular/core';
import { Router } from '@angular/router';

import { DashboardService } from 'src/app/core/services/dashboard.service';
import { Notification } from 'src/app/shared/models/notification';
import { Util } from 'src/app/shared/utils/util';

@Component({
  selector: 'sc-notification-card',
  templateUrl: './notification-card.component.html',
  styleUrls: ['./notification-card.component.css']
})
export class NotificationCardComponent {

  @Input() notification: Notification;

  constructor(
    private dashboardService: DashboardService,
    private router: Router) { }

  public openNotification(notificationId: number) {
    this.router.navigate([ '/dashboard', 'notifications' ], { queryParams: { selected: String(notificationId) }});
  }

  public dismissNotification(): void {
    this.dashboardService.isNotificationShown = false;
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

}
