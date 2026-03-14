import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getProducts, getMenus, getBranches, createOrder, getInventoryByBranch } from "@/api";
import type { ProductDTO, MenuDTO, InventoryDTO } from "@/api";
import { useState, useMemo, useEffect } from "react";
import { 
  Plus, 
  Minus, 
  Trash2, 
  Receipt, 
  ShoppingCart,
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
import { Badge } from "@/components/ui/badge";
import { cn } from "@/lib/utils";
import { toast } from "sonner";
import { useTranslation } from "react-i18next";

interface CartItem {
  itemId: number;
  type: "PRODUCT" | "MENU";
  name: string;
  nameEs?: string;
  price: number;
  quantity: number;
}

const ItemImage = ({ imageUrl, className }: { id?: number, icon?: string, imageUrl?: string, type: "PRODUCT" | "MENU", className?: string }) => {
  const fallbackImage = "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=800&auto=format&fit=crop";

  return (
    <div className={cn("relative overflow-hidden", className)}>
      <img 
        src={imageUrl || fallbackImage} 
        alt="Item" 
        className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110"
      />
      <div className="absolute inset-0 bg-gradient-to-t from-black/60 via-transparent to-transparent opacity-60" />
    </div>
  );
};

export function TakeOrder() {
  const { t, i18n } = useTranslation();
  const queryClient = useQueryClient();
  const [selectedBranch, setSelectedBranch] = useState<string>("");
  const [activeTab, setActiveTab] = useState<"all" | "products" | "menus">("all");
  const [activeCategory, setActiveCategory] = useState<string>("all");
  const [searchQuery, setSearchQuery] = useState("");
  const [cart, setCart] = useState<CartItem[]>([]);

  // Get initial branch from localStorage if available
  const branchId = localStorage.getItem("branchId");
  useEffect(() => {
    if (branchId && !selectedBranch) {
      setSelectedBranch(branchId.toString());
    }
  }, [branchId, selectedBranch]);

  const categories = [
    { id: "all", label: t('takeOrder.categories.all'), icon: <UtensilsCrossed className="h-4 w-4" /> },
    { id: "burger", label: t('takeOrder.categories.burger'), icon: <Beef className="h-4 w-4" /> },
    { id: "drink", label: t('takeOrder.categories.drink'), icon: <CupSoda className="h-4 w-4" /> },
    { id: "sides", label: t('takeOrder.categories.sides'), icon: <CircleDashed className="h-4 w-4" /> },
    { id: "combo", label: t('takeOrder.categories.combo'), icon: <Sparkles className="h-4 w-4" /> },
  ];

  const { data: products, isLoading: loadingProducts } = useQuery({ queryKey: ["products"], queryFn: getProducts });
  const { data: menus, isLoading: loadingMenus } = useQuery({ queryKey: ["menus"], queryFn: getMenus });
  const { data: branches } = useQuery({ queryKey: ["branches"], queryFn: getBranches });

  const { data: inventory, isLoading: loadingInventory } = useQuery({
    queryKey: ["inventory", selectedBranch],
    queryFn: () => getInventoryByBranch(parseInt(selectedBranch)),
    enabled: !!selectedBranch,
  });

  const availabilityMap = useMemo(() => {
    const map = new Map<string, boolean>();
    console.log("Building Availability Map. Inventory Size:", inventory?.length);
    inventory?.forEach((item: InventoryDTO) => {
      map.set(`${item.item.id}`, item.stockQuantity > 0 && item.isAvailable);
    });
    return map;
  }, [inventory]);

  const isItemAvailable = (itemId: number, _type: "PRODUCT" | "MENU") => {
    if (!selectedBranch || loadingInventory) return true;
    
    if (inventory && inventory.length > 0) {
      const available = availabilityMap.get(`${itemId}`) ?? true;
      if (!available) console.log(`Item ${itemId} (${_type}) is OUT OF STOCK`);
      return available;
    }
    
    return true;
  };

  const activeProducts = useMemo(() => products?.filter(p => p.active) || [], [products]);
  const activeMenus = useMemo(() => menus?.filter(m => m.active) || [], [menus]);

  const filteredItems = useMemo(() => {
    const query = searchQuery.toLowerCase();
    let items: ((ProductDTO | MenuDTO) & { type: "PRODUCT" | "MENU" })[] = [
      ...activeProducts.map(p => ({ ...p, type: "PRODUCT" as const })),
      ...activeMenus.map(m => ({ ...m, type: "MENU" as const }))
    ];

    if (query) {
      items = items.filter(i => 
        i.name.toLowerCase().includes(query) || 
        (i.nameEs && i.nameEs.toLowerCase().includes(query))
      );
    }

    if (activeCategory !== "all") {
      if (activeCategory === "sides") {
        items = items.filter(i => i.icon === "fries" || i.icon === "sides");
      } else if (activeCategory === "drink") {
        items = items.filter(i => ["drink", "beer", "shake", "coffee"].includes(i.icon || ""));
      } else {
        items = items.filter(i => i.icon === activeCategory || (activeCategory === "combo" && i.type === "MENU"));
      }
    }

    return items;
  }, [activeProducts, activeMenus, searchQuery, activeCategory]);

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

  const updateQuantity = (itemId: number, type: "PRODUCT" | "MENU", delta: number) => {
    setCart(prev => prev.map(i => {
      if (i.itemId === itemId && i.type === type) {
        const newQty = Math.max(1, i.quantity + delta);
        return { ...i, quantity: newQty };
      }
      return i;
    }));
  };

  const removeFromCart = (itemId: number, type: "PRODUCT" | "MENU") => {
    setCart(prev => prev.filter(i => !(i.itemId === itemId && i.type === type)));
  };

  const clearCart = () => setCart([]);

  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);

  const formatPrice = (p: number) => new Intl.NumberFormat(i18n.language === 'es' ? 'es-ES' : 'en-US', {
    style: 'currency',
    currency: 'USD',
  }).format(p);

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
            <SelectContent className="rounded-2xl border-2">
              {branches?.map(b => (
                <SelectItem key={b.id} value={b.id?.toString() || ""}>{b.name}</SelectItem>
              ))}
            </SelectContent>
          </Select>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
        <div className="lg:col-span-8 space-y-8">
          <div className="bg-card p-6 rounded-[2.5rem] border-2 shadow-sm space-y-6">
            <div className="flex flex-col md:flex-row gap-4 items-center">
              <div className="relative flex-grow w-full group">
                <Search className="absolute left-5 top-1/2 -translate-y-1/2 h-5 w-5 text-muted-foreground group-focus-within:text-primary transition-colors" />
                <Input 
                  placeholder={t('common.search')} 
                  className="h-14 pl-14 pr-6 rounded-2xl border-2 bg-muted/50 focus-visible:ring-primary/10 text-lg font-medium transition-all"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
                {searchQuery && (
                  <button 
                    onClick={() => setSearchQuery("")}
                    className="absolute right-5 top-1/2 -translate-y-1/2 h-6 w-6 flex items-center justify-center bg-muted rounded-lg text-muted-foreground hover:text-primary transition-colors"
                  >
                    <X className="h-4 w-4" />
                  </button>
                )}
              </div>
              <Tabs value={activeTab} onValueChange={(v) => setActiveTab(v as any)} className="w-full md:w-auto">
                <TabsList className="h-14 bg-muted p-1.5 rounded-2xl border-2 w-full">
                  <TabsTrigger value="all" className="rounded-xl px-6 font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">{t('common.all')}</TabsTrigger>
                  <TabsTrigger value="products" className="rounded-xl px-6 font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">{t('takeOrder.tabs.products')}</TabsTrigger>
                  <TabsTrigger value="menus" className="rounded-xl px-6 font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">{t('takeOrder.tabs.menus')}</TabsTrigger>
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

          <div className="space-y-12">
            {/* Menus Section */}
            {(activeTab === "all" || activeTab === "menus") && filteredItems.filter(i => i.type === "MENU").length > 0 && (
              <div className="space-y-6">
                <div className="flex items-center gap-4">
                  <div className="h-px flex-grow bg-border" />
                  <h3 className="text-xl font-black uppercase italic tracking-tighter text-secondary flex items-center gap-2">
                    <Sparkles className="h-5 w-5" /> {t('takeOrder.tabs.menus')}
                  </h3>
                  <div className="h-px flex-grow bg-border" />
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-2 xl:grid-cols-3 gap-8">
                  {filteredItems.filter(i => i.type === "MENU").map(item => {
                    const isAvailable = item.id ? isItemAvailable(item.id, item.type) : true;
                    return (
                      <Card 
                        key={`${item.type}-${item.id}`} 
                        data-testid="product-card"
                        className={cn(
                          "group border-4 bg-card rounded-[3rem] overflow-hidden shadow-md hover:shadow-2xl transition-all flex flex-col relative h-full p-0 border-secondary/20 hover:border-secondary",
                          !isAvailable ? "opacity-60 grayscale cursor-not-allowed" : "hover:scale-[1.02] cursor-pointer"
                        )}
                        onClick={() => isAvailable && addToCart(item, item.type)}
                      >
                        <ItemImage imageUrl={item.imageUrl} type={item.type} className="h-56 w-full shrink-0" />
                        
                        {!isAvailable && (
                          <div className="absolute inset-0 z-20 bg-background/60 backdrop-blur-[2px] flex items-center justify-center p-6">
                            <div className="bg-destructive text-destructive-foreground font-black text-xs tracking-[0.2em] uppercase px-6 py-3 rounded-2xl shadow-2xl border-4 border-destructive-foreground/20 rotate-[-10deg] scale-110 animate-in zoom-in-50 duration-300">
                              {t('common.outOfStock')}
                            </div>
                          </div>
                        )}

                        <CardContent className="p-8 space-y-6 flex-grow flex flex-col justify-between">
                          <div className="space-y-4">
                            <div className="flex justify-between items-start">
                              <div className="space-y-1">
                                <span className="text-[10px] font-black uppercase tracking-[0.2em] px-2.5 py-1 rounded-lg bg-secondary text-primary shadow-sm inline-block mb-2">
                                  {t('takeOrder.categories.combo')}
                                </span>
                                <h3 className="font-black text-foreground group-hover:text-primary transition-colors text-2xl leading-tight uppercase tracking-tighter italic">
                                  {i18n.language === 'es' && item.nameEs ? item.nameEs : item.name}
                                </h3>
                              </div>
                              {isAvailable && (
                                <Badge className="bg-primary text-white border-none font-black text-[10px] uppercase tracking-tighter px-3 py-1 rounded-full animate-bounce">
                                  -{ (item as MenuDTO).formattedDiscount }
                                </Badge>
                              )}
                            </div>

                            <div className="bg-muted/50 p-4 rounded-3xl border-2 border-dashed border-secondary/20">
                              <p className="text-[11px] text-muted-foreground font-bold uppercase leading-relaxed line-clamp-2">
                                {(item as MenuDTO).productsList}
                              </p>
                            </div>
                          </div>

                          <div className="flex justify-between items-center pt-6 border-t-2 border-border/50 mt-auto">
                            <div className="space-y-0.5">
                              <p className="text-[10px] font-black text-muted-foreground uppercase tracking-widest leading-none">Total Combo</p>
                              <p className="font-black text-primary text-3xl tracking-tighter italic">{item.formattedPrice}</p>
                            </div>
                            <div className={cn(
                              "h-14 w-14 rounded-2xl flex items-center justify-center text-white shadow-xl transition-all",
                              isAvailable 
                                ? "bg-secondary text-primary shadow-secondary/20 group-hover:bg-primary group-hover:text-white group-hover:rotate-90" 
                                : "bg-muted text-muted-foreground shadow-none"
                            )}>
                              <Plus className="h-8 w-8" />
                            </div>
                          </div>
                        </CardContent>
                      </Card>
                    );
                  })}
                </div>
              </div>
            )}

            {/* Single Items Section */}
            {(activeTab === "all" || activeTab === "products") && filteredItems.filter(i => i.type === "PRODUCT").length > 0 && (
              <div className="space-y-6">
                <div className="flex items-center gap-4">
                  <div className="h-px flex-grow bg-border" />
                  <h3 className="text-xl font-black uppercase italic tracking-tighter text-primary flex items-center gap-2">
                    <UtensilsCrossed className="h-5 w-5 text-secondary" /> {t('takeOrder.tabs.products')}
                  </h3>
                  <div className="h-px flex-grow bg-border" />
                </div>
                <div className="grid grid-cols-1 md:grid-cols-2 xl:grid-cols-3 2xl:grid-cols-4 gap-6">
                  {filteredItems.filter(i => i.type === "PRODUCT").map(item => {
                    const isAvailable = item.id ? isItemAvailable(item.id, item.type) : true;
                    return (
                      <Card 
                        key={`${item.type}-${item.id}`} 
                        data-testid="product-card"
                        className={cn(
                          "group border-2 bg-card rounded-[2.5rem] overflow-hidden shadow-sm hover:shadow-2xl transition-all flex flex-col relative h-full p-0",
                          !isAvailable ? "opacity-60 grayscale cursor-not-allowed" : "hover:scale-[1.02] cursor-pointer"
                        )}
                        onClick={() => isAvailable && addToCart(item, item.type)}
                      >
                        <ItemImage imageUrl={item.imageUrl} type={item.type} className="h-44 w-full shrink-0" />
                        
                        {!isAvailable && (
                          <div className="absolute inset-0 z-20 bg-background/60 backdrop-blur-[2px] flex items-center justify-center p-6">
                            <div className="bg-destructive text-destructive-foreground font-black text-xs tracking-[0.2em] uppercase px-6 py-3 rounded-2xl shadow-2xl border-4 border-destructive-foreground/20 rotate-[-10deg] scale-110 animate-in zoom-in-50 duration-300">
                              {t('common.outOfStock')}
                            </div>
                          </div>
                        )}

                        <CardContent className="p-6 space-y-4 flex-grow flex flex-col justify-between">
                          <div className="space-y-3">
                            <div className="flex justify-between items-center">
                              <span className="text-[8px] font-black uppercase tracking-[0.2em] px-2 py-0.5 rounded-md bg-primary/10 text-primary">
                                {item.type}
                              </span>
                            </div>
                            
                            <h3 className="font-black text-foreground group-hover:text-primary transition-colors text-lg leading-tight uppercase tracking-tighter italic min-h-[2.5rem] line-clamp-2">
                              {i18n.language === 'es' && item.nameEs ? item.nameEs : item.name}
                            </h3>
                          </div>

                          <div className="flex justify-between items-center pt-4 border-t border-border/50 mt-4">
                            <p className="font-black text-primary text-2xl tracking-tighter italic">{item.formattedPrice}</p>
                            <div className={cn(
                              "h-11 w-11 rounded-2xl flex items-center justify-center text-white shadow-lg transition-all",
                              isAvailable 
                                ? "bg-primary shadow-primary/20 group-hover:bg-secondary group-hover:text-primary group-hover:rotate-90" 
                                : "bg-muted text-muted-foreground shadow-none"
                            )}>
                              <Plus className="h-6 w-6" />
                            </div>
                          </div>
                        </CardContent>
                      </Card>
                    );
                  })}
                </div>
              </div>
            )}

            {filteredItems.length === 0 && (
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
                          className="h-10 w-10 flex items-center justify-center text-muted-foreground/40 hover:text-destructive hover:bg-destructive/10 rounded-xl transition-all"
                        >
                          <Trash2 className="h-5 w-5" />
                        </button>
                      </div>
                    </div>
                  ))
                ) : (
                  <div className="flex flex-col items-center justify-center py-20 text-center px-6">
                    <div className="w-20 h-20 bg-muted rounded-full flex items-center justify-center mb-6 border-4 border-dashed border-border">
                      <ShoppingCart className="h-8 w-8 text-muted-foreground/20" />
                    </div>
                    <h4 className="font-black uppercase tracking-tighter text-xl text-foreground mb-2">{t('takeOrder.cart.emptyCart')}</h4>
                    <p className="text-muted-foreground text-xs font-medium uppercase tracking-widest leading-relaxed">
                      {t('takeOrder.cart.emptyCartDesc')}
                    </p>
                  </div>
                )}
              </div>
            </CardContent>

            <CardFooter className="p-8 bg-muted/30 border-t-2 border-border/50 shrink-0">
              <div className="w-full space-y-6">
                <div className="flex justify-between items-end">
                  <div className="space-y-1">
                    <span className="text-[10px] text-muted-foreground uppercase tracking-widest">{t('takeOrder.cart.totalDue')}</span>
                    <p className="text-4xl font-black text-primary tracking-tighter leading-none">{formatPrice(total)}</p>
                  </div>
                  <Button 
                    variant="ghost" 
                    size="sm"
                    className="text-destructive hover:text-destructive hover:bg-destructive/5 transition-all text-xs font-black uppercase tracking-widest"
                    onClick={clearCart}
                    disabled={cart.length === 0}
                  >
                    <Trash2 className="h-4 w-4 mr-2" /> {t('takeOrder.cart.voidTicket')}
                  </Button>
                </div>
                
                <Button 
                  data-testid="place-order-button"
                  className="w-full h-20 rounded-[1.5rem] font-black uppercase italic tracking-tighter text-2xl shadow-2xl shadow-primary/30 hover:scale-[1.02] active:scale-[0.98] transition-all bg-primary text-white group relative overflow-hidden"
                  onClick={handleCheckout}
                  disabled={createOrderMutation.isPending || cart.length === 0}
                >
                  <span className="relative z-10 flex items-center justify-center gap-3">
                    <ClipboardList className="h-6 w-6 text-secondary" />
                    {createOrderMutation.isPending ? t('common.loading') : t('takeOrder.cart.placeOrder')}
                  </span>
                  <div className="absolute inset-0 bg-white/10 translate-y-full group-hover:translate-y-0 transition-transform duration-500" />
                </Button>
              </div>
            </CardFooter>
          </Card>
        </div>
      </div>
    </div>
  );
}
