interface FullUpdateUser {
  username: string;
  password: string;
  email: string;
  fullName: string;
  gender: string;
  phone: string;
  address: string;
  city: string;
  state: string;
  country: string;
}

type UpdateUser = Partial<FullUpdateUser>;
export default UpdateUser;
