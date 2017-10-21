import { Component, OnInit } from '@angular/core';
import {isUndefined} from "util";
import {KafkaService} from "../services/kafka/kafka.service";
import {Router} from "@angular/router";
declare var $: any;
@Component({
  selector: 'app-brokers',
  templateUrl: './brokers.component.html',
  styleUrls: ['./brokers.component.css']
})
export class BrokersComponent implements OnInit {

  constructor(private kafkaService: KafkaService, private router: Router) { }

  ngOnInit() {
  }

  public connect(hosts) {
    if(hosts.value.length < 1) {  //invalid
      this.required();
      return;
    }

    this.kafkaService.connect(hosts).then((result) => {
      console.log(JSON.stringify(result));
      this.router.navigate(['/kafka-metrics']);
    });

  }

  private required() {
    $.notify({
      icon: "notifications",
      message: "Please specify a JMX server and port to connect"
    }, {
      type: "danger",
      timer: 1000,
      placement: {
        from: "top",
        align: "center"
      }
    });
  }

}
