export interface Finder {
  type: 'username' | 'email' | 'uuid';
  value: string;
}
