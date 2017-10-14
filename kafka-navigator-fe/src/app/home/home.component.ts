import { Component, OnInit } from '@angular/core';
import {KafkaService} from "../services/kafka/kafka.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  public hosts: string;

  constructor(private kafkaService: KafkaService) { }

  ngOnInit() {
  }

  connect() {
    this.kafkaService.getTopics().then((result) => {
      console.log("hosts: " + this.hosts);
      console.log(JSON.stringify(result))
    });
  }


}
