import { Device } from 'src/app/shared/models/device';
import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Gateway } from 'src/app/shared/models/gateway';
import { Subscribable } from 'src/app/shared/utils/subscribable';
import { AppService } from 'src/app/app.service';
import { GatewayService } from 'src/app/core/services/gateway.service';
import { DeviceService } from 'src/app/core/services/device.service';
import { take, takeUntil } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';

@Component({
  selector: 'sc-device',
  templateUrl: './device.component.html',
  styleUrls: ['./device.component.css']
})
export class DeviceComponent extends Subscribable implements OnInit {

  /**
   * Lists of gateways associated to the current devicees.
   */
  public gatewaysSelect: Gateway[];

  /**
   * Stores the device id.
   * 0 if it's a new device.
   */
  public deviceId: number;

  /**
   * Device object to be used.
   */
  public device: Device;

  /**
   * True if we are going to clone a device, otherwise is false.
   *
   */
  private clone: boolean;

  /**
   * Creates an instance of DeviceComponent.
   */
  constructor(
    public appService: AppService,
    private gatewayService: GatewayService,
    private deviceService: DeviceService,
    private activatedRoute: ActivatedRoute,
    private router: Router) {
      super();
      this.device = new Device();
      this.device.properties = [];
      this.gatewaysSelect = [];
    }

  /**
   * Gets the device id from the url.
   * If the id is different from zero the gateway is requested from the backend.
   */
  ngOnInit() {
    this.deviceId = Number(this.activatedRoute.snapshot.params.id);
    this.clone = this.activatedRoute.snapshot.queryParams.clone === 'true';
    this.getGateways();
    if (this.deviceId) {
      this.getDevice();
    }
  }

  /**
   * Gets the current device by id from the backend.
   */
  private getDevice(): void {
    this.deviceService.getDeviceById(this.deviceId)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (device: Device) => {
          this.device = device;
          if (this.device && !this.device.properties) {
            this.device.properties = [];
          }
        },
        (err: HttpErrorResponse) => {
          this.appService.handleGenericError(err);
          this.router.navigate([ '..' ], { relativeTo: this.activatedRoute });
        });
  }

  /**
   * Saves or updates the current device.
   */
  public saveOrUpdateDevice(): void {
    if (this.deviceId && !this.clone) {
      this.updateDevice();
    } else {
      this.createDevice();
    }
  }

  /**
   * Create a new device.
   */
  private createDevice(): void {
    this.deviceService.createDevice(this.device)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (device: Device) => {
        this.router.navigate([ '..' ], { relativeTo: this.activatedRoute });
        this.appService.showSnack('Proceso creado correctamente.');
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err)
    );
  }

  /**
   * Updates the current gateway with the new data.
   */
  private updateDevice(): void {
    this.deviceService.updateDevice(this.device)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (device: Device) => {
        this.device = device;
        this.appService.showSnack('Proceso actualizado correctamente.');
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err)
    );
  }

  /**
   * Retrieves the gateway for the user logged in.
   *
   * @date 2019-04-04
   */
  private getGateways(): void {
    this.gatewayService.getGatewaysByUserId(this.appService.user.id)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (gateways: Gateway[]) => {
        this.gatewayService.gateways = gateways;
        this.buildGatewaysSelect();
        // this.gatewaysReady = true;
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err));
  }

  /**
   * Builds the list of selectable gateways for the filter.
   */
  private buildGatewaysSelect(): void {
    if (!this.gatewayService.gateways) {
      return;
    }
    this.gatewayService.gateways.forEach(gateway => this.gatewaysSelect.push(gateway));
  }
}
