import { OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { Entity } from '../models/entity';

/**
 * Util class that destroys all {@link takeUntil()} observers in the OnDestroy cycle.
 *
 * @date 2018-11-04
 * @export
 */
export class Subscribable implements OnDestroy {

  /**
   * {@link Subject} that emits a value and completes when the component is going to be destroyeded.
   *
   */
  public destroyed = new Subject();

  /**
   * Function used as a callback for ngFor's trackBy for all entities.
   *
   * @date 2019-04-20
   * @param index - index of the item.
   * @param item - item to be tracked. Must extend from Entity.
   * @returns - the identifier, in this case the id.
   */
  public identity(index: number, item: Entity): number {
    return item.id;
  }

  ngOnDestroy() {
    console.log('Cleaning component...');
    this.destroyed.next();
    this.destroyed.complete();
  }

}
