export class Admin {
  id: number;
  email: string;
  password: string;
  name: string;
  surname: string;
  role: string;
  // tslint:disable-next-line:variable-name
  access_token?: string;
  // tslint:disable-next-line:variable-name
  max_quota_organizations: number;
}
