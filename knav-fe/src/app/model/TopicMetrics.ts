export class TopicMetrics {
  public bytesInPerSec: number;
  public bytesOutPerSec: number;
  public bytesRejectPerSec: number;
  public failedFetchRequestsPerSec: number;
  public failedProduceRequestsPerSec: number;
  public MessagesInPerSec: number;
  public TotalFetchRequestsPerSec: number;
  public totalProduceRequestsPerSec: number;

  public getAttributeText(attributeName: string): String {
    let result = '';
    switch (attributeName) {
      case 'BYTES_IN_PER_SEC':
        result = 'Bytes in per sec';
        break;
      case 'BYTES_OUT_PER_SEC':
        result = 'Bytes out per sec';
        break;
      case 'BYTES_REJECTED_PER_SEC':
        result = 'Bytes rejected per sec';
        break;
      case 'FAILED_FETCH_REQUESTS_PER_SEC':
        result = 'Failed fetch requests per sec';
        break;
      case 'FAILED_PRODUCE_REQUESTS_PER_SEC':
        result = 'Failed produce requests per sec';
        break;
      case 'MESSAGES_IN_PER_SEC':
        result = 'Messages in per sec';
        break;
      case 'TOTAL_FETCH_REQUESTS_PER_SEC':
        result = 'Total fetch requests per sec';
        break;
      case 'TOTAL_PRODUCE_REQUESTS_PER_SEC':
        result = 'Total produce requests per sec';
        break;
      default:
        throw new Error('could not recognize the specified attribute: ' + attributeName);
    }
    return result;
  }

}
