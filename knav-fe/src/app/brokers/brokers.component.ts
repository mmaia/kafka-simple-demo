import {Component, OnInit} from '@angular/core';
import {KafkaService} from '../services/kafka/kafka.service';
import {Broker} from "../model/Broker";

declare var $: any;

@Component({
  selector: 'app-brokers',
  templateUrl: './brokers.component.html',
  styleUrls: ['./brokers.component.css']
})
export class BrokersComponent implements OnInit {

  private broker: Broker;

  constructor(private kafkaService: KafkaService) {
  }

  ngOnInit() {
  }

  public connect(hosts) {
    if (!(hosts.value.length >= 1)) {
      this.required();
      return;
    }

    this.kafkaService.connect(hosts.value).then((result) => {
      console.log('got domains back after connecting...');
      console.log(JSON.stringify(result));
    }).catch((error) => console.log(error));

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
