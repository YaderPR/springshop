// types/Product.ts

export interface Product {
  id: number;
  name: string;
  description: string;
  price: number;
  stock: number;
  imageUrl: string;
  categoryId?: number; 
  categoryName?: string;
}

// --------------------
// Tipos Específicos
// --------------------

export interface Apparel extends Product {
  size: string;
  color: string;
  brand: string;
  apparelCategoryId?: number;
  apparelCategoryName?: string;
}

export interface Supplement extends Product {
  brand: string;
  flavor: string;
  size: string; 
  ingredients: string;
  usageInstructions: string;
  warnings: string;
}

export interface WorkoutAccessory extends Product {
  material: string;
  dimensions: string;
  weight: number;
  color: string;
  workoutAccessoryCategoryId?: number;
  workoutAccessoryCategoryName?: string;
}

// Unión de tipos para el formulario
export type AnyProduct = Apparel | Supplement | WorkoutAccessory;

// --------------------
// Categorías
// --------------------

export interface Category { 
  id: number;
  name: string;
  imageUrl?: string; 
}

export interface ApparelCategory {
  id: number;
  name: string;
  imageUrl?: string;
}

export interface workoutAccessoryCategory {
  id: number;
  name: string;
  imageUrl?: string;
}