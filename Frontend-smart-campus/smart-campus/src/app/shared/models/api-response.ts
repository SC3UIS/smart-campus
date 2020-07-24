export class ApiResponse {

  public sucessful: boolean;
  public message: string;

  constructor(sucessful: boolean, message?: string) {
    this.sucessful = sucessful;
    this.message = message;
  }

}
