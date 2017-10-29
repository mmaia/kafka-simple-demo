import {Component, OnInit} from '@angular/core';
import {KafkaService} from '../services/kafka/kafka.service';
import {Broker} from '../model/Broker';
import {BrokerService} from '../services/broker.service';
import {TopicMetric} from '../model/TopicMetric';

declare var $: any;

@Component({
  selector: 'app-brokers',
  templateUrl: './brokers.component.html',
  styleUrls: ['./brokers.component.css']
})
export class BrokersComponent implements OnInit {

  public broker: Broker;
  public domains: Array<String>;
  public topicMetrics: Array<TopicMetric>;

  constructor(private kafkaService: KafkaService, public brokerService: BrokerService) {
    this.broker = new Broker();
  }
  ngOnInit() {
  }
  public connect(hosts) {
    if (!(hosts.value.length >= 1)) {
      this.required();
      return;
    }

    this.kafkaService.connect(hosts.value).then((result) => {
      console.log('got broker...');
      console.log(JSON.stringify(result));
      this.broker = result;
      this.domains = this.broker.jmxDomains;
      this.topicMetrics = this.brokerService.sortBrokerTopMetrics(this.broker.topicMetricList);
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
