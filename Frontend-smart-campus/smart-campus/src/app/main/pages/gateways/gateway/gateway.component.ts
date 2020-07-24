import { Component, OnInit } from '@angular/core';
import { Subscribable } from 'src/app/shared/utils/subscribable';
import { ActivatedRoute, Router } from '@angular/router';
import { take, takeUntil } from 'rxjs/operators';
import { HttpErrorResponse } from '@angular/common/http';

import { AppService } from 'src/app/app.service';
import { Gateway } from 'src/app/shared/models/gateway';
import { GatewayService } from 'src/app/core/services/gateway.service';
import { PropertyRule } from 'src/app/main/components/property-table/property-rule.class';

@Component({
  selector: 'sc-gateway',
  templateUrl: './gateway.component.html',
  styleUrls: ['./gateway.component.css']
})
export class GatewayComponent extends Subscribable implements OnInit {

  public gatewayId: number;
  public gateway: Gateway;

  /**
   * Property's rules to apply in the table.
   *
   */
  public propertyRules: PropertyRule[];

  /**
   * True if we are going to clone a gateway, otherwise is false.
   *
   */
  public clone: boolean;

  constructor(
    public appService: AppService,
    private gatewayService: GatewayService,
    private activatedRoute: ActivatedRoute,
    private router: Router) {
      super();
      this.gateway = new Gateway();
      this.propertyRules = [new PropertyRule('CONFIG', 'broker_url', false, false, false)];
    }

  /**
   * Gets the gateway id from the url.
   * If the id is different from zero the gateway is requested from the backend.
   *
   */
  ngOnInit() {
    this.gatewayId = Number(this.activatedRoute.snapshot.params.id);
    this.clone = this.activatedRoute.snapshot.queryParams.clone === 'true';
    if (this.gatewayId) {
      this.getGateway();
    }
  }

  /**
   * Gets the current gateway by id from the backend.
   */
  private getGateway(): void {
    this.gatewayService.getGatewayById(this.gatewayId)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (gateway: Gateway) => {
          this.gateway = gateway;
          if (this.clone) {
            this.gateway.devices = new Array();
            this.gateway.processes = new Array();
          }
        },
        (err: HttpErrorResponse) => {
          this.appService.handleGenericError(err);
          this.router.navigate([ '..' ], { relativeTo: this.activatedRoute });
        });
  }

  /**
   * Saves or updates the current gateway.
   */
  public saveOrUpdateGateway(): void {
    if (this.gatewayId && !this.clone) {
      this.updateGateway();
    } else {
      this.createGateway();
    }
  }

  /**
   * Create a new gateway.
   */
  private createGateway(): void {
    this.gateway.userId = this.appService.user.id;
    if (this.clone) {
      this.gateway.id = null;
    }
    this.gatewayService.createGateway(this.gateway)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (gateway: Gateway) => {
        this.router.navigate([ '..' ], { relativeTo: this.activatedRoute });
        this.appService.showSnack('Gateway creado correctamente.');
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err)
    );
  }

  /**
   * Updates the curren gateway with the new data.
   */
  private updateGateway(): void {
    this.gatewayService.updateGateway(this.gateway)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (gateway: Gateway) => {
        this.gateway = gateway;
        this.appService.showSnack('Gateway actualizado correctamente.');
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err)
    );
  }

}
