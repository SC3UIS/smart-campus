import {
    Directive,
    ElementRef,
    EventEmitter,
    HostListener,
    Output
} from '@angular/core';

/**
 * Directive used to know when a click was done outside the current HTML element.
 *
 * @date 2018-12-30
 * @export
 */
@Directive({
    selector: '[scClickOutside]'
})
export class ClickOutsideDirective {

    /**
     * Creates an instance of ClickOutsideDirective.
     * @date 2019-01-09
     * @param elr Reference to the element that has the directive.
     */
    constructor(private elr: ElementRef) { }

    /**
     * {@link EventEmitter} that emits when the click was done outside the current element.
     *
     */
    @Output() clickOutside = new EventEmitter<MouseEvent>();

    /**
     * Listener for click event to check if it was done over this element or another.
     *
     * @date 2019-01-09
     * @param event executed when clicking over an element.
     * @param targetElement of the event.
     */
    @HostListener('document:click', ['$event', '$event.target'])
    public onClick(event: MouseEvent, targetElement: HTMLElement): void {
        if (!targetElement) {
            return;
        }

        const clickedInside = this.elr.nativeElement.contains(targetElement);
        if (!clickedInside) {
            this.clickOutside.emit(event);
        }
    }
}
