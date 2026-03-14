import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getProducts, getMenus, getBranches, createOrder } from "@/api";
import type { ProductDTO, MenuDTO } from "@/api";
import { useState, useMemo } from "react";
import { useTranslation } from "react-i18next";
import { 
  ShoppingCart, 
  Plus, 
  Minus, 
  Trash2, 
  Receipt, 
  Store, 
  UtensilsCrossed, 
  Beef, 
  CupSoda, 
  CircleDashed, 
  Sparkles,
  Search,
  X,
  ClipboardList,
  Utensils
} from "lucide-react";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from "@/components/ui/card";
import { 
  Select, 
  SelectContent, 
  SelectItem, 
  SelectTrigger, 
  SelectValue 
} from "@/components/ui/select";
import { Input } from "@/components/ui/input";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { cn } from "@/lib/utils";
import { toast } from "sonner";

interface CartItem {
  itemId: number;
  type: "PRODUCT" | "MENU";
  name: string;
  nameEs?: string;
  price: number;
  quantity: number;
}

const ItemImage = ({ icon, type, className }: { icon?: string, type: "PRODUCT" | "MENU", className?: string }) => {
  const getImageUrl = () => {
    if (type === "MENU") {
      const images: Record<string, string> = {
        burger: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?q=80&w=800&auto=format&fit=crop",
        combo: "https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?q=80&w=800&auto=format&fit=crop",
        drink: "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?q=80&w=800&auto=format&fit=crop",
      };
      return images[icon || ""] || images.combo;
    } else {
      const images: Record<string, string> = {
        burger: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?q=80&w=800&auto=format&fit=crop",
        fries: "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?q=80&w=800&auto=format&fit=crop",
        drink: "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?q=80&w=800&auto=format&fit=crop",
        beer: "https://images.unsplash.com/photo-1535958636474-b021ee887b13?q=80&w=800&auto=format&fit=crop",
        shake: "https://images.unsplash.com/photo-1572490122747-3968b75cc699?q=80&w=800&auto=format&fit=crop",
        coffee: "https://images.unsplash.com/photo-1541167760496-162955ed8a9f?q=80&w=800&auto=format&fit=crop",
      };
      return images[icon || ""] || "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=800&auto=format&fit=crop";
    }
  };

  return (
    <div className={cn("relative overflow-hidden", className)}>
      <img 
        src={getImageUrl()} 
        alt="Item" 
        className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
      />
      <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent opacity-60" />
    </div>
  );
};

