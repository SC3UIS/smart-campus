import { Pipe, PipeTransform } from '@angular/core';
import { ThingType } from '../models/types';

@Pipe({
  name: 'deviceType'
})
export class DeviceTypePipe implements PipeTransform {

  transform(value: ThingType, args?: any): any {
    switch (value) {
      case 'ACTUATOR': return 'Actuador';
      case 'GATEWAY': return 'Gateway';
      case 'SENSOR': return 'Sensor';
    }
  }

}
