import {Injectable} from '@angular/core';
import {TopicMetric} from '../model/TopicMetric';
import {Broker} from '../model/Broker';

@Injectable()
export class BrokerService {
  private brokers: Map<string, Broker>;
  public getBrokers(): Array<Broker> {
    if (this.brokers) {
      return Array.from(this.brokers.values());
    }
    return null;
  }
  public addBroker(broker: Broker) {
    if (!this.brokers) {
      this.brokers = new Map<string, Broker>();
    }
    broker.topicMetricList = this.sortBrokerTopMetrics(broker.topicMetricList);
    this.brokers.set(broker.id, broker);
  }
  public getBroker(id: string): Broker {
    return this.brokers.get(id);
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
  public sortBrokerTopMetrics(topicMetricList: Array<TopicMetric>): Array<TopicMetric> {
    const result = new Array<TopicMetric>();
    topicMetricList.forEach((topicMetric) => {
      if (topicMetric.topicMetricAttributeType === 'MESSAGES_IN_PER_SEC') {
        result.splice(0, 0, topicMetric);
      } else {
        result.push(topicMetric);
      }
    });
    return result;
  }

}
