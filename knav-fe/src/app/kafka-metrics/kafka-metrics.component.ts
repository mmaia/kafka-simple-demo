import { Component, OnInit } from '@angular/core';
import {KafkaService} from "../services/kafka/kafka.service";
import {KMetric} from "../model/KMetric";

@Component({
  selector: 'app-kafka-metrics',
  templateUrl: './kafka-metrics.component.html',
  styleUrls: ['./kafka-metrics.component.css']
})
export class KafkaMetricsComponent implements OnInit {

  public metrics: Array<KMetric>;

  constructor(private kafkaService: KafkaService) { }

  ngOnInit() {
    this.metrics = this.kafkaService.getKMetrics();
  }
}
