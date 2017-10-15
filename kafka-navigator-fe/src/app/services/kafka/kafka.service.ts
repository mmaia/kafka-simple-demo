import { Injectable } from '@angular/core';
import {Http} from "@angular/http";
import 'rxjs/add/operator/toPromise';
import {Topic} from "../../model/Topic";

@Injectable()
export class KafkaService {

  private connectUrl: string = 'http://localhost:7000/api/topics/connect/';
  private kNavUrl: string = 'http://localhost:7000/api/topics/';
  private kHosts: Array<string>;

  constructor(private http: Http) { }

  getTopics(): Promise<any> {
    return this.http.get(this.kNavUrl)
      .toPromise()
      .then((response) => {
        return response.json() as Topic[]
      })
      .catch(this.handleError);
  }

  connect(hosts: Array<string>): Promise<any> {

    return Promise.resolve("not yet implemented");
  }

  private handleError(error: any): Promise<any> {
    console.error('An error occurred', error); // for demo purposes only
    return Promise.reject(error.message || error);
  }

}
