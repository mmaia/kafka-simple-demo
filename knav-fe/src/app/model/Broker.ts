export class Broker {
  private allDomains: Array<String>;

  public getAllDomains(): Array<String> {
    return this.allDomains;
  }

  public setAllDomains(domains) {
    this.allDomains = domains;
  }

  public getKafkaDomains(): Array<String> {
    return this.allDomains.filter(this.isKafka);
  }

  private isKafka(domain: string): boolean {
    return domain.startsWith('k');
  }
}
