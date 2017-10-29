export class TopicMetric {
  public topicMetricAttributeType: string;
  public attributes: {
    RateUnit: string,
    OneMinuteRate: number,
    EventType: string,
    Count: number,
    FifteenMinuteRate: number,
    FiveMinuteRate: number,
    MeanRate: number
  };
}
