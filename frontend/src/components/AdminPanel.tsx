import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Package, Utensils, Store } from "lucide-react";
import { ProductList } from "./ProductList";
import { MenuList } from "./MenuList";
import { BranchList } from "./BranchList";

export function AdminPanel() {
  return (
    <div className="space-y-8 animate-in fade-in duration-500">
      <div className="flex flex-col gap-2 text-center">
        <h1 className="text-4xl font-black tracking-tighter text-slate-900 uppercase italic">Admin Panel</h1>
        <p className="text-slate-500 font-medium">Manage your products, menus, and branches.</p>
      </div>

      <Tabs defaultValue="products" className="w-full">
        <div className="flex justify-center mb-8">
          <TabsList className="grid grid-cols-3 w-full max-w-md h-12 bg-slate-200/50 p-1.5 rounded-2xl">
            <TabsTrigger value="products" className="rounded-xl font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">
              <Package className="w-4 h-4 mr-2" />
              Products
            </TabsTrigger>
            <TabsTrigger value="menus" className="rounded-xl font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">
              <Utensils className="w-4 h-4 mr-2" />
              Menus
            </TabsTrigger>
            <TabsTrigger value="branches" className="rounded-xl font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">
              <Store className="w-4 h-4 mr-2" />
              Branches
            </TabsTrigger>
          </TabsList>
        </div>

        <div className="max-w-4xl mx-auto">
          <TabsContent value="products" className="mt-0 outline-none">
            <ProductList />
          </TabsContent>
          <TabsContent value="menus" className="mt-0 outline-none">
            <MenuList />
          </TabsContent>
          <TabsContent value="branches" className="mt-0 outline-none">
            <BranchList />
          </TabsContent>
        </div>
      </Tabs>
    </div>
  );
}
