import { Entity } from './entity';

export class Notification extends Entity {

  public gatewayId: number;
  public gatewayName: string;
  public userId: number;
  public processId: number;
  public processName: string;
  public alive: boolean;
  public read: boolean;
  public timestamp: Date;
  public message: string;
  public hidden: boolean;

  constructor(
    id: number,
    userId: number,
    gatewayId: number,
    gatewayName: string,
    processId: number,
    processName: string,
    alive: boolean,
    read: boolean,
    timestamp: Date,
    message: string) {
      super();
      this.id = id;
      this.userId = userId;
      this.gatewayId = gatewayId;
      this.gatewayName = gatewayName;
      this.processId = processId;
      this.processName = processName;
      this.alive = alive;
      this.read = read;
      this.timestamp = timestamp;
      this.message = message;
    }
}
