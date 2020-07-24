import { CoreModule } from '../core.module';
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { environment } from 'src/environments/environment';
import { Process } from 'src/app/shared/models/process';
import { Util } from 'src/app/shared/utils/util';
import { ApiResponse } from 'src/app/shared/models/api-response';


@Injectable({
  providedIn: CoreModule
})
export class ProcessService {

  /**
   * Stores the processes of the logged user.
   */
  public processes: Process[];

  /**
   * Creates an instance of ProcessService.
   * @date 2019-04-06
   * @param http - Angular's HTTP client.
   */
  constructor(private http: HttpClient) { }

  /**
   * Retrieves the processes for the given user idenfitied by its id.
   *
   * @date 2019-04-04
   * @param userId - id of the user whose process are being retrieved.
   * @returns an Array of Processes.
   */
  public getProcessesByUserId(userId: number): Observable<Process[]> {
    return this.http.get<Process[]>(`${ environment.adminService }/processes/user/${ userId }`, Util.options());
  }

  /**
   * Retrieves the process identified by its id.
   *
   * @date 2019-04-06
   * @param processId - id of the Process.
   * @returns the Process.
   */
  public getProcessById(processId: number): Observable<Process> {
    return this.http.get<Process>(`${ environment.adminService }/processes/process/${ processId }`, Util.options());
  }

  /**
   * Deletes the process identified by the given id.
   *
   * @date 2019-04-05
   * @param processId - id of the Process to be removed.
   * @returns an ApiResponse indicating if the operation succeeded or not.
   */
  public deleteProcessById(processId: number): Observable<ApiResponse> {
    return this.http.delete<ApiResponse>(`${ environment.adminService }/processes/process/${ processId }`, Util.options());
  }

  /**
   * Creates a new process.
   *
   * @date 2019-04-07
   * @param process - Process to be created.
   * @returns the Process with its id after creation.
   */
  public createProcess(process: Process): Observable<Process> {
    return this.http.post<Process>(`${ environment.adminService }/processes/process`, process, Util.options());
  }

  /**
   * Updates an existing process.
   *
   * @date 2019-04-07
   * @param process - Process to be updated.
   * @returns the Process with the information as it was saved.
   */
  public updateProcess(process: Process): Observable<Process> {
    return this.http.put<Process>(`${ environment.adminService }/processes/process/${ process.id }`,
      process, Util.options());
  }

  /**
   * Deploys a given process.
   *
   * @date 2019-04-24
   * @param processId id of the process to be deployed/undeployed.
   * @param deploy true to deploy it or re-deploy it, false to stop it.
   * @returns an ApiResponse that indicates if the operation succeeded or not.
   */
  public deployProcess(processId: number, deploy: boolean): Observable<ApiResponse> {
    return this.http.put<ApiResponse>(
      `${ environment.adminService }/processes/process/${ processId }/deploy/${ deploy }`, null, Util.options());
  }
}
