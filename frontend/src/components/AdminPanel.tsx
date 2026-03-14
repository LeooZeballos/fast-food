import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Package, Utensils, Store, ShieldCheck } from "lucide-react";
import { ProductList } from "./ProductList";
import { MenuList } from "./MenuList";
import { BranchList } from "./BranchList";
import { useTranslation } from "react-i18next";

export function AdminPanel() {
  const { t } = useTranslation();
  return (
    <div className="space-y-8 md:space-y-12 animate-in fade-in slide-in-from-bottom-4 duration-700">
      <div className="flex flex-col gap-3 text-center bg-card p-6 md:p-10 rounded-[2rem] md:rounded-[2.5rem] border-2 shadow-sm relative overflow-hidden">
        <div className="absolute top-0 left-0 w-full h-2 bg-secondary" />
        <h1 className="text-3xl md:text-5xl font-black tracking-tighter text-primary uppercase italic flex items-center justify-center gap-3 md:gap-4">
          <ShieldCheck className="h-8 w-8 md:h-12 md:w-12 text-secondary" />
          {t('admin.panel')}
        </h1>
        <p className="text-muted-foreground font-bold uppercase text-[9px] md:text-[10px] tracking-[0.2em]">{t('admin.management')}</p>
      </div>

      <Tabs defaultValue="products" className="w-full">
        <div className="flex justify-center mb-8 md:mb-12">
          <TabsList className="grid grid-cols-3 w-full max-w-xl h-12 md:h-14 bg-muted p-1.5 md:p-2 rounded-2xl md:rounded-[2rem] border-2 border-white shadow-inner">
            <TabsTrigger data-testid="admin-tab-products" value="products" className="rounded-xl md:rounded-[1.5rem] font-black uppercase text-[9px] md:text-[11px] tracking-widest transition-all data-[state=active]:bg-primary data-[state=active]:text-white data-[state=active]:shadow-xl">
              <Package className="w-3 h-3 md:w-4 md:h-4 mr-1.5 md:mr-2" />
              {t('admin.tabs.products')}
            </TabsTrigger>
            <TabsTrigger data-testid="admin-tab-menus" value="menus" className="rounded-xl md:rounded-[1.5rem] font-black uppercase text-[9px] md:text-[11px] tracking-widest transition-all data-[state=active]:bg-primary data-[state=active]:text-white data-[state=active]:shadow-xl">
              <Utensils className="w-3 h-3 md:w-4 md:h-4 mr-1.5 md:mr-2" />
              {t('admin.tabs.menus')}
            </TabsTrigger>
            <TabsTrigger data-testid="admin-tab-branches" value="branches" className="rounded-xl md:rounded-[1.5rem] font-black uppercase text-[9px] md:text-[11px] tracking-widest transition-all data-[state=active]:bg-primary data-[state=active]:text-white data-[state=active]:shadow-xl">
              <Store className="w-3 h-3 md:w-4 md:h-4 mr-1.5 md:mr-2" />
              {t('admin.tabs.branches')}
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
