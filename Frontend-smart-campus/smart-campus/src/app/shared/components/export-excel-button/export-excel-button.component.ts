import { Component, Input } from '@angular/core';

import { Util } from '../../utils/util';

/**
 * Button used to download some information (an array of objects) to an excel file.
 *
 * @date 2019-04-20
 * @export
 */
@Component({
  selector: 'sc-export-excel-button',
  templateUrl: './export-excel-button.component.html',
  styleUrls: ['./export-excel-button.component.css']
})
export class ExportExcelButtonComponent {

  /**
   * Information to be downloaded.
   *
   */
  @Input() json: any[];

  /**
   * Name of the file to be downloaded.
   *
   */
  @Input() filename: string;

  /**
   * Creates an instance of ExportExcelButtonComponent.
   * @date 2019-04-20
   */
  constructor() { }

  /**
   * Exports the table to an excel file.
   *
   */
  public saveExcel(): void {
    Util.exportAsExcelFile(this.json, this.filename);
  }

}
