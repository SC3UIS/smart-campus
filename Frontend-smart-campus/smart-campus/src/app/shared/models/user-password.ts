export class UserPassword {

  public oldPass: string;
  public newPass: string;

  constructor(oldPass?: string, newPass?: string) {
    this.oldPass = oldPass;
    this.newPass = newPass;
  }

}
