import React, { useState, useEffect } from 'react';
import type { Category } from '../../types/Product'; // Usamos el tipo base 'Category', ya que todos tienen 'id' y 'name'
import { Trash2, Edit, X, Save } from 'lucide-react';

// Definimos los tipos de las funciones que este componente recibirá como props
type FetchFunc = () => Promise<Category[]>;
type CreateFunc = (data: { name: string }) => Promise<Category>;
type UpdateFunc = (id: number, data: { name: string }) => Promise<Category>;
type DeleteFunc = (id: number) => Promise<void>;

interface Props {
  // Las 4 funciones CRUD que le pasaremos
  fetchCategories: FetchFunc;
  createCategory: CreateFunc;
  updateCategory: UpdateFunc;
  deleteCategory: DeleteFunc;
  // Un título para saber qué estamos editando
  title: string;
}

export default function CategoryManager({
  fetchCategories,
  createCategory,
  updateCategory,
  deleteCategory,
  title
}: Props) {
  const [categories, setCategories] = useState<Category[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Estado para el formulario
  const [name, setName] = useState('');
  const [editingId, setEditingId] = useState<number | null>(null);

  // Cargar datos
  useEffect(() => {
    loadData();
  }, [fetchCategories]); // Se vuelve a cargar si la función de fetch cambia (al cambiar de pestaña)

  const loadData = async () => {
    setIsLoading(true);
    setError(null);
    try {
      const data = await fetchCategories();
      setCategories(data);
    } catch (err: any) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const resetForm = () => {
    setName('');
    setEditingId(null);
  };

  // Manejador del formulario
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name) return;

    setIsLoading(true);
    try {
      if (editingId) {
        // Modo Actualizar
        await updateCategory(editingId, { name });
      } else {
        // Modo Crear
        await createCategory({ name });
      }
      resetForm();
      await loadData(); // Recargar la lista
    } catch (err: any) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  // Manejador de borrado
  const handleDelete = async (id: number) => {
    if (!window.confirm("¿Estás seguro de que quieres eliminar esta categoría?")) return;

    setIsLoading(true);
    try {
      await deleteCategory(id);
      await loadData(); // Recargar la lista
    } catch (err: any) {
      setError(err.message);
    } finally {
      setIsLoading(false);
    }
  };

  // Manejador para poner en modo "Editar"
  const handleEdit = (category: Category) => {
    setName(category.name);
    setEditingId(category.id);
  };

  return (
    <div className="space-y-6">
      <h3 className="text-xl font-semibold text-secondary">{title}</h3>

      {/* --- Formulario de Crear/Editar --- */}
      <form onSubmit={handleSubmit} className="flex gap-4">
        <input
          type="text"
          value={name}
          onChange={(e) => setName(e.target.value)}
          placeholder={editingId ? "Editar nombre..." : "Nueva categoría..."}
          className="flex-1 p-3 rounded bg-gray-800 border border-gray-600 text-white"
          required
        />
        <button
          type="submit"
          disabled={isLoading}
          className="bg-secondary text-primary font-bold py-3 px-5 rounded-xl hover:bg-lime-400 disabled:opacity-50"
        >
          {isLoading ? "..." : (editingId ? <Save size={20} /> : "Crear")}
        </button>
        {editingId && (
          <button
            type="button"
            onClick={resetForm}
            className="bg-gray-600 text-white py-3 px-5 rounded-full hover:bg-gray-500"
          >
            <X size={20} />
          </button>
        )}
      </form>

      {error && <p className="text-red-500">{error}</p>}

      {/* --- Lista de Categorías --- */}
      <div className="space-y-3">
        {isLoading && categories.length === 0 && <p>Cargando...</p>}
        {categories.map((cat) => (
          <div
            key={cat.id}
            className="flex items-center justify-between p-4 bg-primary border border-gray-700 rounded-lg"
          >
            <span className="text-lg">{cat.name} </span>
            <div className="flex gap-3">
              <button
                onClick={() => handleEdit(cat)}
                className="text-green-500 hover:text-green-400"
                title="Editar"
              >
                <Edit size={20} />
              </button>
              <button
                onClick={() => handleDelete(cat.id)}
                className="text-red-500 hover:text-red-400"
                title="Eliminar"
              >
                <Trash2 size={20} />
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}