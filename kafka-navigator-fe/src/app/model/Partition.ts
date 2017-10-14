export class Partition {
  public partitionNumber: number;
  public leader: number;
  public replicas: Array<number>;
  public isr: Array<number>;
}
