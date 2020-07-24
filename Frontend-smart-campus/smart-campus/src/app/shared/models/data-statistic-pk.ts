export class DataStatisticPk {
  gatewayId: string;
  processId: string;
  hour: string;
  dayOfWeek: string;
  dayOfMonth: string;
  month: string;

  constructor(
    gatewayId: string,
    processId: string,
    hour: string,
    dayOfWeek: string,
    dayOfMonth: string,
    month: string,
  ) {
    this.gatewayId = gatewayId;
    this.processId = processId;
    this.hour = hour;
    this.dayOfWeek = dayOfWeek;
    this.dayOfMonth = dayOfMonth;
    this.month = month;
  }
}
