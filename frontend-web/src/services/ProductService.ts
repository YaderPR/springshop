import type { Product, Category } from "../types/Product";


const API_URL = "http://localhost:8081/api/products";
const API_URL_CATEGORIES = "http://localhost:8081/api/products/categories";
const API_URL_APPARELS = "http://localhost:8081/api/products/apparels";
const API_URL_APPARELS_CATEGORIES = "http://localhost:8081/api/products/apparels/categories";


//PRODUCTOS
export async function getProducts(): Promise<Product[]> {
  const res = await fetch(API_URL);
  if (!res.ok) throw new Error("Error al obtener productos");
  return res.json();
}

export async function getProductById(id: number): Promise<Product> {
  const res = await fetch(`${API_URL}/${id}`);
  if (!res.ok) throw new Error("Error al obtener producto");
  return res.json();
}

export async function createProduct(data: Product): Promise<Product> {
  const res = await fetch(API_URL, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error("Error al crear producto");
  return res.json();
}

export async function updateProduct(id: number, data: Product): Promise<Product> {
  const res = await fetch(`${API_URL}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error("Error al actualizar producto");
  return res.json();
}

export async function deleteProduct(id: number): Promise<void> {
  const res = await fetch(`${API_URL}/${id}`, { method: "DELETE" });
  if (!res.ok) throw new Error("Error al eliminar producto");
}
// CATEGORÍAS DE PRODUCTOS
export async function getCategories(): Promise<Category[]> {
  const res = await fetch(`${API_URL_CATEGORIES}`); // Ajusta esta URL si es necesario
  if (!res.ok) throw new Error("Error al obtener categorías");
  return res.json();
}

// APPARELS
export async function getApparels(): Promise<Product[]> {
  const res = await fetch(API_URL_APPARELS);
  if (!res.ok) throw new Error("Error al obtener apparels");
  return res.json();
}

export async function getApparelById(id: number): Promise<Product> {
  const res = await fetch(`${API_URL_APPARELS}/${id}`);
  if (!res.ok) throw new Error("Error al obtener apparel");
  return res.json();
}

export async function createApparel(data: Product): Promise<Product> {
  const res = await fetch(API_URL_APPARELS, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error("Error al crear apparel");
  return res.json();
}

export async function updateApparel(id: number, data: Product): Promise<Product> {
  const res = await fetch(`${API_URL_APPARELS}/${id}`, {
    method: "PUT",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify(data),
  });
  if (!res.ok) throw new Error("Error al actualizar apparel");
  return res.json();
}

export async function deleteApparel(id: number): Promise<void> {
  const res = await fetch(`${API_URL_APPARELS}/${id}`, { method: "DELETE" });
  if (!res.ok) throw new Error("Error al eliminar apparel");
}
// CATEGORÍAS DE APPARELS
export async function getApparelCategories(): Promise<Category[]> {
  const res = await fetch(`${API_URL_APPARELS_CATEGORIES}`); // Ajusta esta URL si es necesario
  if (!res.ok) throw new Error("Error al obtener categorías de apparels");
  return res.json();
}