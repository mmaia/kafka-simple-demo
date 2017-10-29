import {TopicMetric} from './TopicMetric';

export class Broker {
  public id: string;
  public host: string;
  public port: number;
  public jmxPort: number;
  public topicMetricList: Array<TopicMetric>;
  public jmxDomains: Array<String>;
  public os: {
    attributes: {
      SystemCpuLoad: number,
      AvailableProcessors: number,
      Arch: string,
      ProcessCpuLoad: number,
      Name: string
    }
  };
  public kafkaVersion: string;
}
