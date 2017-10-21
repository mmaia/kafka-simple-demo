import { Component, OnInit } from '@angular/core';

@Component({
  selector: 'app-brokers',
  templateUrl: './brokers.component.html',
  styleUrls: ['./brokers.component.css']
})
export class BrokersComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

  public connect() {
    console.log("trying to connect to Broker JMX");
  }

}