export function TakeOrder() {
  const queryClient = useQueryClient();
  const { t, i18n } = useTranslation();
  const [cart, setCart] = useState<CartItem[]>([]);
  const [selectedBranch, setSelectedBranch] = useState<string>("");
  const [searchQuery, setSearchQuery] = useState("");
  const [activeTab, setActiveTab] = useState("all");
  const [activeCategory, setActiveCategory] = useState("all");

  const categories = [
    { id: "all", label: t('takeOrder.categories.all'), icon: <UtensilsCrossed className="h-4 w-4" /> },
    { id: "burger", label: t('takeOrder.categories.burger'), icon: <Beef className="h-4 w-4" /> },
    { id: "drink", label: t('takeOrder.categories.drink'), icon: <CupSoda className="h-4 w-4" /> },
    { id: "sides", label: t('takeOrder.categories.sides'), icon: <CircleDashed className="h-4 w-4" /> },
    { id: "combo", label: t('takeOrder.categories.combo'), icon: <Sparkles className="h-4 w-4" /> },
  ];

  const { data: products, isLoading: loadingProducts, error: errorProducts } = useQuery({ queryKey: ["products"], queryFn: getProducts });
  const { data: menus, isLoading: loadingMenus, error: errorMenus } = useQuery({ queryKey: ["menus"], queryFn: getMenus });
  const { data: branches } = useQuery({ queryKey: ["branches"], queryFn: getBranches });

  const activeProducts = useMemo(() => products?.filter(p => p.active) || [], [products]);
  const activeMenus = useMemo(() => menus?.filter(m => m.active) || [], [menus]);

  const filteredItems = useMemo(() => {
    const query = searchQuery.toLowerCase();
    let items: ((ProductDTO | MenuDTO) & { type: "PRODUCT" | "MENU" })[] = [
      ...activeProducts.map(p => ({ ...p, type: "PRODUCT" as const })),
      ...activeMenus.map(m => ({ ...m, type: "MENU" as const }))
    ];

    if (activeTab === "products") items = items.filter(i => i.type === "PRODUCT");
    if (activeTab === "menus") items = items.filter(i => i.type === "MENU");

    if (activeCategory !== "all") {
      if (activeCategory === "sides") {
        items = items.filter(i => i.icon === "fries");
      } else {
        items = items.filter(i => i.icon === activeCategory || (activeCategory === "combo" && i.type === "MENU"));
      }
    }

    if (query) {
      items = items.filter(i => i.name.toLowerCase().includes(query) || (i.nameEs && i.nameEs.toLowerCase().includes(query)));
    }

    return items;
  }, [activeProducts, activeMenus, searchQuery, activeTab, activeCategory]);

  const addToCart = (item: ProductDTO | MenuDTO, type: "PRODUCT" | "MENU") => {
    setCart(prev => {
      const existing = prev.find(i => i.itemId === item.id && i.type === type);
      if (existing) {
        return prev.map(i => i.itemId === item.id && i.type === type ? { ...i, quantity: i.quantity + 1 } : i);
      }
      return [...prev, { 
        itemId: item.id!, 
        type, 
        name: item.name,
        nameEs: item.nameEs,
        price: item.price, 
        quantity: 1 
      }];
    });
  };

  const removeFromCart = (itemId: number, type: "PRODUCT" | "MENU") => {
    setCart(prev => prev.filter(i => !(i.itemId === itemId && i.type === type)));
  };

  const updateQuantity = (itemId: number, type: "PRODUCT" | "MENU", delta: number) => {
    setCart(prev => prev.map(i => {
      if (i.itemId === itemId && i.type === type) {
        const newQty = Math.max(1, i.quantity + delta);
        return { ...i, quantity: newQty };
      }
      return i;
    }));
  };

  const clearCart = () => window.confirm(t('common.clearConfirm')) && setCart([]);
  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  
  const formatPrice = (amount: number) => {
    const locale = i18n.language === 'es' ? 'es-ES' : 'en-US';
    const currency = i18n.language === 'es' ? 'EUR' : 'USD';
    return new Intl.NumberFormat(locale, { style: 'currency', currency }).format(amount);
  };

  const createOrderMutation = useMutation({
    mutationFn: createOrder,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["orders"] });
      setCart([]);
      toast.success(t('takeOrder.cart.success'), {
        description: t('takeOrder.cart.successDesc'),
      });
    },
    onError: (error: any) => {
      toast.error(t('takeOrder.cart.error'), {
        description: error.message || t('common.error'),
      });
    }
  });

  const handleCheckout = () => {
    if (!selectedBranch) {
      toast.warning(t('takeOrder.cart.missingBranch'), {
        description: t('takeOrder.cart.missingBranchDesc'),
      });
      return;
    }
    if (cart.length === 0) {
      toast.warning(t('takeOrder.cart.emptyCart'), {
        description: t('takeOrder.cart.emptyCartDesc'),
      });
      return;
    }
    createOrderMutation.mutate({ branchId: parseInt(selectedBranch), items: cart.map(i => ({ itemId: i.itemId, quantity: i.quantity })) });
  };

  if (loadingProducts || loadingMenus) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mt-8 animate-pulse">
        {[1, 2, 3, 4, 5, 6, 7, 8].map(i => (
          <div key={i} className="h-80 bg-card border-2 rounded-[2.5rem]" />
        ))}
      </div>
    );
  }

  return (
    <div className="flex flex-col gap-8 mt-4 animate-in fade-in duration-700 pb-20">
      <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 bg-card p-8 rounded-[2.5rem] border-2 shadow-sm relative overflow-hidden">
        <div className="absolute top-0 left-0 w-2 h-full bg-primary" />
        <div className="space-y-1">
          <h2 className="text-4xl font-black italic tracking-tighter uppercase text-primary flex items-center gap-4">
            <UtensilsCrossed className="h-10 w-10 text-secondary" /> {t('takeOrder.posTerminal')}
          </h2>
          <p className="text-muted-foreground font-bold uppercase text-[10px] tracking-[0.2em]">{t('takeOrder.kitchenManagement')}</p>
        </div>
        <div className="w-full lg:w-80">
          <label className="text-[10px] font-black uppercase tracking-widest text-primary/40 mb-2 block ml-1">{t('takeOrder.branch')}</label>
          <Select value={selectedBranch} onValueChange={setSelectedBranch}>
            <SelectTrigger data-testid="branch-select" className="h-14 border-2 bg-muted/50 focus:ring-primary/10 rounded-2xl transition-all px-6 w-full">
              <div className="flex items-center gap-3 w-full">
                <Store className="h-5 w-5 text-secondary shrink-0" />
                <SelectValue placeholder={t('takeOrder.selectLocation')} />
              </div>
            </SelectTrigger>
            <SelectContent className="rounded-2xl">
              {branches?.map(b => (
                <SelectItem key={b.id} value={b.id?.toString() || ""} className="py-3 pl-4 pr-10">
                  <div className="flex items-center gap-2">
                    <span className="font-black text-sm uppercase tracking-tight">{b.name}</span>
                    <span className="text-[10px] text-muted-foreground uppercase font-bold ml-1">— {b.city}</span>
                  </div>
                </SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>

      <div className="grid grid-cols-1 xl:grid-cols-12 gap-8 items-start">
        <div className="xl:col-span-8 space-y-8">
          <div className="flex flex-col gap-6">
            <div className="flex flex-col md:flex-row gap-6 items-center justify-between bg-card/50 p-4 rounded-3xl border-2 border-dashed border-border">
              <div className="relative w-full md:w-96 group">
                <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground/60 group-focus-within:text-primary transition-colors" />
                <Input 
                  placeholder={t('common.search')}
                  className="pl-12 h-12 border-2 bg-card rounded-2xl focus-visible:ring-primary/10 text-lg font-medium"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
                {searchQuery && <button onClick={() => setSearchQuery("")} className="absolute right-4 top-1/2 -translate-y-1/2 text-muted-foreground/60 hover:text-primary"><X className="h-5 w-5" /></button>}
              </div>

              <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full md:w-auto">
                <TabsList className="h-12 p-1.5 bg-muted rounded-2xl">
                  <TabsTrigger value="all" className="px-6 rounded-xl font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">{t('takeOrder.tabs.all')}</TabsTrigger>
                  <TabsTrigger value="products" className="px-6 rounded-xl font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">{t('takeOrder.tabs.products')}</TabsTrigger>
                  <TabsTrigger value="menus" className="px-6 rounded-xl font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">{t('takeOrder.tabs.menus')}</TabsTrigger>
                </TabsList>
              </Tabs>
            </div>

            <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
              {categories.map(cat => (
                <Button
                  key={cat.id}
                  variant={activeCategory === cat.id ? "default" : "outline"}
                  onClick={() => setActiveCategory(cat.id)}
                  className={cn(
                    "h-14 px-6 rounded-2xl flex items-center gap-3 transition-all border-2 shrink-0",
                    activeCategory === cat.id ? "bg-secondary text-primary border-secondary" : "bg-card text-muted-foreground border-border hover:border-primary/20 hover:text-primary"
                  )}
                >
                  <span className={cn(activeCategory === cat.id ? "text-primary" : "text-muted-foreground/60")}>{cat.icon}</span>
                  <span className="font-black uppercase text-[10px] tracking-widest italic">{cat.label}</span>
                </Button>
              ))}
            </div>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredItems.length > 0 ? (
              filteredItems.map(item => (
                <Card 
                  key={`${item.type}-${item.id}`} 
                  data-testid="product-card"
                  className="group border-2 bg-card rounded-[2.5rem] overflow-hidden shadow-sm hover:shadow-2xl hover:scale-[1.02] transition-all cursor-pointer flex flex-col"
                  onClick={() => addToCart(item, item.type)}
                >
                  <ItemImage icon={item.icon} type={item.type} className="h-48 w-full" />
                  <CardContent className="p-6 space-y-4 flex-grow flex flex-col">
                    <div className="space-y-1 flex-grow">
                      <div className="flex justify-between items-center mb-1">
                        <span className="text-[9px] font-black uppercase tracking-[0.2em] text-muted-foreground/60">{item.type}</span>
                        {item.type === "MENU" && <span className="text-[10px] font-black text-secondary italic tracking-tighter uppercase">{t('takeOrder.cart.saveDiscount')} {(item as MenuDTO).formattedDiscount}</span>}
                      </div>
                      <h3 className="font-black text-foreground group-hover:text-primary transition-colors text-lg leading-tight uppercase tracking-tighter italic">
                        {i18n.language === 'es' && item.nameEs ? item.nameEs : item.name}
                      </h3>
                      {item.type === "MENU" && (
                        <p className="text-[10px] text-muted-foreground font-bold uppercase leading-relaxed line-clamp-2 mt-2">
                          {(item as MenuDTO).productsList}
                        </p>
                      )}
                    </div>
                    <div className="flex justify-between items-center pt-2 mt-auto">
                      <p className="font-black text-primary text-2xl tracking-tighter">{item.formattedPrice}</p>
                      <div className="h-10 w-10 rounded-xl bg-primary flex items-center justify-center text-white shadow-lg shadow-primary/20 group-hover:bg-secondary group-hover:text-primary transition-all">
                        <Plus className="h-6 w-6" />
                      </div>
                    </div>
                  </CardContent>
                </Card>
              ))
            ) : (
              <div className="col-span-full py-24 bg-card rounded-[3rem] border-2 border-dashed border-border text-center">
                <Utensils className="h-16 w-12 text-muted-foreground/20 mx-auto mb-6" />
                <h3 className="text-2xl font-black uppercase tracking-tighter italic text-foreground">{t('common.noResults')}</h3>
                <p className="text-muted-foreground font-bold uppercase text-[10px] tracking-widest mt-2">{t('common.noResultsAdjust')}</p>
              </div>
            )}
          </div>
        </div>

        <div className="xl:col-span-4 lg:sticky lg:top-8">
          <Card className="border-2 shadow-2xl rounded-[2.5rem] overflow-hidden flex flex-col max-h-[calc(100vh-6rem)] py-0 bg-card select-none relative">
            <CardHeader className="bg-primary text-primary-foreground p-8 shrink-0 relative overflow-hidden">
              <div className="absolute top-0 right-0 w-32 h-32 bg-secondary/10 rounded-full -mr-16 -mt-16 blur-3xl" />
              <div className="flex justify-between items-center relative z-10">
                <CardTitle className="flex items-center gap-4 uppercase font-black italic tracking-tighter text-3xl">
                  <Receipt className="h-8 w-8 text-secondary" /> {t('takeOrder.cart.ticket')}
                </CardTitle>
                <div className="text-right">
                  <p className="text-[10px] font-black text-primary-foreground/60 uppercase tracking-widest">{t('takeOrder.cart.orderType')}</p>
                  <p className="text-xs font-black text-secondary uppercase tracking-widest">{t('takeOrder.cart.dineIn')}</p>
                </div>
              </div>
            </CardHeader>
            
            <CardContent className="flex-grow overflow-y-auto p-0 scrollbar-hide bg-card">
              <div className="px-8 pt-8 pb-4 space-y-1">
                {cart.length > 0 ? (
                  cart.map(item => (
                    <div key={`${item.type}-${item.itemId}`} className="group flex items-center justify-between py-4 border-b border-border last:border-0 hover:bg-muted/50 -mx-2 px-2 rounded-xl transition-colors">
                      <div className="flex-grow min-w-0">
                        <div className="flex items-center gap-2 mb-0.5">
                          <span className="text-[8px] font-black bg-primary/5 text-primary px-1.5 py-0.5 rounded uppercase tracking-tighter">{item.type[0]}</span>
                          <p className="font-black text-foreground uppercase tracking-tighter italic truncate text-sm">
                            {i18n.language === 'es' && item.nameEs ? item.nameEs : item.name}
                          </p>
                        </div>
                        <p className="text-[10px] font-black text-secondary tracking-tighter">
                          {item.quantity} × {formatPrice(item.price)}
                        </p>
                      </div>
                      <div className="flex items-center gap-3 ml-4 shrink-0">
                        <div className="flex items-center bg-muted rounded-lg p-1 border border-border">
                          <button 
                            onClick={(e) => { e.stopPropagation(); updateQuantity(item.itemId, item.type, -1); }}
                            className="h-6 w-6 flex items-center justify-center hover:bg-card rounded-md transition-colors text-muted-foreground"
                          >
                            <Minus className="h-3 w-3" />
                          </button>
                          <span className="w-6 text-center font-black text-xs text-foreground">{item.quantity}</span>
                          <button 
                            onClick={(e) => { e.stopPropagation(); updateQuantity(item.itemId, item.type, 1); }}
                            className="h-6 w-6 flex items-center justify-center hover:bg-card rounded-md transition-colors text-muted-foreground"
                          >
                            <Plus className="h-3 w-3" />
                          </button>
                        </div>
                        <button 
                          onClick={(e) => { e.stopPropagation(); removeFromCart(item.itemId, item.type); }}
                          className="h-8 w-8 flex items-center justify-center text-muted-foreground/60 hover:text-destructive hover:bg-destructive/5 rounded-lg transition-all"
                        >
                          <Trash2 className="h-4 w-4" />
                        </button>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="py-20 text-center space-y-4 opacity-20">
                    <ShoppingCart className="h-12 w-12 mx-auto" />
                    <p className="text-[10px] font-black uppercase tracking-[0.3em]">{t('takeOrder.cart.emptyCart')}</p>
                  </div>
                )}
              </div>
            </CardContent>

            <CardFooter className="flex flex-col p-8 pt-6 gap-6 bg-muted/50 border-t-2 border-border shrink-0">
              <div className="w-full space-y-3">
                <div className="flex justify-between items-end">
                  <span className="text-[10px] font-black text-muted-foreground uppercase tracking-widest">{t('takeOrder.cart.totalDue')}</span>
                  <span className="text-4xl font-black text-primary tracking-tighter leading-none">{formatPrice(total)}</span>
                </div>
              </div>
              
              <div className="flex gap-3 w-full">
                <Button 
                  variant="outline" 
                  className="flex-1 h-16 rounded-2xl border-2 font-black uppercase italic tracking-tighter text-muted-foreground hover:text-destructive hover:bg-destructive/5 transition-all text-xs"
                  onClick={clearCart}
                  disabled={cart.length === 0}
                >
                  {t('takeOrder.cart.voidTicket')}
                </Button>
                <Button 
                  data-testid="place-order-button"
                  className="flex-[2] h-16 rounded-2xl font-black uppercase italic tracking-tighter text-lg shadow-2xl shadow-primary/30 hover:scale-[1.02] active:scale-[0.98] transition-all bg-primary group relative overflow-hidden"
                  onClick={handleCheckout}
                  disabled={createOrderMutation.isPending || cart.length === 0}
                >
                  <span className="relative z-10 flex items-center justify-center gap-3">
                    {createOrderMutation.isPending ? t('takeOrder.cart.sending') : t('takeOrder.cart.placeOrder')}
                  </span>
                  <div className="absolute inset-0 bg-card/10 translate-y-full group-hover:translate-y-0 transition-transform duration-500" />
                </Button>
              </div>
            </CardFooter>
          </Card>
        </div>
      </div>
    </div>
  );
}
