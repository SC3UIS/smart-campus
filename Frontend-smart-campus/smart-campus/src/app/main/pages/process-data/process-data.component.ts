import { Component, OnInit, ViewChild } from '@angular/core';
import { takeUntil, take } from 'rxjs/operators';

import { BaseChartDirective, Label } from 'ng2-charts';
import { ChartDataSets } from 'chart.js';
import { MqttService, IMqttMessage } from 'ngx-mqtt';

import { ExternalService, BroadcastMessage, BroadcastResponse } from 'src/app/core/services/external.service';
import { Subscribable } from 'src/app/shared/utils/subscribable';

@Component({
  selector: 'sc-process-data',
  templateUrl: './process-data.component.html',
  styleUrls: ['./process-data.component.css']
})
export class ProcessDataComponent extends Subscribable implements OnInit {

  public aliveChartsColors = [
    {
      // backgroundColor: [ 'rgb(255, 0, 0)', 'rgb(255, 0 , 0)' ]
    }
  ];

  public lineChartDataP: ChartDataSets[] = [];
  public lineChartDataT: ChartDataSets[] = [];

  public dataP: number[] = [];
  public dataT: number[] = [];
  public lineChartLabelsP: Label[] = [];
  public lineChartLabelsT: Label[] = [];
  public previousValue = '0';
  public previousValueT = '0';

  @ViewChild(BaseChartDirective) chart: BaseChartDirective;

  constructor(private externalService: ExternalService, private mqttService: MqttService) {
    super();
    this.lineChartDataP = [
      {
        data: this.dataP,
        label: 'Potenciómetro'
      }
    ];

    this.lineChartDataT = [
      {
        data: this.dataT,
        label: 'Temperatura'
      }
    ];
  }

  ngOnInit() {
    this.connectMqttPotenciometro();
    this.connectMqttTemperatura();
  }

  private connectMqttPotenciometro(): void {
    this.mqttService.observe('potenciometro')
      .subscribe((message: IMqttMessage) => {
        const msg = message.payload.toString();
        try {
          const info: ProcessMessage = JSON.parse(msg);
          const date = new Date();
          //const measurement = Number(info);
          const measurement = Number(info.payload);
          this.dataP.push(measurement);
          const ledValue = measurement > 3 ? '1' : '0';
          this.lineChartLabelsP
            .push(`${ String(date.getHours()) }:${ String(date.getMinutes()) }:${ String(date.getSeconds()) }`);
          this.chart.chart.update();
          if (this.previousValue === ledValue) {
            return;
          }
          this.previousValue = ledValue;
          this.externalService.notifyActuatorData(new BroadcastMessage(ledValue, 'led'))
            .pipe(take(1), takeUntil(this.destroyed))
            .subscribe((res: BroadcastResponse[]) => {
              if (res && res.length > 0) {
                console.error(res);
              } else {
                console.log('Request sent successfuly');
              }
            });
        } catch (err) {
          console.error(err);
        }
      });
  }

  private connectMqttTemperatura(): void {
    this.mqttService.observe('temperatura')
      .subscribe((message: IMqttMessage) => {
        const msg = message.payload.toString();
        try {
          console.log(msg);
          const info: ProcessMessage = JSON.parse(msg);
          const date = new Date();
          //const measurement = Number(info);
          const measurement = Number(info.payload);
          this.dataT.push(measurement);
          const ledValue = measurement < 26 ? '1' : '0';
          this.lineChartLabelsT
            .push(`${ String(date.getHours()) }:${ String(date.getMinutes()) }:${ String(date.getSeconds()) }`);
          this.chart.chart.update();
          if (this.previousValueT === ledValue) {
            return;
          }
          this.previousValueT = ledValue;
          console.log('Requesting broadcast', ledValue);
          this.externalService.notifyActuatorData(new BroadcastMessage(ledValue, 'led-solo'))
            .pipe(take(1), takeUntil(this.destroyed))
            .subscribe((res: BroadcastResponse[]) => {
              if (res && res.length > 0) {
                console.error(res);
              } else {
                console.log('Request sent successfuly');
              }
            });
        } catch (err) {
          console.error(err);
        }
      });
  }
}

export class ProcessMessage {

  public gatewayId: number;
  public processId: number;
  public payload: string;
  public timestamp: Date;

}
