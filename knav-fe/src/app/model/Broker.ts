import {TopicMetrics} from './TopicMetrics';

export class Broker {
  public id: string;
  public topicMetrics: TopicMetrics;
  public allDomains: Array<String>;

  public getKafkaDomains(): Array<String> {
    return this.allDomains.filter(this.isKafka);
  }
  private isKafka(domain: string): boolean {
    return domain.startsWith('k');
  }
}
