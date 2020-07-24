import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { ApiResponse } from 'src/app/shared/models/api-response';
import { CoreModule } from 'src/app/core/core.module';
import { environment } from 'src/environments/environment';
import { User } from 'src/app/shared/models/user';
import { Util } from 'src/app/shared/utils/util';

/**
 * Service to manage users and authentication.
 *
 * @date 2019-03-31
 * @export
 */
@Injectable({
  providedIn: CoreModule
})
export class UserService {

  /**
   * Stores the users of the logged user (admin-only).
   */
  public users: User[];

  /**
   * Creates an instance of UserService.
   * @date 2019-04-09
   * @param http - Angular's HTTP client.
   */
  constructor(private http: HttpClient) { }

  /**
   * Authenticates the user.
   *
   * @date 2019-04-04
   * @param user - user to be authenticated.
   * @returns an Observable wrapping the User object containing the information about the logged in user.
   */
  public login(user: User): Observable<User> {
    return this.http.post<User>(`${environment.adminService}/users/authentication`, user, Util.options());
  }

  /**
   * Creates a new User.
   *
   * @date 2019-04-04
   * @param user- user to be created.
   * @returns an Observable wrapping the User object containing the information about the created in user.
   */
  public signin(user: User): Observable<User> {
    return this.http.post<User>(`${environment.adminService}/users/user`, user, Util.options());
  }

  /**
   * Recovers the password for a user with a given email address.
   *
   * @date 2019-04-04
   * @param email - email of the user that desires to change its password.
   * @returns an ApiResponse that indicates if the operation was successful or not.
   */
  public recoverPassword(email: string): Observable<ApiResponse> {
    return this.http.get<ApiResponse>(`${environment.adminService}/users/pass/${email}`, Util.options());
  }

  /**
   * Consumes delete user by id REST service.
   *
   * @date 2018-12-31
   * @param id of the user to be deleted.
   * @returns an {@link Observable} with the response.
   */
  public deleteUser(id: number): Observable<ApiResponse> {
    return this.http
      .delete<ApiResponse>(`${environment.adminService}/users/user/${id}`, Util.options());
  }

  /**
   * Consumes get users REST service.
   *
   * @date 2019-04-12
   * @returns an {@link Observable} with the response.
   */
  public getUsers(): Observable<User[]> {
    return this.http
      .get<User[]>(`${environment.adminService}/users`, Util.options());
  }

  /**
   * Consumes get user REST service.
   *
   * @date 2019-04-12
   * @param id of the user to be obtained.
   * @returns an {@link Observable} with the response.
   */
  public getUser(id: number): Observable<User> {
    return this.http
      .get<User>(`${environment.adminService}/users/user/${id}`, Util.options());
  }

  /**
   * Creates a new user.
   *
   * @date 2019-04-07
   * @param user - User to be created.
   * @returns the User with its id after creation.
   */
  public createUser(user: User): Observable<User> {
    return this.http.post<User>(`${environment.adminService}/users/user`, user, Util.options());
  }

  /**
   * Updates an existing user.
   *
   * @date 2019-04-07
   * @param user - User to be updated.
   * @returns the Application with the information as it was saved.
   */
  public updateUser(user: User): Observable<User> {
    return this.http.put<User>(`${environment.adminService}/users/user/${user.id}`, user, Util.options());
  }

}
