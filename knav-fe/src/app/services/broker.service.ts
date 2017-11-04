import {Injectable} from '@angular/core';
import {Broker} from '../model/Broker';
import {TopicUtilsService} from './topic-utils.service';

@Injectable()
export class BrokerService {
  private brokers: Map<string, Broker>;

  constructor(private topicUtilsService: TopicUtilsService) {}

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
    broker.topicMetricList = this.topicUtilsService.sortTopicMetrics(broker.topicMetricList);
    this.brokers.set(broker.id, broker);
  }
  public getBroker(id: string): Broker {
    return this.brokers.get(id);
  }
}
