import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { HomeComponent } from './home/home.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { OrderDetailComponent } from './order-detail/order-detail.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { ProductDetailComponent } from './product-detail/product-detail.component';
import { ProductsComponent } from './products/products.component';
import { MatTableModule } from '@angular/material/table';
import { WatchlistComponent } from './watchlist/watchlist.component';
import { CartComponent } from './cart/cart.component';
import { ContactUsComponent } from './contact-us/contact-us.component';
import { AdminDashboardComponent } from './admin-dashboard/admin-dashboard.component';
import {MatPaginatorModule} from "@angular/material/paginator";
import { AdminTopProductsComponent } from './admin-top-products/admin-top-products.component';
import { AdminPopularProductsComponent } from './admin-popular-products/admin-popular-products.component';
import { AdminTotalSoldComponent } from './admin-total-sold/admin-total-sold.component';
import { AdminTopProfitableComponent } from './admin-top-profitable/admin-top-profitable.component';
import { ProductEditComponent } from './product-edit/product-edit.component';
import { CreateProductComponent } from './create-product/create-product.component';
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatInputModule} from "@angular/material/input";


@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    HomeComponent,
    OrderDetailComponent,
    ProductDetailComponent,
    ProductsComponent,
    WatchlistComponent,
    CartComponent,
    ContactUsComponent,
    AdminDashboardComponent,
    AdminTopProductsComponent,
    AdminPopularProductsComponent,
    AdminTotalSoldComponent,
    AdminTopProfitableComponent,
    ProductEditComponent,
    CreateProductComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    ReactiveFormsModule,
    FormsModule,
    MatDialogModule,
    BrowserAnimationsModule,
    MatButtonModule,
    MatTableModule,
    MatPaginatorModule,
    MatFormFieldModule,
    MatInputModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
