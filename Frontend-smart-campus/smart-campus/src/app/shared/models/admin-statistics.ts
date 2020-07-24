export class AdminStatistics {

  public gatewaysAlive: number;
  public gatewaysDeath: number;
  public processesAlive: number;
  public processesDeath: number;
  public devices: number;
  public applications: number;
  public changes: StatusChange[];

  constructor(
    gatewaysAlive?: number,
    gatewaysDeath?: number,
    processesAlive?: number,
    processesDeath?: number,
    devices?: number,
    applications?: number,
    changes: StatusChange[] = []) {
    this.gatewaysAlive = gatewaysAlive;
    this.gatewaysDeath = gatewaysDeath;
    this.processesAlive = processesAlive;
    this.processesDeath = processesDeath;
    this.devices = devices;
    this.applications = applications;
    this.changes = changes;
  }

}

export class StatusChange {

  public sentDate: Date;
  public alive: number;
  public death: number;

  constructor(sentDate?: Date, alive?: number, death?: number) {
    this.sentDate = sentDate;
    this.alive = alive;
    this.death = death;
  }

}
