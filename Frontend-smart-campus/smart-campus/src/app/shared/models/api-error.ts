import { HttpErrorResponse } from '@angular/common/http';

/**
 * Maps the backend's error object.
 *
 * @date 2019-04-09
 * @export
 */
export class ApiError {

  /**
   * Date when the error occurred.
   *
   */
  public timestamp: Date;

  /**
   * Error message.
   *
   */
  public message: string;

  /**
   * Exception classname (short).
   *
   */
  public exception: string;

  /**
   * Http Status as a String.
   *
   */
  public status: string;

  /**
   * Creates an instance of ApiError.
   * @date 2019-04-09
   * @param [status] - HTTP Status as a String.
   * @param [message] - Error message.
   * @param [timestamp] - Error's date.
   * @param [exception] - Exception class name.
   */
  constructor(status?: string, message?: string, timestamp?: Date, exception?: string) {
    this.status = status;
    this.message = message;
    this.timestamp = timestamp;
    this.exception = exception;
  }

  /**
   * Creates an ApiError from an error occurred in the client-side.
   *
   * @date 2019-04-09
   * @param error - Error to be mapped.
   * @returns - The ApiError.
   */
  public static fromClientError(error: Error): ApiError {
      return new ApiError('CLIENT_ERROR', error.message, new Date(), 'ClientError');
  }

  /**
   * Creates an ApiError from a Generic HttpErrorMessage.
   *
   * @date 2019-04-09
   * @param error - Error to be mapped.
   * @returns - The ApiError.
   */
  public static fromGeneric(error: HttpErrorResponse): ApiError {
    return new ApiError('INTERNAL_ERROR', error.message, new Date(), 'InternalError');
  }

  /**
   * Creates an ApiError from a client timeout error.
   *
   * @date 2019-04-09
   * @param error - ProgressEvent Error to be mapped
   * @returns - The ApiError.
   */
  public static fromTimeout(error: ProgressEvent): ApiError {
    return new ApiError('INTERNAL_ERROR', 'El servidor no se encuentra disponible.', new Date(error.timeStamp), 'TimeoutEror');
  }

}
