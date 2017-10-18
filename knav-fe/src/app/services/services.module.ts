import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {WindowService} from "./window/window.service";
import {KafkaService} from "./kafka/kafka.service";

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forRoot([]),
  ],
  declarations: [],
  providers: [WindowService, KafkaService],
  exports: []
})
export class ServicesModule { }
