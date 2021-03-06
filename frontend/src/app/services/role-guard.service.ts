import {Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot} from '@angular/router';
import {AuthService} from './auth.service';
import {Observable} from 'rxjs';
import {TokenService} from './token.service';


@Injectable({
    providedIn: 'root'
})
export class RoleGuardService implements CanActivate {

    constructor(
        private _authService: AuthService,
        private _router: Router,
        private _token: TokenService
    ) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> | Promise<boolean> | boolean {
        const user: any = this._token.decode(this._token.getAccessToken());
        if (user) {
            if (user.authorities[0] === route.data.role) {
                return true;
            }
        }

        this._router.navigate(['/projects']);
        return false;
    }
}
