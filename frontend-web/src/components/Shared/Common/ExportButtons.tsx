import React from 'react';
import { Download } from 'lucide-react';

// --- Importaciones de las librerías ---
import * as XLSX from 'xlsx';
import jsPDF from 'jspdf';
import autoTable from 'jspdf-autotable';

interface Props {
  data: any[];       // Los datos (JSON) para exportar
  filename: string;  // El nombre del archivo (sin extensión)
  columns: { header: string, accessor: string }[]; // Mapeo de columnas
}

export default function ExportButtons({ data, filename, columns }: Props) {

  // --- 1. LÓGICA DE EXCEL ---
  const handleExportExcel = () => {
    // a. Limpiamos los datos: solo incluimos las columnas que queremos
    const cleanedData = data.map(item => {
      const row: { [key: string]: any } = {};
      columns.forEach(col => {
        row[col.header] = item[col.accessor];
      });
      return row;
    });

    // b. Creamos la hoja de cálculo
    const worksheet = XLSX.utils.json_to_sheet(cleanedData);
    const workbook = XLSX.utils.book_new();
    XLSX.utils.book_append_sheet(workbook, worksheet, 'Datos');
    
    // c. Descargamos el archivo
    XLSX.writeFile(workbook, `${filename}.xlsx`);
  };

  // --- 2. LÓGICA DE PDF ---
  const handleExportPDF = () => {
    const doc = new jsPDF();
    
    // a. Definimos las cabeceras (ej. ['ID', 'Producto', 'Stock'])
    const head = [columns.map(col => col.header)];
    
    // b. Definimos el cuerpo (ej. [[1, 'Proteína', 5], [2, 'Camiseta', 3]])
    const body = data.map(item => {
      return columns.map(col => item[col.accessor]);
    });

    // c. Añadimos la tabla al documento
    autoTable(doc, {
      head: head,
      body: body,
      startY: 20, // Espacio desde arriba
      styles: {
        fontSize: 9,
        cellPadding: 2,
      },
      headStyles: {
        fillColor: [34, 139, 34] // Un color verde (puedes cambiarlo)
      }
    });

    // d. Descargamos el archivo
    doc.save(`${filename}.pdf`);
  };

  return (
    <div className="flex gap-3">
      <button
        onClick={handleExportExcel}
        className="flex items-center gap-2 bg-green-700 hover:bg-green-800 text-white font-semibold py-2 px-4 rounded-lg"
      >
        <Download size={16} />
        Exportar a Excel
      </button>
      <button
        onClick={handleExportPDF}
        className="flex items-center gap-2 bg-red-700 hover:bg-red-800 text-white font-semibold py-2 px-4 rounded-lg"
      >
        <Download size={16} />
        Exportar a PDF
      </button>
    </div>
  );
}   