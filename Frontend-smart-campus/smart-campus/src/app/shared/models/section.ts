export class Section {

  public title: string;
  public description: string;
  public url: string;
  public materialIconName: string;
  public cardColor: string;

  /**
   * Creates an instance of Section.
   *
   * @param [title] of the section.
   * @param [description] of the section.
   * @param [url] of the section.
   * @param [materialIconName] of the section's card.
   * @param [cardColor] of the section's card.
   */
  constructor(title?: string, description?: string, url?: string, materialIconName?: string, cardColor?: string) {
    this.title = title;
    this.description = description;
    this.url = url;
    this.materialIconName = materialIconName;
    this.cardColor = cardColor;
  }
}
