export class Data {
  public gatewayId: number;
  public gatewayName: string;
  public processId: number;
  public processName: string;
  public payload: string;
  public timestamp: string;

  constructor(gatewayId?: number, processId?: number, payload?: string, timestamp?: string, gatewayName?: string, processName?: string) {
    this.gatewayId = gatewayId;
    this.processId = processId;
    this.payload = payload;
    this.timestamp = timestamp;
    this.gatewayName = gatewayName;
    this.processName = processName;
  }
}
