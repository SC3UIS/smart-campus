import { PipeTransform, Pipe } from '@angular/core';

/**
 * Pipe that filters an array of elements using a given callback.
 *
 * @date 2019-04-19
 * @export
 */
@Pipe({
    name: 'callbackFilter'
})
export class CallbackFilterPipe implements PipeTransform {

    transform(items: any[], callback: (item: any) => boolean): any {
        if (!items || !callback) {
            return items;
        }
        return items.filter(item => callback(item));
    }
}
