import { HttpHeaders } from '@angular/common/http';
import { MatSnackBarConfig } from '@angular/material';
import * as FileSaver from 'file-saver';
import * as XLSX from 'xlsx';
const EXCEL_TYPE = 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=UTF-8';
const EXCEL_EXTENSION = '.xlsx';

/**
 * General utility methods.
 *
 * @date 2019-03-31
 * @export
 */
export class Util {

  /**
   * Retrieve the General HTTP options.
   *
   * @date 2019-04-09
   * @returns - The options object.
   */
  // tslint:disable-next-line:ban-types
  public static options(): Object {
    return {
      headers: new HttpHeaders({
        'Content-Type':  'application/json'
      })
    };
  }

  /**
   * Retrieves the general Material Snack config used in the app.
   *
   * @date 2019-04-09
   * @returns - The snackbar options.
   */
  public static snackOptions(): MatSnackBarConfig {
    return {
      duration: 5000,
      horizontalPosition: 'end',
      panelClass: 'snack-pane'
    };
  }

  /**
   * Checks if a string contains (ignoring case and trailing whitespaces).
   *
   * @date 2019-04-09
   * @param value - String to be compared. Not nullable.
   * @param other - String checked to be contained inside the other. Not nullable.
   * @returns true if the string is contained in the other, false otherwise.
   */
  public static stringContains(value: string, other: string): boolean {
    return value.toLowerCase().includes(other.trim().toLowerCase());
  }

  /**
   * Returns the miliseconds of a date in UTC timezone.
   *
   * @date 2019-04-15
   * @param date - date to be transformed. Nullable.
   * @returns the miliseconds in UTC, null if the date is null.
   */
  public static toMilisUTC(date: Date): number {
    if (!date) {
      return null;
    }
    return Date.UTC(date.getUTCFullYear(), date.getUTCMonth(), date.getUTCDate(),
    date.getUTCHours(), date.getUTCMinutes(), date.getUTCSeconds());
  }

  /**
   * Transforms the Date to UTC.
   *
   * @date 2019-04-15
   * @param date - date to be transformed. Nullable.
   * @returns the Date in UTC, null if the date is null.
   */
  public static toUTC(date: Date): Date {
    if (!date) {
      return null;
    }
    return new Date(this.toMilisUTC(date));
  }

  /**
   * Returns a new Date exact of the given one but with the hours set to be end of the day.
   *
   * @date 2019-04-15
   * @param date - date to be transformed. Nullable.
   * @returns the Date in end of the Day, null if the date is null.
   */
  public static endOfDay(date: Date): Date {
    if (!date) {
      return null;
    }
    const endOfDay = new Date(date);
    endOfDay.setHours(23, 59, 59);
    return endOfDay;
  }

  /**
   * Indicates if a given date is today.
   *
   * @date 2019-04-15
   * @param date - date to be verified. Nullable.
   * @returns true if the given date is today, false if it's any other day or if it's null.
   */
  public static isToday(date: Date): boolean {
    if (!date) {
      return null;
    }
    const today = new Date();
    return date.getFullYear() === today.getFullYear() &&
      date.getMonth() === today.getMonth() &&
      date.getDate() === today.getDate();
  }

  /**
   * Return an string with the date formated in yyyy-mm-dd
   *
   * @param date - Date to be formated
   * @returns Date formated in yyyy-mm-dd
   */
  public static formatDate(date: Date): string {
    return new Date(date.getTime() - (date.getTimezoneOffset() * 60000 ))
    .toISOString()
    .split('T')[0];
  }

  /**
   * Converts a json into a excel file.
   */
  public static exportAsExcelFile(json: any[], excelFileName: string): void {
      const worksheet: XLSX.WorkSheet = XLSX.utils.json_to_sheet(json);
      const workbook: XLSX.WorkBook = { Sheets: { data: worksheet }, SheetNames: ['data'] };
      const excelBuffer: any = XLSX.write(workbook, { bookType: 'xlsx', type: 'array' });
      Util.saveAsExcelFile(excelBuffer, excelFileName);
  }

  /**
   * Downloads an excel file.
   */
  private static saveAsExcelFile(buffer: any, fileName: string): void {
     const data: Blob = new Blob([buffer], {type: EXCEL_TYPE});
     FileSaver.saveAs(data, fileName + '_export_' + new  Date().getTime() + EXCEL_EXTENSION);
  }

  public static dayOfWeekToString(day: any) {
    day = String(day);
    switch (day) {
      case '1': return 'Domingo';
      case '2': return 'Lunes';
      case '3': return 'Martes';
      case '4': return 'Miércoles';
      case '5': return 'Jueves';
      case '6': return 'Viernes';
      case '7': return 'Sábado';
    }
  }

  public static monthToString(month: any) {
    month = String(month);
    switch (month) {
      case '1': return 'Enero';
      case '2': return 'Febrero';
      case '3': return 'Marzo';
      case '4': return 'Abril';
      case '5': return 'Mayo';
      case '6': return 'Junio';
      case '7': return 'Julio';
      case '8': return 'Agosto';
      case '9': return 'Septiembre';
      case '10': return 'Octubre';
      case '11': return 'Noviembre';
      case '12': return 'Diciembre';
    }
  }

  public static monthShort(month: number): string {
    switch (month) {
      case 1: return 'Ene';
      case 2: return 'Feb';
      case 3: return 'Mar';
      case 4: return 'Abr';
      case 5: return 'May';
      case 6: return 'Jun';
      case 7: return 'Jul';
      case 8: return 'Ago';
      case 9: return 'Sep';
      case 10: return 'Oct';
      case 11: return 'Nov';
      case 12: return 'Dic';
    }
  }
}
