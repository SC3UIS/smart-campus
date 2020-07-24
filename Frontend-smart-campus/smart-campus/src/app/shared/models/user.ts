import { Entity } from './entity';

/**
 * User of the application.
 *
 * @date 2019-03-30
 * @export
 */
export class User extends Entity {

  public name: string;
  public username: string;
  public email: string;
  public password: string;
  public admin: boolean;

  /**
   * Creates an instance of User.
   * @date 2019-03-30
   * @param id of the user.
   * @param name of the user.
   * @param username of the user.
   * @param email of the user.
   * @param admin indicates if the user is an administrator or not.
   */
  constructor(id?: number, name?: string, username?: string, email?: string, admin?: boolean) {
    super();
    this.id = id;
    this.email = email;
    this.name = name;
    this.admin = admin;
    this.username = username;
  }

}
