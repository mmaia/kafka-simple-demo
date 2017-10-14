import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import {WindowService} from "./window/window.service";

@NgModule({
  imports: [
    CommonModule,
    RouterModule.forRoot([]),
  ],
  declarations: [],
  providers: [WindowService],
  exports: []
})
export class ServicesModule { }
