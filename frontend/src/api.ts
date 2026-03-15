import axios from "axios";

const api = axios.create({
  baseURL: "http://localhost:4080/api/v1",
  withCredentials: true,
  xsrfCookieName: "XSRF-TOKEN",
  xsrfHeaderName: "X-XSRF-TOKEN",
});

// CSRF Fallback for non-GET requests
api.interceptors.request.use((config) => {
  if (config.method !== 'get' && !config.headers['X-XSRF-TOKEN']) {
    const xsrfCookie = document.cookie
      .split('; ')
      .find(row => row.startsWith('XSRF-TOKEN='));
    
    if (xsrfCookie) {
      config.headers['X-XSRF-TOKEN'] = xsrfCookie.split('=')[1];
    }
  }
  return config;
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
  nameEs?: string;
  price: number;
  icon?: string;
  imageUrl?: string;
  active: boolean;
  formattedPrice?: string;
}

export type MenuDTO = {
  id?: number;
  name: string;
  nameEs?: string;
  price: number;
  discountPercentage: number;
  productsList: string;
  icon?: string;
  imageUrl?: string;
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
  preparationStartTimestamp?: string;
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

export const updateBranch = async ({ id, branch }: { id: number; branch: BranchDTO }) => {
  const response = await api.put<BranchDTO>(`/branches/${id}`, branch);
  return response.data;
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

export const updateProduct = async ({ id, product }: { id: number; product: Partial<ProductDTO> }) => {
  const response = await api.put<ProductDTO>(`/products/${id}`, product);
  return response.data;
};

export const getMenus = async () => {
  const response = await api.get<MenuDTO[]>("/menus");
  return response.data;
};

export const createMenu = async (menu: Partial<MenuDTO>) => {
  const response = await api.post<MenuDTO>("/menus", menu);
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

export const updateMenu = async ({ id, menu }: { id: number; menu: Partial<MenuDTO> }) => {
  const response = await api.put<MenuDTO>(`/menus/${id}`, menu);
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

const authApi = axios.create({
  baseURL: "http://localhost:4080",
  withCredentials: true,
  xsrfCookieName: "XSRF-TOKEN",
  xsrfHeaderName: "X-XSRF-TOKEN",
});

export const login = async (username: string, password: string) => {
  const params = new URLSearchParams();
  params.append("username", username);
  params.append("password", password);
  
  // Spring Security's default /login expects form-data
  await authApi.post("/login", params, {
    headers: {
      "Content-Type": "application/x-www-form-urlencoded",
      "X-Requested-With": "XMLHttpRequest",
    },
  });
};

export type UserDTO = {
  name: string;
  branchId?: number | "none";
  roles?: string[];
}

export type InventoryDTO = {
  id: number;
  stockQuantity: number;
  isAvailable: boolean;
  item: {
    id: number;
    name: string;
  };
}

export type AuditLogDTO = {
  id: number;
  username: string;
  action: string;
  details: string;
  timestamp: string;
}

export const getInventoryByBranch = async (branchId: number) => {
  const response = await api.get<InventoryDTO[]>(`/inventory/branch/${branchId}`);
  return response.data;
};

export const getAuditLogs = async () => {
  const response = await api.get<AuditLogDTO[]>("/admin/audit-logs");
  return response.data;
};

export const logout = async () => {
  await authApi.post("/logout", null);
};

export const getMe = async () => {
  const response = await api.get<UserDTO>("/branches/me");
  return response.data;
};

// RFC 7807 Error Interceptor
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response && error.response.data) {
      const detail = error.response.data.detail || error.response.data.message;
      if (detail) {
        error.message = detail;
      }
      // If there are validation errors (RFC 7807 custom properties)
      if (error.response.data.errors) {
        const valErrors = error.response.data.errors;
        (error as any).validationErrors = valErrors;
        error.message = Object.values(valErrors).join(", ");
      }
    }
    return Promise.reject(error);
  }
);

export default api;
