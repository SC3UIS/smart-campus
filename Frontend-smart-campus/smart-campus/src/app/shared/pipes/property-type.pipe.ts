import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
  name: 'propertyType'
})
export class PropertyTypePipe implements PipeTransform {

  /**
   * Transform the passed value from english to spanish.
   */
  transform(value: any, args?: any): any {
    switch (value) {
      case 'CONFIG': return 'Configuración';
      case 'INFORMATIVE': return 'Informativa';
      case 'REPORTED': return 'Reportada';
    }
  }
}
