import {Partition} from "./Partition";

export class Topic {
  public name: string;
  public partitions: Array<Partition>;
}
