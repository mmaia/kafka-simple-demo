import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';
import { RouterModule } from '@angular/router';
import { ComponentsModule } from './components/components.module';
import { AppComponent } from './app.component';
import {ServicesModule} from "./services/services.module";
import {AppRoutingModule} from "./app.routing";
import { HomeComponent } from './home/home.component';
import { KafkaMetricsComponent } from './kafka-metrics/kafka-metrics.component';

@NgModule({
  declarations: [
    AppComponent,
    HomeComponent,
    KafkaMetricsComponent
  ],
  imports: [
    BrowserModule,
    FormsModule,
    HttpModule,
    ComponentsModule,
    RouterModule,
    ServicesModule,
    AppRoutingModule
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
