import { Component, OnInit, OnDestroy } from '@angular/core';
import { takeUntil } from 'rxjs/operators';

import { IMqttMessage, MqttService } from 'ngx-mqtt';

import { AppService } from './app.service';
import { DashboardService } from './core/services/dashboard.service';
import { Subscribable } from './shared/utils/subscribable';

/**
 * Main component used to bootstrap the application.
 *
 * @date 2019-04-09
 * @export
 */
@Component({
  selector: 'sc-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent extends Subscribable implements OnInit, OnDestroy {

  public title: string;

  constructor(
    public appService: AppService,
    public dashboardService: DashboardService,
    private mqttService: MqttService) {
    super();
    this.title = 'Smart Campus';
  }

  ngOnInit() {
    if (this.appService.isUserAuthenticated()) {
      this.subscribeToNotifications();
    }

    this.appService.subscribeToNotification
      .pipe(takeUntil(this.destroyed))
      .subscribe(() => this.subscribeToNotifications());
  }

  ngOnDestroy() {
    this.appService.subscribeToNotification.complete();
  }

  private subscribeToNotifications(): void {
    this.mqttService.observe(`notifications-${ this.appService.user.id }`)
    .subscribe((message: IMqttMessage) => {
      try {
        this.dashboardService.newNotification = JSON.parse(message.payload.toString());
      } catch (err) {
        console.error('An error occurred parsing a notification', err);
      }
      this.dashboardService.isNotificationShown = true;
      setTimeout(() => {
        this.dashboardService.isNotificationShown = false;
        this.dashboardService.newNotification = null;
      }, 5000);
    });
  }

}
