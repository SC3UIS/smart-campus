import { PropertyType } from './types';

export class Property {

  public type: PropertyType;
  public name: string;
  public value: string;

  constructor(name: string = '', type: PropertyType = 'CONFIG', value: string = '') {
    this.name = name;
    this.type = type;
    this.value = value;
  }
}
