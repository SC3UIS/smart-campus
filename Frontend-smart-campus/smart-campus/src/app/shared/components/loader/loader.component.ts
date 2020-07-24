import { Component, OnInit } from '@angular/core';

/**
 * Robot fullscreen loader, shown when an operation is blocking the user's interaction.
 *
 * @date 2019-04-09
 * @export
 */
@Component({
  selector: 'sc-loader',
  templateUrl: './loader.component.html',
  styleUrls: ['./loader.component.css']
})
export class LoaderComponent implements OnInit {

  /**
   * Creates an instance of LoaderComponent.
   * @date 2019-04-09
   */
  constructor() { }

  ngOnInit() {
  }

}
