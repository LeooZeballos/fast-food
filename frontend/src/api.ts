import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:8080/api/v1",
});

export type BranchDTO = {
  id?: number;
  name: string;
  street: string;
  city: string;
}

export type ProductDTO = {
  id?: number;
  name: string;
  price: number;
  active: boolean;
  formattedPrice?: string;
}

export type MenuDTO = {
  id?: number;
  name: string;
  price: number;
  discountPercentage: number;
  productsList: string;
  active: boolean;
  formattedPrice?: string;
  formattedDiscount?: string;
}

export type FoodOrderDetailDTO = {
  id?: number;
  itemName: string;
  historicPrice: number;
  quantity: number;
  subtotal: number;
  formattedPrice?: string;
  formattedSubtotal?: string;
}

export type FoodOrderDTO = {
  id?: number;
  creationTimestamp: string;
  paymentTimestamp?: string;
  formattedState: string;
  branchName: string;
  foodOrderDetails: FoodOrderDetailDTO[];
  total: number;
  formattedCreationTimestamp?: string;
  formattedPaymentTimestamp?: string;
  formattedTotal?: string;
  formattedFoodOrderDetails?: string;
}

export type CreateOrderDTO = {
  branchId: number;
  items: {
    itemId: number;
    quantity: number;
  }[];
}

export const getBranches = async () => {
  const response = await api.get<BranchDTO[]>("/branches");
  return response.data;
};

export const createBranch = async (branch: BranchDTO) => {
  const response = await api.post<BranchDTO>("/branches", branch);
  return response.data;
};

export const deleteBranch = async (id: number) => {
  await api.delete(`/branches/${id}`);
};

export const getProducts = async () => {
  const response = await api.get<ProductDTO[]>("/products");
  return response.data;
};

export const createProduct = async (product: Partial<ProductDTO>) => {
  const response = await api.post<ProductDTO>("/products", product);
  return response.data;
};

export const deleteProduct = async (id: number) => {
  await api.delete(`/products/${id}`);
};

export const toggleProductStatus = async ({ id, active }: { id: number; active: boolean }) => {
  const action = active ? "disable" : "enable";
  const response = await api.patch<ProductDTO>(`/products/${id}/${action}`);
  return response.data;
};

export const getMenus = async () => {
  const response = await api.get<MenuDTO[]>("/menus");
  return response.data;
};

export const deleteMenu = async (id: number) => {
  await api.delete(`/menus/${id}`);
};

export const toggleMenuStatus = async ({ id, active }: { id: number; active: boolean }) => {
  const action = active ? "disable" : "enable";
  const response = await api.patch<MenuDTO>(`/menus/${id}/${action}`);
  return response.data;
};

export const getOrders = async (type?: string) => {
  const response = await api.get<FoodOrderDTO[]>("/orders", { params: { type } });
  return response.data;
};

export const startPreparation = async (id: number) => {
  const response = await api.post<FoodOrderDTO>(`/orders/${id}/start-preparation`);
  return response.data;
};

export const finishPreparation = async (id: number) => {
  const response = await api.post<FoodOrderDTO>(`/orders/${id}/finish-preparation`);
  return response.data;
};

export const confirmPayment = async (id: number) => {
  const response = await api.post<FoodOrderDTO>(`/orders/${id}/confirm-payment`);
  return response.data;
};

export const cancelOrder = async (id: number) => {
  const response = await api.post<FoodOrderDTO>(`/orders/${id}/cancel`);
  return response.data;
};

export const rejectOrder = async (id: number) => {
  const response = await api.post<FoodOrderDTO>(`/orders/${id}/reject`);
  return response.data;
};

export const createOrder = async (order: CreateOrderDTO) => {
  const response = await api.post<FoodOrderDTO>("/orders", order);
  return response.data;
};

export default api;
