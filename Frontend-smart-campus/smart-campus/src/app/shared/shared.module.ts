import { CommonModule } from '@angular/common';
import { FlexLayoutModule } from '@angular/flex-layout';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { NgModule } from '@angular/core';

import { CustomFormsModule } from 'ngx-custom-validators';

import { AnimatedDirective } from './directives/animated.directive';
import { ConfirmDialogComponent } from './components/confirm-dialog/confirm-dialog.component';
import { ClickOutsideDirective } from './directives/click-outside.directive';
import { LoaderComponent } from './components/loader/loader.component';
import { MaterialModule } from '../libs/material.module';
import { PropertyTypePipe } from './pipes/property-type.pipe';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ExportExcelButtonComponent } from './components/export-excel-button/export-excel-button.component';
import { CallbackFilterPipe } from './pipes/callback-filter.pipe';
import { DeviceTypePipe } from './pipes/device-type.pipe';
import { HelpComponent } from './components/help/help.component';
import { HelpDialogComponent } from './components/help-dialog/help-dialog.component';

/**
 * Imports and exports all general Directives, Pipes and Components.
 *
 * @date 2019-04-09
 * @export
 */
@NgModule({
  declarations: [
    AnimatedDirective,
    ClickOutsideDirective,
    ConfirmDialogComponent,
    LoaderComponent,
    PropertyTypePipe,
    ExportExcelButtonComponent,
    CallbackFilterPipe,
    DeviceTypePipe,
    HelpComponent,
    HelpDialogComponent
  ],
  imports: [
    CommonModule,
    CustomFormsModule,
    FlexLayoutModule,
    FormsModule,
    HttpClientModule,
    MaterialModule,
    BrowserAnimationsModule
  ],
  entryComponents: [
    ConfirmDialogComponent,
    HelpDialogComponent
  ],
  exports: [
    AnimatedDirective,
    CallbackFilterPipe,
    ClickOutsideDirective,
    ConfirmDialogComponent,
    CustomFormsModule,
    DeviceTypePipe,
    FlexLayoutModule,
    FormsModule,
    HelpComponent,
    LoaderComponent,
    HttpClientModule,
    PropertyTypePipe,
    BrowserAnimationsModule,
    ExportExcelButtonComponent
  ]
})
export class SharedModule { }
