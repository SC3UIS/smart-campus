import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { CoreModule } from '../core.module';
import { Observable } from 'rxjs';
import { environment } from 'src/environments/environment';
import { Util } from 'src/app/shared/utils/util';

@Injectable({
  providedIn: CoreModule
})
export class ExternalService {

  constructor(private http: HttpClient) { }

  public notifyActuatorData(request: BroadcastMessage): Observable<BroadcastResponse[]> {
    return this.http.post<BroadcastResponse[]>(`${ environment.dataService }/data/message`, request, Util.options());
  }
}

export class BroadcastMessage {

  public message: string;
  public processIds: number[];
  public topic: string;

  constructor(message: string, topic?: string, processIds?: number[]) {
    this.message = message;
    this.topic = topic;
    this.processIds = processIds;
  }
}

export class BroadcastResponse {

  public gatewayIp: string;
  public errorMessage: string;

}
