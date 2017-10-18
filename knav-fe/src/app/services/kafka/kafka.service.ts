import {Injectable} from '@angular/core';
import {Headers, Http, RequestMethod, RequestOptions} from "@angular/http";
import 'rxjs/add/operator/toPromise';
import {Topic} from "../../model/Topic";

@Injectable()
export class KafkaService {

  private connectUrl: string = 'http://localhost:7000/api/bus/connect';
  private kNavUrl: string = 'http://localhost:7000/api/topics';
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

  connect(hosts: string): Promise<any> {
    this.kHosts = hosts.split(",");
    console.log(JSON.stringify(this.kHosts));
    return this.http.post(this.connectUrl, this.kHosts, this.reqOptions())
      .toPromise()
      .then((response) => console.log(JSON.stringify(response)));
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
