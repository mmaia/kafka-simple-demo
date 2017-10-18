export class Partition {
  public topic: string;
  public partition: number;
  public leader: number;
  public replicas: Array<number>;
  public isr: Array<number>;
}
