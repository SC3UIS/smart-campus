import { Component, OnInit, ChangeDetectorRef } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { ChartType, ChartOptions } from 'chart.js';
import { take, takeUntil } from 'rxjs/operators';

import { DataService } from 'src/app/core/services/data.service';
import { Subscribable } from 'src/app/shared/utils/subscribable';
import { DataStatistic } from 'src/app/shared/models/data-statistic';
import { AppService } from 'src/app/app.service';
import { DataStatisticType } from 'src/app/main/components/data-statistics/data-statistic-type';
import { ProcessService } from 'src/app/core/services/process.service';
import { GatewayService } from 'src/app/core/services/gateway.service';
import { forkJoin } from 'rxjs';
import { Util } from 'src/app/shared/utils/util';

@Component({
  selector: 'sc-data-statistics',
  templateUrl: './data-statistics.component.html',
  styleUrls: ['./data-statistics.component.css']
})
export class DataStatisticsComponent extends Subscribable implements OnInit {

  /**
   * True when the charts are ready.
   */
  public chartReady: boolean;

  /**
   * Stores the statistics.
   */
  public statistics: DataStatisticType;

  /**
   * Charts that are going to be displayed.
   */
  public charts: Array<any>;

  /**
   * Charts default options.
   */
  public barChartOptions: ChartOptions = {
    responsive: true,
    scales: { xAxes: [{}], yAxes: [{}] },
    plugins: {
      datalabels: {
        anchor: 'end',
        align: 'end',
      }
    }
  };

  /**
   * Default chart type.
   */
  public barChartType: ChartType = 'line';

  constructor(
    private dataService: DataService,
    private appService: AppService,
    private processService: ProcessService,
    private gatewayService: GatewayService,
    private cdr: ChangeDetectorRef
  ) {
    super();
    this.statistics = new DataStatisticType();
    this.charts = new Array();
    this.charts.push({
      label: 'Hora',
      property: 'hour'
    });
    this.charts.push({
      label: 'Dia de la semana',
      property: 'dayOfWeek'
    });
    this.charts.push({
      label: 'Dia del mes',
      property: 'dayOfMonth'
    });
    this.charts.push({
      label: 'Mes',
      property: 'month'
    });
   }

  /**
   * Loads statistics.
   */
  ngOnInit() {
    forkJoin(
      this.gatewayService.getGatewaysByUserId(this.appService.user.id),
      this.processService.getProcessesByUserId(this.appService.user.id)
    ).pipe(take(1), takeUntil(this.destroyed))
    .subscribe((responses: Array<Array<any>>) => {
      const gatewaysMap = {};
      const processesMap = {};
      for (const gateway of responses[0]) {
        gatewaysMap[gateway.id] = gateway.name;
      }
      for (const process of responses[1]) {
        processesMap[process.id] = process.name;
      }
      this.dataService.getStatistics(this.appService.user.id).pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (statistics: DataStatistic[]) => {
          if (statistics.length === 0) {
            return;
          }
          for (const statistic of statistics) {
            let type = '';
            let subtype = '';
            if (statistic.id.gatewayId) {
              statistic.id.gatewayId = gatewaysMap[statistic.id.gatewayId];
              if (statistic.id.gatewayId) {
                type = 'gatewayId';
              }
            } else if (statistic.id.processId) {
              statistic.id.processId = processesMap[statistic.id.processId];
              if (statistic.id.processId) {
                type = 'processId';
              }
            }
            if (statistic.id.hour) {
              subtype = 'hour';
            } else if (statistic.id.dayOfWeek) {
              subtype = 'dayOfWeek';
              statistic.id.dayOfWeek = Util.dayOfWeekToString(statistic.id.dayOfWeek);
            } else if (statistic.id.dayOfMonth) {
              subtype = 'dayOfMonth';
            } else if (statistic.id.month) {
              statistic.id.month = Util.monthToString(statistic.id.month);
              if (statistic.id.month) {
                subtype = 'month';
              }
            }
            if (type && subtype) {
              this.statistics[type][subtype].addDataElement(statistic, type, subtype);
            }
          }
          this.chartReady = true;
          this.cdr.detectChanges();
        },
        (err: HttpErrorResponse) => this.appService.handleGenericError(err)
      );
    },
    (err: HttpErrorResponse) => this.appService.handleGenericError(err))

  }

  /**
   * Change the current chart.
   */
  changeChart(chart) {
    this.barChartType = chart.value;
  }
}
