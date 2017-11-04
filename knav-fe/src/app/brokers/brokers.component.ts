import {Component, OnInit} from '@angular/core';
import {KafkaService} from '../services/kafka/kafka.service';
import {BrokerService} from '../services/broker.service';
import {TopicUtilsService} from '../services/topic-utils.service';

declare var $: any;

@Component({
  selector: 'app-brokers',
  templateUrl: './brokers.component.html',
  styleUrls: ['./brokers.component.css']
})
export class BrokersComponent implements OnInit {

  constructor(private kafkaService: KafkaService, public brokerService: BrokerService, public topicUtilsService: TopicUtilsService) {}

  ngOnInit() {}
  public connect(hosts) {
    if (!(hosts.value.length >= 1)) {
      this.required();
      return;
    }

    this.kafkaService.connect(hosts.value).then((result) => {
      console.log('got broker...');
      this.brokerService.addBroker(result);
    }).catch((error) => console.log(error));
  }

  private required() {
    $.notify({
      icon: 'notifications',
      message: 'Please specify a JMX server and port to connect'
    }, {
      type: 'danger',
      timer: 1000,
      placement: {
        from: 'top',
        align: 'center'
      }
    });
  }

}
