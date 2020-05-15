export class Employee {
  uidNumber: number;
  name?: string;
  surname?: string;
  username?: string;

  constructor(uidNumber: number, name: string, surname: string, username: string) {
    this.uidNumber = uidNumber;
    this.name = name;
    this.surname = surname;
    this.username = username;
  }

  public getName(): string {
    if (this.name && this.surname) {
      return this.name + ' ' + this.surname;
    } else {
      if (this.username) {
        return this.username;
      } else {
        return this.uidNumber.toString();
      }
    }
  }
}
