import {Component, OnInit} from '@angular/core';
import {KafkaService} from '../services/kafka/kafka.service';
import {Topic} from '../model/Topic';
import {TopicUtilsService} from '../services/topic-utils.service';

@Component({
  selector: 'app-kafka-topics',
  templateUrl: './kafka-topics.component.html',
  styleUrls: ['./kafka-topics.component.css']
})
export class KafkaTopicsComponent implements OnInit {
  public topics: Array<Topic>;
  constructor(private kafkaService: KafkaService, public topicUtilsService: TopicUtilsService) { }

  ngOnInit() {
    console.log('Topics onInit()');
    this.kafkaService.getTopics().then((result) => {
      this.topics = result;
    }).catch((error) => console.log(JSON.stringify(error)));
  }
}
