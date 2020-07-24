import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { ApplicationComponent } from './main/pages/applications/application/application.component';
import { ApplicationsComponent } from './main/pages/applications/applications.component';
import { AuthenticationGuard } from './core/guards/authentication.guard';
import { AuthenticationComponent } from './main/pages/authentication/authentication.component';
import { DashboardGuard } from './core/guards/dashboard.guard';
import { DashboardTemplateComponent } from './main/templates/dashboard-template/dashboard-template.component';
import { DeviceComponent } from './main/pages/devices/device/device.component';
import { DevicesComponent } from './main/pages/devices/devices.component';
import { GatewayComponent } from './main/pages/gateways/gateway/gateway.component';
import { GatewaysComponent } from './main/pages/gateways/gateways.component';
import { LoginComponent } from './main/components/login/login.component';
import { PasswordRecoveryComponent } from './main/components/password-recovery/password-recovery.component';
import { ProcessComponent } from './main/pages/processes/process/process.component';
import { ProcessesComponent } from './main/pages/processes/processes.component';
import { SigninComponent } from './main/components/signin/signin.component';
import { UsersComponent } from './main/pages/users/users.component';
import { UserComponent } from './main/pages/users/user/user.component';
import { ProfileComponent } from './main/pages/profile/profile.component';
import { NotificationsComponent } from './main/pages/notifications/notifications.component';
import { DataComponent } from './main/components/data/data.component';
import { UsersGuard } from './core/guards/users.guard';
import { ProcessDataComponent } from './main/pages/process-data/process-data.component';

const routes: Routes = [
  {
    path: '',
    component: AuthenticationComponent,
    children: [
      {
        path: '',
        redirectTo: 'login',
        pathMatch: 'full'
      },
      {
        path: 'login',
        component: LoginComponent,
      },
      {
        path: 'signin',
        component: SigninComponent,
      },
      {
        path: 'password',
        component: PasswordRecoveryComponent,
      }
    ],
    canActivate: [ AuthenticationGuard ]
  },
  {
    path: 'dashboard',
    component: DashboardTemplateComponent,
    children: [
      {
        path: '',
        component: DataComponent
      },
      {
        path: 'applications',
        component: ApplicationsComponent,
        pathMatch: 'full'
      },
      {
        path: 'applications/:id',
        component: ApplicationComponent
      },
      {
        path: 'gateways',
        component: GatewaysComponent,
        pathMatch: 'full'
      },
      {
        path: 'gateways/:id',
        component: GatewayComponent
      },
      {
        path: 'processes',
        component: ProcessesComponent,
        pathMatch: 'full'
      },
      {
        path: 'processes/:id',
        component: ProcessComponent
      },
      {
        path: 'devices',
        component: DevicesComponent,
        pathMatch: 'full'
      },
      {
        path: 'devices/:id',
        component: DeviceComponent
      },
      {
        path: 'users',
        component: UsersComponent,
        pathMatch: 'full',
        canActivate: [ UsersGuard ]
      },
      {
        path: 'users/:id',
        component: UserComponent
      },
      {
        path: 'profile',
        component: ProfileComponent
      },
      {
        path: 'notifications',
        component: NotificationsComponent
      }
    ],
    canActivate: [ DashboardGuard ]
  },
  {
    path: 'data',
    component: ProcessDataComponent
  },
  {
    path: '**',
    redirectTo: '/dashboard',
    pathMatch: 'full'
  }
];

/**
 * Sets and exports the Main routes.
 *
 * @date 2019-04-09
 * @export
 */
@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
