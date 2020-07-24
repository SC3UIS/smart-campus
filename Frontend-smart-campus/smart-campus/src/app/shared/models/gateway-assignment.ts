export class GatewayAssignment {

  public gatewayId: number;
  public applicationId: number;

  constructor(gatewayId: number, applicationId: number) {
    this.gatewayId = gatewayId;
    this.applicationId = applicationId;
  }
}
