import axios from "axios";

const fileApi = axios.create({
  baseURL: "http://localhost:8092/api/v2/files"
});


export async function uploadImage(file: File): Promise<string> {
  const formData = new FormData();
  formData.append("file", file);

  try {
    const { data: uploadedFile } = await fileApi.post(
      "/upload",
      formData,
      {
        headers: {
          "Content-Type": "multipart/form-data",
        },
      }
    );

    return `http://localhost:8092/api/v2/files/${uploadedFile.filename}`;
    
  } catch (error: any) {
    console.error("Error al subir la imagen:", error.response?.data || error.message);
    throw new Error("Error al subir la imagen");
  }
}
