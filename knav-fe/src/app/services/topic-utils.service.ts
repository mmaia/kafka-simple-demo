import {TopicMetric} from '../model/TopicMetric';

export class TopicUtilsService {

  public getAttributeTypeText(attributeName: string): String {
    let result = '';
    switch (attributeName) {
      case ('BYTES_IN_PER_SEC'): {
        result = 'Bytes in';
        break;
      }
      case ('BYTES_OUT_PER_SEC'): {
        result = 'Bytes out';
        break;
      }
      case ('BYTES_REJECTED_PER_SEC'): {
        result = 'Bytes rejected';
        break;
      }
      case ('FAILED_FETCH_REQUESTS_PER_SEC'): {
        result = 'Failed fetch requests';
        break;
      }
      case ('FAILED_PRODUCE_REQUESTS_PER_SEC'): {
        result = 'Failed produce requests';
        break;
      }
      case ('MESSAGES_IN_PER_SEC'): {
        result = 'Messages in';
        break;
      }
      case ('TOTAL_FETCH_REQUESTS_PER_SEC'): {
        result = 'Total fetch requests';
        break;
      }
      case ('TOTAL_PRODUCE_REQUESTS_PER_SEC'): {
        result = 'Total produce requests';
        break;
      }
      default: {
        throw new Error('could not recognize the specified attribute: ' + attributeName);
      }
    }
    return result;
  }

  public sortTopicMetrics(topicMetricList: Array<TopicMetric>): Array<TopicMetric> {
    const result = new Array<TopicMetric>();
    topicMetricList.forEach((topicMetric) => {
      if (topicMetric.topicMetricAttributeType === 'MESSAGES_IN_PER_SEC') {
        result.splice(0, 0, topicMetric);
      } else if (topicMetric.topicMetricAttributeType === 'BYTES_IN_PER_SEC') {
        result.splice(1, 0, topicMetric);
      } else if (topicMetric.topicMetricAttributeType === 'BYTES_OUT_PER_SEC') {
        result.splice(2, 0, topicMetric);
      } else {
        result.push(topicMetric);
      }
    });
    return result;
  }
}
