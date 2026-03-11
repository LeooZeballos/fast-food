import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Package, Utensils, Store, ShieldCheck } from "lucide-react";
import { ProductList } from "./ProductList";
import { MenuList } from "./MenuList";
import { BranchList } from "./BranchList";

export function AdminPanel() {
  return (
    <div className="space-y-12 animate-in fade-in slide-in-from-bottom-4 duration-700">
      <div className="flex flex-col gap-3 text-center bg-white p-10 rounded-[2.5rem] border-2 shadow-sm relative overflow-hidden">
        <div className="absolute top-0 left-0 w-full h-2 bg-secondary" />
        <h1 className="text-5xl font-black tracking-tighter text-primary uppercase italic flex items-center justify-center gap-4">
          <ShieldCheck className="h-12 w-12 text-secondary" />
          Admin Panel
        </h1>
        <p className="text-slate-400 font-bold uppercase text-[10px] tracking-[0.2em]">Management & Configuration System</p>
      </div>

      <Tabs defaultValue="products" className="w-full">
        <div className="flex justify-center mb-12">
          <TabsList className="grid grid-cols-3 w-full max-w-xl h-14 bg-slate-200/50 p-2 rounded-[2rem] border-2 border-white shadow-inner">
            <TabsTrigger value="products" className="rounded-[1.5rem] font-black uppercase text-[11px] tracking-widest transition-all data-[state=active]:bg-primary data-[state=active]:text-white data-[state=active]:shadow-xl">
              <Package className="w-4 h-4 mr-2" />
              Products
            </TabsTrigger>
            <TabsTrigger value="menus" className="rounded-[1.5rem] font-black uppercase text-[11px] tracking-widest transition-all data-[state=active]:bg-primary data-[state=active]:text-white data-[state=active]:shadow-xl">
              <Utensils className="w-4 h-4 mr-2" />
              Menus
            </TabsTrigger>
            <TabsTrigger value="branches" className="rounded-[1.5rem] font-black uppercase text-[11px] tracking-widest transition-all data-[state=active]:bg-primary data-[state=active]:text-white data-[state=active]:shadow-xl">
              <Store className="w-4 h-4 mr-2" />
              Branches
            </TabsTrigger>
          </TabsList>
        </div>

        <div className="w-full">
          <TabsContent value="products" className="mt-0 outline-none animate-in fade-in zoom-in-95 duration-500">
            <ProductList />
          </TabsContent>
          <TabsContent value="menus" className="mt-0 outline-none animate-in fade-in zoom-in-95 duration-500">
            <MenuList />
          </TabsContent>
          <TabsContent value="branches" className="mt-0 outline-none animate-in fade-in zoom-in-95 duration-500">
            <BranchList />
          </TabsContent>
        </div>
      </Tabs>
    </div>
  );
}
