import { ThingType } from './types';
import { Property } from './property';
import { Entity } from './entity';

export class Device extends Entity {

  public name: string;
  public description: string;
  public type: ThingType;
  public gatewayId: number;
  public properties: Property[];

  constructor(id?: number, name?: string, description?: string, type?: ThingType, gatewayId?: number, properties: Property[] = []) {
    super();
    this.id = id;
    this.name = name;
    this.description = description;
    this.type = type;
    this.gatewayId = gatewayId;
    this.properties = properties;
  }

}
