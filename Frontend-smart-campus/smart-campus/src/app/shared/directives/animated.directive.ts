import {
  Directive,
  ElementRef,
  Input,
  OnInit,
  Renderer2
} from '@angular/core';

/**
 * Animates the current element using animate.css styles.
 * The animation name is required.
 * To se the available animations go to https://github.com/daneden/animate.css.
 *
 * @date 2018-11-15
 * @export
 */
@Directive({
  selector: '[scAnimated]'
})
export class AnimatedDirective implements OnInit {

  /**
   * Animate.css animation's name.
   *
   */
  @Input('scAnimated') animation: string;

  /**
   * Creates an instance of AnimatedDirective.
   * @date 2019-01-09
   * @param elr Reference to the element that has the directive.
   * @param renderer Angular Renderer
   */
  constructor(public elr: ElementRef, public renderer: Renderer2) { }

  /**
   * Directive's onInit lifecycle. Adds the proper css classes required for the animation.
   *
   * @date 2019-01-09
   */
  ngOnInit() {
    this.renderer.addClass(this.elr.nativeElement, 'animated');
    this.renderer.addClass(this.elr.nativeElement, this.animation);
  }

}
