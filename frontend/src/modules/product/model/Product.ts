export interface Product {
  name: string;
  uuid: string;
  brandName: string;
  description: string;
  price: number;
  specifications: { [key: string]: any };
  stockQuantity: number;
  type: null;
  createdAt: number;
  updatedAt: number;
  imagesUuid: string[];
  categories: string[];
}
