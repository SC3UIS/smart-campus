import { NgModule, LOCALE_ID } from '@angular/core';
import { CommonModule, registerLocaleData } from '@angular/common';
import { LoginComponent } from './components/login/login.component';
import { AuthenticationComponent } from './pages/authentication/authentication.component';
import { SigninComponent } from './components/signin/signin.component';
import { PasswordRecoveryComponent } from './components/password-recovery/password-recovery.component';
import { DashboardTemplateComponent } from './templates/dashboard-template/dashboard-template.component';
import { MaterialModule } from '../libs/material.module';
import { AppRoutingModule } from '../app-routing.module';
import { SharedModule } from '../shared/shared.module';
import { UserCardComponent } from './components/user-card/user-card.component';
import { SectionCardComponent } from './components/section-card/section-card.component';
import { ApplicationsComponent } from './pages/applications/applications.component';
import { ApplicationComponent } from './pages/applications/application/application.component';
import { GatewaysComponent } from './pages/gateways/gateways.component';
import { GatewayComponent } from './pages/gateways/gateway/gateway.component';
import { GatewaysByApplicationComponent } from './components/gateways-by-application/gateways-by-application.component';
import { GatewaySelectionDialogComponent } from './components/gateway-selection-dialog/gateway-selection-dialog.component';
import { PropertyTableComponent } from './components/property-table/property-table.component';
import { PropertyEditionDialogComponent } from './components/property-edition-dialog/property-edition-dialog.component';
import { DevicesComponent } from './pages/devices/devices.component';
import { DeviceComponent } from './pages/devices/device/device.component';
import { UsersComponent } from './pages/users/users.component';
import { UserComponent } from './pages/users/user/user.component';
import { ProcessesComponent } from './pages/processes/processes.component';
import { ProcessComponent } from './pages/processes/process/process.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { NotificationsComponent } from './pages/notifications/notifications.component';
import { ProcessesByGatewayComponent } from './components/processes-by-gateway/processes-by-gateway.component';
import { DevicesByGatewayComponent } from './components/devices-by-gateway/devices-by-gateway.component';
import { NotificationsCardComponent } from './components/notifications-card/notifications-card.component';
import { DataComponent } from './components/data/data.component';
import { NotificationCardComponent } from './components/notification-card/notification-card.component';
import { DataStatisticsComponent } from './components/data-statistics/data-statistics.component';
import { ProcessDataComponent } from './pages/process-data/process-data.component';
import { ChartsModule } from 'ng2-charts';

import localeES from '@angular/common/locales/es';
import { NotificationDialogComponent } from './components/notification-dialog/notification-dialog.component';
import { AdministrationStatisticsComponent } from './components/administration-statistics/administration-statistics.component';
registerLocaleData(localeES, 'es');

@NgModule({
  declarations: [
    AuthenticationComponent,
    LoginComponent,
    SigninComponent,
    PasswordRecoveryComponent,
    DashboardTemplateComponent,
    UserCardComponent,
    SectionCardComponent,
    ApplicationsComponent,
    ApplicationComponent,
    GatewaysComponent,
    GatewayComponent,
    GatewaysByApplicationComponent,
    GatewaySelectionDialogComponent,
    PropertyTableComponent,
    PropertyEditionDialogComponent,
    ProcessesComponent,
    ProcessDataComponent,
    ProcessComponent,
    DevicesComponent,
    DeviceComponent,
    UsersComponent,
    UserComponent,
    ProfileComponent,
    NotificationsComponent,
    ProcessesByGatewayComponent,
    DevicesByGatewayComponent,
    NotificationsCardComponent,
    DataComponent,
    NotificationCardComponent,
    DataStatisticsComponent,
    NotificationDialogComponent,
    AdministrationStatisticsComponent
  ],
  entryComponents: [
    GatewaySelectionDialogComponent,
    PropertyEditionDialogComponent,
    NotificationDialogComponent
  ],
  imports: [
    AppRoutingModule,
    ChartsModule,
    CommonModule,
    MaterialModule,
    SharedModule,
  ],
  providers: [
    { provide: LOCALE_ID, useValue: 'es' }
  ]
})
export class MainModule { }
