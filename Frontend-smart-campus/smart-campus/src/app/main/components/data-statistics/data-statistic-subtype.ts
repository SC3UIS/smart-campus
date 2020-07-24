import { StatisticDetail } from 'src/app/shared/models/statistic-detail';

export class DataStatisticSubtype {
  hour: StatisticDetail;
  dayOfWeek: StatisticDetail;
  dayOfMonth: StatisticDetail;
  month: StatisticDetail;

  constructor() {
    this.hour = new StatisticDetail();
    this.dayOfWeek = new StatisticDetail();
    this.dayOfMonth = new StatisticDetail();
    this.month = new StatisticDetail();
  }
}
