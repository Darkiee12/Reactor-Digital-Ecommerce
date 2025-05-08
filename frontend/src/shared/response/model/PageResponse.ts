export default interface PageResponse<T> {
  items: T[];
  instance: string;
  timestamp: string;
  page: Metadata;
}

export interface Metadata {
  page: number;
  size: number;
  totalPages: number;
  totalItems: number;
  hasNext: boolean;
  hasPrevious: boolean;
}
