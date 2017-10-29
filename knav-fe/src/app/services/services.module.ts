import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {WindowService} from './window/window.service';
import {KafkaService} from './kafka/kafka.service';
import {BrokerService} from './broker.service';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forRoot([]),
  ],
  declarations: [],
  providers: [WindowService, KafkaService, BrokerService],
  exports: []
})
export class ServicesModule { }
