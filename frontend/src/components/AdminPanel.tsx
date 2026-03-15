import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Package, Utensils, Store, ShieldCheck, ShieldAlert, History } from "lucide-react";
import { ProductList } from "./ProductList";
import { MenuList } from "./MenuList";
import { BranchList } from "./BranchList";
import AuditLogList from "./AuditLogList";
import { useTranslation } from "react-i18next";
import { useAuth } from "../AuthContext";

export function AdminPanel() {
  const { t } = useTranslation();
  const { isAdmin } = useAuth();

  if (!isAdmin) {
    return (
      <div className="flex flex-col items-center justify-center py-24 text-center space-y-6 animate-in fade-in zoom-in-95 duration-500">
        <div className="w-24 h-24 bg-destructive/10 rounded-full flex items-center justify-center border-4 border-destructive/20">
          <ShieldAlert className="h-12 w-12 text-destructive" />
        </div>
        <div className="space-y-2">
          <h2 className="text-3xl font-black uppercase italic tracking-tighter text-foreground">
            {t('common.accessDenied') || "Access Denied"}
          </h2>
          <p className="text-muted-foreground font-bold uppercase text-[10px] tracking-widest max-w-xs mx-auto leading-relaxed">
            {t('common.adminOnly') || "This section is reserved for system administrators only."}
          </p>
        </div>
      </div>
    );
  }

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
        <div className="flex justify-center mb-10 md:mb-16 sticky top-4 z-30 px-4">
          <TabsList className="flex h-auto p-1.5 md:p-2 bg-card/80 backdrop-blur-xl rounded-full border-2 border-primary/5 shadow-2xl shadow-primary/5 gap-1 md:gap-2 overflow-x-auto scrollbar-hide max-w-full sm:max-w-fit mx-auto">
            <TabsTrigger 
              data-testid="admin-tab-products" 
              value="products" 
              className="group px-5 md:px-8 py-2.5 md:py-3.5 rounded-full font-black uppercase text-[10px] md:text-[11px] tracking-widest transition-all data-[state=active]:bg-primary data-[state=active]:text-primary-foreground data-[state=active]:shadow-2xl data-[state=active]:shadow-primary/40 hover:bg-muted/80 gap-2 md:gap-3 min-w-max"
            >
              <div className="w-7 h-7 md:w-8 md:h-8 rounded-full bg-primary/5 flex items-center justify-center transition-colors group-data-[state=active]:bg-white/20">
                <Package className="w-3.5 h-3.5 md:w-4 md:h-4 transition-transform group-hover:scale-110" />
              </div>
              {t('admin.tabs.products')}
            </TabsTrigger>
            
            <TabsTrigger 
              data-testid="admin-tab-menus" 
              value="menus" 
              className="group px-5 md:px-8 py-2.5 md:py-3.5 rounded-full font-black uppercase text-[10px] md:text-[11px] tracking-widest transition-all data-[state=active]:bg-primary data-[state=active]:text-primary-foreground data-[state=active]:shadow-2xl data-[state=active]:shadow-primary/40 hover:bg-muted/80 gap-2 md:gap-3 min-w-max"
            >
              <div className="w-7 h-7 md:w-8 md:h-8 rounded-full bg-primary/5 flex items-center justify-center transition-colors group-data-[state=active]:bg-white/20">
                <Utensils className="w-3.5 h-3.5 md:w-4 md:h-4 transition-transform group-hover:scale-110" />
              </div>
              {t('admin.tabs.menus')}
            </TabsTrigger>
            
            <TabsTrigger 
              data-testid="admin-tab-branches" 
              value="branches" 
              className="group px-5 md:px-8 py-2.5 md:py-3.5 rounded-full font-black uppercase text-[10px] md:text-[11px] tracking-widest transition-all data-[state=active]:bg-primary data-[state=active]:text-primary-foreground data-[state=active]:shadow-2xl data-[state=active]:shadow-primary/40 hover:bg-muted/80 gap-2 md:gap-3 min-w-max"
            >
              <div className="w-7 h-7 md:w-8 md:h-8 rounded-full bg-primary/5 flex items-center justify-center transition-colors group-data-[state=active]:bg-white/20">
                <Store className="w-3.5 h-3.5 md:w-4 md:h-4 transition-transform group-hover:scale-110" />
              </div>
              {t('admin.tabs.branches')}
            </TabsTrigger>
            
            <TabsTrigger 
              data-testid="admin-tab-audit" 
              value="audit" 
              className="group px-5 md:px-8 py-2.5 md:py-3.5 rounded-full font-black uppercase text-[10px] md:text-[11px] tracking-widest transition-all data-[state=active]:bg-primary data-[state=active]:text-primary-foreground data-[state=active]:shadow-2xl data-[state=active]:shadow-primary/40 hover:bg-muted/80 gap-2 md:gap-3 min-w-max"
            >
              <div className="w-7 h-7 md:w-8 md:h-8 rounded-full bg-primary/5 flex items-center justify-center transition-colors group-data-[state=active]:bg-white/20">
                <History className="w-3.5 h-3.5 md:w-4 md:h-4 transition-transform group-hover:scale-110" />
              </div>
              {t('admin.tabs.audit')}
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
          <TabsContent value="audit" className="mt-0 outline-none animate-in fade-in zoom-in-95 duration-500">
            <AuditLogList />
          </TabsContent>
        </div>
      </Tabs>
    </div>
  );
}
