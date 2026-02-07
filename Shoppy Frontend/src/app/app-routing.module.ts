import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomeComponent } from './home/home.component';
import { ProductsComponent } from './products/products.component';
import {WatchlistComponent} from "./watchlist/watchlist.component";
import { CartComponent } from './cart/cart.component';
import { AuthGuard } from './auth.guard';
import {ContactUsComponent} from './contact-us/contact-us.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import { ProductEditComponent } from './product-edit/product-edit.component';
import { CreateProductComponent} from "./create-product/create-product.component";

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'home', component: HomeComponent, canActivate: [AuthGuard] },
  { path: 'products', component: ProductsComponent, canActivate: [AuthGuard] },
  { path: 'watchlist', component: WatchlistComponent, canActivate: [AuthGuard] },
  { path: 'cart', component: CartComponent, canActivate: [AuthGuard] },
  { path: 'contact-us', component: ContactUsComponent, canActivate: [AuthGuard] },
  { path: 'admin-dashboard', component: AdminDashboardComponent, canActivate: [AuthGuard] },
  { path: 'products/edit/:id', component: ProductEditComponent, canActivate: [AuthGuard] },
  { path: 'products/create', component: CreateProductComponent, canActivate: [AuthGuard] },
  { path: '', redirectTo: '/login', pathMatch: 'full' } // Redirect root path to login
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
