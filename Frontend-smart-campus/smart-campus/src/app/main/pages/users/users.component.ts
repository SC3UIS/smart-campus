import { ActivatedRoute, Router } from '@angular/router';
import { Component, OnInit } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { MatTableDataSource, MatDialog } from '@angular/material';
import { take, takeUntil } from 'rxjs/operators';

import { ApiResponse } from 'src/app/shared/models/api-response';
import { AppService } from 'src/app/app.service';
import { ConfirmDialogComponent } from 'src/app/shared/components/confirm-dialog/confirm-dialog.component';
import { DataTable } from 'src/app/shared/utils/data-table';
import { DialogData } from 'src/app/shared/components/confirm-dialog/dialog-data';
import { UsersFilter } from 'src/app/shared/models/types';
import { User } from 'src/app/shared/models/user';
import { Util } from 'src/app/shared/utils/util';
import { UserService } from 'src/app/core/services/user.service';

/**
 * Page to manage all the users.
 *
 * @date 2019-04-12
 * @export
 */
@Component({
  selector: 'sc-users',
  templateUrl: './users.component.html',
  styleUrls: ['./users.component.css']
})
export class UsersComponent extends DataTable<User, UsersFilter> implements OnInit {

  /**
   * Creates an instance of UsersComponent.
   * @date 2019-04-12
   * @param activatedRoute - current Route.
   * @param userService - User related service.
   * @param router - Angular router.
   */
  constructor(
    protected activatedRoute: ActivatedRoute,
    private appService: AppService,
    public userService: UserService,
    private dialog: MatDialog,
    protected router: Router) {
      super(activatedRoute, router);
      this.displayedColumns = [ 'id', 'name', 'username', 'actions' ];
  }

  ngOnInit() {
    super.initDataTable();
    this.getUsers();
    this.defaultSort();
  }

  /**
   * Triggered when pressing "Delete" user button.
   *
   * @date 2019-04-12
   * @param id - id of the user to be deleted.
   */
  public onDeleteRecord(id: number, name: string): void {
    const deleteDialog = this.dialog.open(ConfirmDialogComponent, {
      width: '350px',
      data: new DialogData(
        'Eliminar usuario',
        `Está seguro que desea eliminar el usuario ${ name }`,
        id)
    });

    deleteDialog.afterClosed()
      .pipe(
        take(1),
        takeUntil(this.destroyed))
      .subscribe(result => result ? this.deleteUser(result) : null);
  }

  /**
   *  Deletes the user identified by its id.
   *
   * @date 2019-04-12
   * @param id - id of the user to be deleted.
   */
  private deleteUser(id: number) {
    this.userService.deleteUser(id)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (res: ApiResponse) => {
          this.appService.showSnack('Usuario eliminado correctamente.');
          this.userService.users.splice(
            this.userService.users.findIndex(user => user.id === id), 1);
          this.dataSource = new MatTableDataSource(this.userService.users);
          this.dataSource.paginator = this.paginator;
          if (this.userService.users.length % this.dataSource.paginator.pageSize === 0) {
            this.dataSource.paginator.previousPage();
          }
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err)
      );
  }

  /**
   * Retrieves the user for the user logged in.
   *
   * @date 2019-04-12
   */
  private getUsers(): void {
    this.userService.getUsers()
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (users: User[]) => {
        this.userService.users = users;
        this.dataSource.data = users;
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err));
  }

  protected filterPredicate: (data: User, filter: string) => boolean = (data: User, filter: string) => {
    switch (this.filterType) {
      case 'ID':
        return Util.stringContains(String(data.id), filter);
      case 'NAME':
        return Util.stringContains(data.name, filter);
      case 'USERNAME':
        return Util.stringContains(data.username, filter);
      case 'NONE':
        return true;
    }
  }

}
