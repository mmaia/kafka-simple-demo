import { Injectable } from '@angular/core';
import {Http} from "@angular/http";
import 'rxjs/add/operator/toPromise';

@Injectable()
export class KafkaService {

  private kNavUrl: string = 'api/topics/';
  private kHosts: Array<string>;

  constructor(private http: Http) { }

  getTopics(): Promise<any> {
    return this.http.get(this.kNavUrl)
      .toPromise()
      .then((response) => console.log(JSON.stringify(response)))
      .catch(this.handleError);
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only
    return Promise.reject(error.message || error);
  }

}
