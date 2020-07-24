import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import { ApiResponse } from 'src/app/shared/models/api-response';
import { CoreModule } from 'src/app/core/core.module';
import { environment } from 'src/environments/environment';
import { Notification } from 'src/app/shared/models/notification';
import { Util } from 'src/app/shared/utils/util';

@Injectable({
  providedIn: CoreModule
})
export class NotificationService {

  /**
   * Stores the notifications for the user.
   *
   */
  public notifications: Notification[];

  /**
   * Stores the amount of notifications 'unread' for the user.
   *
   */
  public unreadNotificationsCount: number;

  /**
   * Creates an instance of NotificationService.
   * @date 2019-04-14
   * @param http - Http client.
   */
  constructor(private http: HttpClient) { }

  /**
   * Retrieves the notifications for a given user, using pagination (by default retrieves all the notifications).
   *
   * @date 2019-04-14
   * @param userId - id of the user.
   * @param [page=null] - page of the lazy loading to be retrieved.
   * @param [count=null] - amount of notifications to be retrieved.
   * @returns the notifications for the user.
   */
  public getNotificationsByUser(userId: number, page: number = null, count: number = null): Observable<Notification[]> {
    let endpoint = `${ environment.adminService }/notifications/user/${ userId }`;
    if (page || count) {
      endpoint += `?page=${ page }&count=${ count }`;
    }
    return this.http.get<Notification[]>(endpoint, Util.options());
  }

  /**
   * Retrieves the amount of unread notifications for a user.
   *
   * @date 2019-04-14
   * @param userId - id of the user.
   * @returns the amount of unread notifications.
   */
  public getUnreadNotificationsCount(userId: number): Observable<number> {
    return this.http.get<number>(`${ environment.adminService }/notifications/user/${ userId }/count`, Util.options());
  }

  /**
   * Marks some notifications as read for a given user.
   *
   * @date 2019-04-14
   * @param userId - id of the user.
   * @param notifications - a List of notification ids.
   * @returns an Api Response indicating if the operation succeeded or not.
   */
  public markNotificationsAsRead(userId: number, notifications: number[]): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(`${ environment.adminService }/notifications/user/${ userId }/read`, notifications, Util.options());
  }

  /**
   * Removes a notification identified by its id.
   * This is not a deletion from the database but is just hide the notifications.
   *
   * @date 2019-04-14
   * @param notificationId - id of the notification
   * @returns an Api Response indicating if the operation succeeded or not.
   */
  public deleteNotification(notificationId: number): Observable<ApiResponse> {
    return this.http.delete<ApiResponse>(`${ environment.adminService }/notifications/notification/${ notificationId }`, Util.options());
  }

}
