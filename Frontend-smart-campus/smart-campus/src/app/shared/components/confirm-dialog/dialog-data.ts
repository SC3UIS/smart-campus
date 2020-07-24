/**
 * Confirmation Dialog data object.
 *
 * @date 2019-01-19
 * @export
 */
export class DialogData {

  /**
   * Dialog title.
   *
   */
  public title: string;

  /**
   * Dialog question (description).
   *
   */
  public question: string;

  /**
   * Object key. Used for further operations.
   *
   */
  public key: any;

  /**
   * Creates an instance of DialogData.
   * @date 2019-01-19
   * @param title of the dialog.
   * @param question of the dialog.
   * @param [key] of the object owner of the dialog.
   */
  constructor(title: string, question: string, key?: any) {
    this.title = title;
    this.question = question,
    this.key = key;
  }

}
