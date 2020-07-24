import { Property } from "src/app/shared/models/property";

export class PropertyDialog {

  constructor(
    public currentProperties: Property[],
    public newProperty: Property,
    public oldProperty: Property,
    public isCreation: boolean,
    public onlyValueModifiable: boolean = false
  ) {}
}
