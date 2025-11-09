import axios from "axios";
import type { 
  Product, 
  Category, 
  Apparel, 
  Supplement, 
  WorkoutAccessory,
  ApparelCategory,
  WorkoutAccessoryCategory
} from "../../types/Product";

const api = axios.create({
  baseURL: "http://localhost:8085/api/v2/products",
  headers: {
    "Content-Type": "application/json",
  },
});

export async function getProducts(): Promise<Product[]> {
  const { data } = await api.get<Product[]>("");
  return data;
}

export async function getProductById(id: number): Promise<Product> {
  const { data } = await api.get<Product>(`/${id}`);
  return data;
}

export async function deleteProduct(id: number): Promise<void> {
  await api.delete(`/${id}`);
}

// --- CATEGORÍAS GENÉRICAS (para Suplementos) ---
export async function getCategories(): Promise<Category[]> {
  const { data } = await api.get<Category[]>("/categories");
  return data;
}

// --- APPARELS ---
export async function getApparels(): Promise<Apparel[]> {
  const { data } = await api.get<Apparel[]>("/apparels");
  return data;
}

export async function createApparel(data: any): Promise<Apparel> {
  const res = await api.post<Apparel>("/apparels", data);
  return res.data;
}

export async function updateApparel(id: number, data: any): Promise<Apparel> {
  const res = await api.put<Apparel>(`/apparels/${id}`, data);
  return res.data;
}

export async function deleteApparel(id: number): Promise<void> {
  await api.delete(`/apparels/${id}`);
}

export async function getApparelCategories(): Promise<ApparelCategory[]> {
  const { data } = await api.get<ApparelCategory[]>("/apparels/categories");
  return data;
}

// --- SUPPLEMENTS ---
export async function getSupplements(): Promise<Supplement[]> {
  const { data } = await api.get<Supplement[]>("/supplements");
  return data;
}

export async function createSupplement(data: any): Promise<Supplement> {
  const res = await api.post<Supplement>("/supplements", data);
  return res.data;
}

export async function updateSupplement(id: number, data: any): Promise<Supplement> {
  const res = await api.put<Supplement>(`/supplements/${id}`, data);
  return res.data;
}

export async function deleteSupplement(id: number): Promise<void> {
  await api.delete(`/supplements/${id}`);
}

// --- WORKOUT ACCESSORIES ---
export async function getWorkoutAccessories(): Promise<WorkoutAccessory[]> {
  const { data } = await api.get<WorkoutAccessory[]>("/workout-accessories");
  return data;
}

export async function createWorkoutAccessory(data: any): Promise<WorkoutAccessory> {
  const res = await api.post<WorkoutAccessory>("/workout-accessories", data);
  return res.data;
}

export async function updateWorkoutAccessory(id: number, data: any): Promise<WorkoutAccessory> {
  const res = await api.put<WorkoutAccessory>(`/workout-accessories/${id}`, data);
  return res.data;
}

export async function deleteWorkoutAccessory(id: number): Promise<void> {
  await api.delete(`/workout-accessories/${id}`);
}

export async function getWorkoutAccessoryCategories(): Promise<WorkoutAccessoryCategory[]> {
  const { data } = await api.get<WorkoutAccessoryCategory[]>("/workoutaccessories/categories");
  return data;
}

//Sección de categorías CRUD
// --- CATEGORÍAS GENÉRICAS (para Suplementos) ---

export async function createCategory(data: { name: string }): Promise<Category> {
  const { data: newCategory } = await api.post<Category>("/categories", data);
  return newCategory;
}

export async function updateCategory(id: number, data: { name: string }): Promise<Category> {
  const { data: updatedCategory } = await api.put<Category>(`/categories/${id}`, data);
  return updatedCategory;
}

export async function deleteCategory(id: number): Promise<void> {
  await api.delete(`/categories/${id}`);
}

// --- CATEGORÍAS DE APPAREL (ROPA) ---

export async function createApparelCategory(data: { name: string }): Promise<ApparelCategory> {
  const { data: newCategory } = await api.post<ApparelCategory>("/apparels/categories", data);
  return newCategory;
}

export async function updateApparelCategory(id: number, data: { name: string }): Promise<ApparelCategory> {
  const { data: updatedCategory } = await api.put<ApparelCategory>(`/apparels/categories/${id}`, data);
  return updatedCategory;
}

export async function deleteApparelCategory(id: number): Promise<void> {
  await api.delete(`/apparels/categories/${id}`);
}


// --- CATEGORÍAS DE WORKOUT ACCESSORIES (ACCESORIOS) ---

export async function createWorkoutAccessoryCategory(data: { name: string }): Promise<WorkoutAccessoryCategory> {
  const { data: newCategory } = await api.post<WorkoutAccessoryCategory>("/workoutaccessories/categories", data);
  return newCategory;
}

export async function updateWorkoutAccessoryCategory(id: number, data: { name: string }): Promise<WorkoutAccessoryCategory> {
  const { data: updatedCategory } = await api.put<WorkoutAccessoryCategory>(`/workoutaccessories/categories/${id}`, data);
  return updatedCategory;
}

export async function deleteWorkoutAccessoryCategory(id: number): Promise<void> {
  await api.delete(`/workoutaccessories/categories/${id}`);
}