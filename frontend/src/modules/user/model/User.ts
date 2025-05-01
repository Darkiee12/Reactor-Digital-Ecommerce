export interface User {
  uuid: string;
  username: string;
  fullName: string;
  email: string;
  gender: string;
  phoneNumber: string;
  address: string;
  roles: string[];
  createdAt: string;
  updatedAt: string;
  deleted: boolean;
}
