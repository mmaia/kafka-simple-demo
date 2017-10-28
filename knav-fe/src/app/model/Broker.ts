import {TopicMetrics} from './TopicMetrics';

export class Broker {
  public id: string;
  public host: string;
  public port: number;
  public jmxPort: number;
  public topicMetrics: Array<TopicMetrics>;
  public jmxDomains: Array<String>;
}
