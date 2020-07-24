import { PropertyType } from 'src/app/shared/models/types';

export class PropertyRule {

  constructor(public type: PropertyType, public name: string, public canBeDeleted: boolean,
    public canBeEdited: boolean, public onlyValueModifiable: boolean) {

  }
}
