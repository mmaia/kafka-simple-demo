import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { KafkaMetricsComponent } from './kafka-metrics.component';

describe('KafkaMetricsComponent', () => {
  let component: KafkaMetricsComponent;
  let fixture: ComponentFixture<KafkaMetricsComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ KafkaMetricsComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(KafkaMetricsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
