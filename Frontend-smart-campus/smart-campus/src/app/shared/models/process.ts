import { Property } from './property';
import { Entity } from './entity';

export class Process extends Entity {

  public name: string;
  public description: string;
  public properties: Property[];
  public alive: boolean;
  public gatewayId: number;

  constructor(id?: number, name?: string, description?: string, properties: Property[] = [], alive?: boolean, gatewayId?: number) {
    super();
    this.id = id;
    this.name = name;
    this.description = description;
    this.properties = properties;
    this.alive = alive;
    this.gatewayId = gatewayId;
  }

}
