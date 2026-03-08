import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { BranchList } from "./components/BranchList";
import { ProductList } from "./components/ProductList";
import { MenuList } from "./components/MenuList";
import { OrderList } from "./components/OrderList";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";

const queryClient = new QueryClient();

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <div className="min-h-screen bg-slate-50 p-8">
        <header className="max-w-5xl mx-auto mb-8">
          <h1 className="text-4xl font-extrabold tracking-tight text-slate-900">
            FastFood Admin
          </h1>
          <p className="text-slate-500 mt-2">
            Modern Decoupled Architecture (Vite + React + Spring Boot)
          </p>
        </header>
        
        <main className="max-w-5xl mx-auto">
          <Tabs defaultValue="orders" className="w-full">
            <TabsList className="grid w-full grid-cols-4">
              <TabsTrigger value="orders">Orders</TabsTrigger>
              <TabsTrigger value="branches">Branches</TabsTrigger>
              <TabsTrigger value="products">Products</TabsTrigger>
              <TabsTrigger value="menus">Menus</TabsTrigger>
            </TabsList>
            <TabsContent value="orders">
              <OrderList />
            </TabsContent>
            <TabsContent value="branches">
              <BranchList />
            </TabsContent>
            <TabsContent value="products">
              <ProductList />
            </TabsContent>
            <TabsContent value="menus">
              <MenuList />
            </TabsContent>
          </Tabs>
        </main>
      </div>
    </QueryClientProvider>
  );
}

export default App;
