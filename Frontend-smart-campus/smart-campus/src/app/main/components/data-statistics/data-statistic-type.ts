import { DataStatisticSubtype } from 'src/app/main/components/data-statistics/data-statistic-subtype';
export class DataStatisticType {
  gatewayId: DataStatisticSubtype;
  processId: DataStatisticSubtype;

  constructor() {
    this.gatewayId = new DataStatisticSubtype();
    this.processId = new DataStatisticSubtype();
  }
}
