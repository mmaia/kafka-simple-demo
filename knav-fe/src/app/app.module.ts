import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';
import {RouterModule} from '@angular/router';
import {ComponentsModule} from './components/components.module';
import {AppComponent} from './app.component';
import {ServicesModule} from './services/services.module';
import {AppRoutingModule} from './app.routing';
import {KafkaMetricsComponent} from './kafka-metrics/kafka-metrics.component';
import {BrokersComponent} from './brokers/brokers.component';
import {KafkaTopicsComponent} from './kafka-topics/kafka-topics.component';

@NgModule({
  declarations: [
    AppComponent,
    KafkaMetricsComponent,
    BrokersComponent,
    KafkaTopicsComponent
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
