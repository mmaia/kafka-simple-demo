import {Injectable} from '@angular/core';
import {Headers, Http, RequestMethod, RequestOptions} from "@angular/http";
import 'rxjs/add/operator/toPromise';
import {Topic} from "../../model/Topic";
import {KMetric} from "../../model/KMetric";

@Injectable()
export class KafkaService {
  public kMetrics: Array<KMetric>;
  private connectUrl = 'http://localhost:7000/api/bus/connect';
  private kNavUrl = 'http://localhost:7000/api/topics';
  private kHosts: Array<string>;

  constructor(private http: Http) {
  }

  getTopics(): Promise<any> {
    return this.http.get(this.kNavUrl)
      .toPromise()
      .then((response) => {
        return response.json() as Topic[]
      })
      .catch(this.handleError);
  }

  public connect(jmxHost: string): Promise<Array<String>> {
    console.log(JSON.stringify(jmxHost));
    return this.http.post(this.connectUrl, jmxHost, this.reqOptions())
      .toPromise()
      .then((response) => {
        return response.json() as Array<string>;
      });
  }

  getKMetrics(): Array<KMetric> {
    return this.kMetrics;
  }

  private reqOptions(): RequestOptions {
    const headers = new Headers();
    headers.append("Content-Type", "application/json");
    const options = new RequestOptions({
      method: RequestMethod.Post,
      headers: headers
    });
    return options;
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only
    return Promise.reject(error.message || error);
  }

}
