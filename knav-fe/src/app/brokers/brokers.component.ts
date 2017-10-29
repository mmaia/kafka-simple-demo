import {Component, OnInit} from '@angular/core';
import {KafkaService} from '../services/kafka/kafka.service';
import {Broker} from '../model/Broker';

declare var $: any;

@Component({
  selector: 'app-brokers',
  templateUrl: './brokers.component.html',
  styleUrls: ['./brokers.component.css']
})
export class BrokersComponent implements OnInit {

  public broker: Broker;
  public domains: Array<String>;
  constructor(private kafkaService: KafkaService) {
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
    }).catch((error) => console.log(error));
  }

  public getAttributeTypeText(attributeName: string): String {
    let result = '';
    switch (attributeName) {
      case ('BYTES_IN_PER_SEC'): {
        result = 'Bytes in per sec';
        break;
      }
      case ('BYTES_OUT_PER_SEC'): {
        result = 'Bytes out per sec';
        break;
      }
      case ('BYTES_REJECTED_PER_SEC'): {
        result = 'Bytes rejected per sec';
        break;
      }
      case ('FAILED_FETCH_REQUESTS_PER_SEC'): {
        result = 'Failed fetch requests per sec';
        break;
      }
      case ('FAILED_PRODUCE_REQUESTS_PER_SEC'): {
        result = 'Failed produce requests per sec';
        break;
      }
      case ('MESSAGES_IN_PER_SEC'): {
        result = 'Messages in per sec';
        break;
      }
      case ('TOTAL_FETCH_REQUESTS_PER_SEC'): {
        result = 'Total fetch requests per sec';
        break;
      }
      case ('TOTAL_PRODUCE_REQUESTS_PER_SEC'): {
        result = 'Total produce requests per sec';
        break;
      }
      default: {
        throw new Error('could not recognize the specified attribute: ' + attributeName);
      }
    }
    return result;
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
