const API_URL_STORAGE = "http://localhost:8083/files/upload";

export async function uploadImage(file: File): Promise<string> {
  const formData = new FormData();
  formData.append("file", file);

  const res = await fetch(API_URL_STORAGE, {
    method: "POST",
    body: formData,
  });

  if (!res.ok) {
    
    const errorBody = await res.text();
    console.error("Respuesta de error del storage:", errorBody);
    throw new Error(`Error al subir la imagen: ${res.statusText}`);
  }

  const uploadedFile = await res.json();
  

  return `http://localhost:8083/files/${uploadedFile.filename}`;
}