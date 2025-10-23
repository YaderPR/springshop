import React from 'react';
import ProductForm from '../components/ProductForm'; // Importa tu componente de formulario

const ProductFormPage: React.FC = () => {
    return (
        <main className="bg-primary container mx-auto ">
            <h1 className="text-secondary text-3xl font-bold mb-6"></h1>
            <ProductForm />
        </main>
    );
};

export default ProductFormPage;