import { NgModule } from '@angular/core';
import { CommonModule, } from '@angular/common';
import { BrowserModule  } from '@angular/platform-browser';
import { Routes, RouterModule } from '@angular/router';
import {HomeComponent} from "./home/home.component";
import {KafkaMetricsComponent} from "./kafka-metrics/kafka-metrics.component";

const routes: Routes =[
  { path: '',               redirectTo: 'home',                 pathMatch: 'full' },
  { path: 'home',           component: HomeComponent},
  { path: 'kafka-metrics',  component: KafkaMetricsComponent}
];

@NgModule({
  imports: [
    CommonModule,
    BrowserModule,
    RouterModule.forRoot(routes)
  ],
  exports: [
  ],
})
export class AppRoutingModule { }
