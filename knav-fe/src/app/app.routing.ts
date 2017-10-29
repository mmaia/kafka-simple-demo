import {NgModule} from '@angular/core';
import {CommonModule,} from '@angular/common';
import {BrowserModule} from '@angular/platform-browser';
import {RouterModule, Routes} from '@angular/router';
import {KafkaMetricsComponent} from './kafka-metrics/kafka-metrics.component';
import {BrokersComponent} from './brokers/brokers.component';
import {KafkaTopicsComponent} from './kafka-topics/kafka-topics.component';

const routes: Routes =[
  { path: '',               redirectTo: 'brokers',                 pathMatch: 'full' },
  { path: 'brokers',           component: BrokersComponent},
  { path: 'kafka-metrics',  component: KafkaMetricsComponent},
  { path: 'kafka-topics', component: KafkaTopicsComponent}
];

@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    RouterModule.forRoot(routes,
      {enableTracing: true}
      )
  ],
  exports: [
  ],
})
export class AppRoutingModule { }
