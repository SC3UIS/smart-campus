import { Component, OnInit } from '@angular/core';
import { Subscribable } from 'src/app/shared/utils/subscribable';
import { HttpErrorResponse } from '@angular/common/http';
import { take, takeUntil } from 'rxjs/operators';
import { ActivatedRoute, Router } from '@angular/router';

import { AppService } from 'src/app/app.service';
import { GatewayService } from 'src/app/core/services/gateway.service';
import { Process } from 'src/app/shared/models/process';
import { ProcessService } from 'src/app/core/services/process.service';
import { Gateway } from 'src/app/shared/models/gateway';
import { PropertyRule } from 'src/app/main/components/property-table/property-rule.class';
import { Property } from 'src/app/shared/models/property';

@Component({
  selector: 'sc-process',
  templateUrl: './process.component.html',
  styleUrls: ['./process.component.css']
})
export class ProcessComponent extends Subscribable implements OnInit {

  /**
   * Lists of gateways associated to the current processes.
   */
  public gatewaysSelect: Gateway[];

  /**
   * Stores the process id.
   * 0 if it's a new process.
   */
  public processId: number;

  /**
   * Process object to be used.
   */
  public process: Process;

  /**
   * True if we are going to clone a process, otherwise is false.
   *
   */
  private clone: boolean;

  /**
   * Property's rules to apply in the table.
   *
   */
  public propertyRules: PropertyRule[];

  /**
   * Creates an instance of ProcessComponent.
   */
  constructor(
    public appService: AppService,
    private gatewayService: GatewayService,
    private processService: ProcessService,
    private activatedRoute: ActivatedRoute,
    private router: Router) {
      super();
      this.process = new Process();
      this.process.properties = [new Property('topic', 'CONFIG', '')];
      this.gatewaysSelect = [];
      this.propertyRules = [new PropertyRule('CONFIG', 'topic', false, true, true)];
    }

  /**
   * Gets the process id from the url.
   * If the id is different from zero the gateway is requested from the backend.
   *
   */
  ngOnInit() {
    this.processId = Number(this.activatedRoute.snapshot.params.id);
    this.clone = this.activatedRoute.snapshot.queryParams.clone === 'true';
    this.getGateways();
    if (this.processId) {
      this.getProcess();
    }
  }

  /**
   * Gets the current process by id from the backend.
   */
  private getProcess(): void {
    this.processService.getProcessById(this.processId)
      .pipe(take(1), takeUntil(this.destroyed))
      .subscribe(
        (process: Process) => {
          if (!process.properties) {
            process.properties = [];
          }

          if (!process.properties.some(property => property.name === 'topic' && property.type === 'CONFIG')) {
            process.properties.push(new Property('topic', 'CONFIG', ''));
          }

          this.process = process;
        },
        (err: HttpErrorResponse) => {
          this.appService.handleGenericError(err);
          this.router.navigate([ '..' ], { relativeTo: this.activatedRoute });
        });
  }

  /**
   * Saves or updates the current process.
   */
  public saveOrUpdateProcess(): void {
    if (this.processId && !this.clone) {
      this.updateProcess();
    } else {
      this.createProcess();
    }
  }

  /**
   * Create a new process.
   */
  private createProcess(): void {
    this.processService.createProcess(this.process)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (process: Process) => {
        this.router.navigate([ '..' ], { relativeTo: this.activatedRoute });
        this.appService.showSnack('Proceso creado correctamente.');
      },
      (err: HttpErrorResponse) => this.appService.handleGenericError(err)
    );
  }

  /**
   * Updates the current gateway with the new data.
   */
  private updateProcess(): void {
    this.processService.updateProcess(this.process)
    .pipe(take(1), takeUntil(this.destroyed))
    .subscribe(
      (process: Process) => {
        this.process = process;
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
