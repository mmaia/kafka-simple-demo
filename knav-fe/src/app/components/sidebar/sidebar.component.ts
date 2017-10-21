import { Component, OnInit } from '@angular/core';
import {WindowService} from "../../services/window/window.service";

declare const $: any;
declare interface RouteInfo {
    path: string;
    title: string;
    icon: string;
    class: string;
}
export const ROUTES: RouteInfo[] = [
    { path: 'brokers', title: 'Brokers',  icon: 'device_hub', class: '' },
    { path: 'home', title: 'Home',  icon: 'dashboard', class: '' },
    { path: 'kafka-metrics', title: 'Kafka Client Metrics',  icon:'equalizer', class: '' },
    { path: 'kafka-topics', title: 'Kafka Topics',  icon:'layers', class: '' }
];

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.css']
})
export class SidebarComponent implements OnInit {
  menuItems: any[];

  constructor(private windowService: WindowService) { }

  ngOnInit() {
    this.menuItems = ROUTES.filter(menuItem => menuItem);
  }
  isMobileMenu() {
      if (this.windowService.nativeWindow.width > 991) {
          return false;
      }
      return true;
  };
}
