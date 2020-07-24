
import { Application } from './application';
import { Device } from './device';
import { Process } from './process';
import { Property } from './property';
import { Entity } from './entity';

export class Gateway extends Entity {

  public name: string;
  public description: string;
  public ip: string;
  public properties: Property[];
  public devices: Device[];
  public processes: Process[];
  public applications: Application[];
  public alive: boolean;
  public userId: number;

  constructor(
    id?: number,
    name?: string,
    description?: string,
    ip?: string,
    userId?: number,
    applications: Application[] = [],
    alive?: boolean,
    properties: Property[] = [],
    devices: Device[] = [],
    processes: Process[] = []) {
      super();
      this.id = id;
      this.name = name;
      this.description = description;
      this.ip = ip;
      this.applications = applications;
      this.alive = alive;
      this.properties = properties;
      this.devices = devices;
      this.processes = processes;
    }

}
