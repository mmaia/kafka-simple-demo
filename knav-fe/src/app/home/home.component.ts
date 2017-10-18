import { Component, OnInit } from '@angular/core';
import {KafkaService} from "../services/kafka/kafka.service";

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {

  constructor(private kafkaService: KafkaService) { }

  ngOnInit() {
  }

  connect(hosts: string) {
    console.log("hosts typed by user: " + hosts);
    this.kafkaService.connect(hosts).then((result) => {
      console.log(JSON.stringify(result))
    });
  }
}
