import {TopicMetrics} from './TopicMetrics';

export class Broker {
  public id: string;
  public topicMetrics: TopicMetrics;
  private allDomains: Array<String>;
  private kafkaServerDomain = 'kafka.server';

  public getAllDomains(): Array<String> {
    return this.allDomains;
  }

  public setAllDomains(domains) {
    this.allDomains = domains;
  }

  public getKafkaDomains(): Array<String> {
    return this.allDomains.filter(this.isKafka);
  }

  private isKafka(domain: string): boolean {
    return domain.startsWith('k');
  }
}
