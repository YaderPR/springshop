export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  color?: string;
  brand?: string;
  size?: string;
  categoryId?: number;
  imageUrl?: string;
}

export interface Category {
  id: number;
  name: string;
}

export interface Apparel {
  id: number;
  categoryId: number;
  color: string;
  size: string;
  brand: string;
}

export interface ApparelCategory {
  id: number;
  name: string;
}