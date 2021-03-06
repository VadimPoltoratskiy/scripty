import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';

const routes: Routes = [
    {
        path: 'projects',
        loadChildren: './layouts/projects/projects.module#ProjectsModule',
        data: {title: 'Projects'}
        },
    {
        path: 'admin',
        loadChildren: './layouts/admin-page/admin-page.module#AdminPageModule'
    },
    {
        path: 'report',
        loadChildren: './layouts/admin-ticket/admin-ticket.module#AdminTicketModule',
        data: {title: 'Report a bug'}
    },
    {
        path: '',
        redirectTo: '/projects',
        pathMatch: 'full'
    },
    {
        path: 'admin',
        loadChildren: './layouts/admin-page/admin-page.module#AdminPageModule'
    },
    {
        path: 'userActivation',
        loadChildren: './layouts/user-activation/user-activation.module#UserActivationModule',
        data: {title: 'User activation'}
    },
    {
        path: '**',
        redirectTo: '/projects'
    }
];

@NgModule({
    imports: [RouterModule.forRoot(routes/*, { useHash: true }*/)],
    exports: [RouterModule]
})
export class AppRoutingModule {
}
