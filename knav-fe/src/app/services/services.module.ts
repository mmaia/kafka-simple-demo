import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';
import {RouterModule} from '@angular/router';
import {WindowService} from './window/window.service';
import {KafkaService} from './kafka/kafka.service';
import {BrokerService} from './broker.service';
import {TopicUtilsService} from './topic-utils.service';

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forRoot([]),
  ],
  declarations: [],
  providers: [WindowService, KafkaService, BrokerService, TopicUtilsService],
  exports: []
})
export class ServicesModule { }
