import axios from "axios";

const paymentApi = axios.create({
  baseURL: "http://localhost:8085/api/v2/payments", // Ajusta al puerto de tu microservicio
});

export interface PaymentRequestDto {
  orderId: number;
  method: string;
  amount: number;
  currency: string;
  status?: string;
  transactionId?: string;
}

export const createPayment = async (data: PaymentRequestDto) => {
  const response = await paymentApi.post("/", data);
  return response.data;
};

export const getAllPayments = async () => {
  const response = await paymentApi.get("/");
  return response.data;
};
