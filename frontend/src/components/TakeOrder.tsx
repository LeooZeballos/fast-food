import { useState, useMemo } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "sonner";
import { 
  getBranches, 
  getProducts, 
  getMenus, 
  createOrder 
} from "@/api";
import type { ProductDTO, MenuDTO } from "@/api";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from "@/components/ui/card";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Badge } from "@/components/ui/badge";
import { Input } from "@/components/ui/input";
import { Tabs, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { 
  ShoppingCart, 
  Plus, 
  Minus, 
  Trash2, 
  Store, 
  Search, 
  RotateCcw, 
  CheckCircle2,
  Utensils,
  Package,
  X,
  CupSoda,
  Beer,
  IceCream,
  CircleDashed,
  UtensilsCrossed,
  Sparkles,
  Coffee,
  Receipt,
  Beef
} from "lucide-react";
import { cn } from "@/lib/utils";

type CartItem = {
  itemId: number;
  name: string;
  price: number;
  quantity: number;
  icon?: string;
  type: "PRODUCT" | "MENU";
};

const ItemImage = ({ icon, type, className }: { icon?: string, type: "PRODUCT" | "MENU", className?: string }) => {
  const images: Record<string, string> = {
    burger: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?q=80&w=800&auto=format&fit=crop",
    fries: "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?q=80&w=800&auto=format&fit=crop",
    drink: "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?q=80&w=800&auto=format&fit=crop",
    beer: "https://images.unsplash.com/photo-1535958636474-b021ee887b13?q=80&w=800&auto=format&fit=crop",
    shake: "https://images.unsplash.com/photo-1572490122747-3968b75cc699?q=80&w=800&auto=format&fit=crop",
    coffee: "https://images.unsplash.com/photo-1541167760496-162955ed8a9f?q=80&w=800&auto=format&fit=crop",
    combo: "https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?q=80&w=800&auto=format&fit=crop",
  };

  const imageUrl = (icon && images[icon]) || (type === "MENU" ? images.combo : "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=800&auto=format&fit=crop");

  return (
    <div className={cn("relative overflow-hidden", className)}>
      <img 
        src={imageUrl} 
        alt={icon} 
        className="w-full h-full object-cover transition-transform duration-700 group-hover:scale-110" 
      />
    </div>
  );
};

const CATEGORIES = [
  { id: "all", label: "All", icon: <UtensilsCrossed className="h-4 w-4" /> },
  { id: "burger", label: "Burgers", icon: <Beef className="h-4 w-4" /> },
  { id: "drink", label: "Drinks", icon: <CupSoda className="h-4 w-4" /> },
  { id: "sides", label: "Sides", icon: <CircleDashed className="h-4 w-4" /> },
  { id: "combo", label: "Combos", icon: <Sparkles className="h-4 w-4" /> },
];

export function TakeOrder() {
  const [selectedBranch, setSelectedBranch] = useState<string>(() => localStorage.getItem("selectedBranch") || "");
  const [searchQuery, setSearchQuery] = useState("");
  const [activeTab, setActiveTab] = useState("all");
  const [activeCategory, setActiveCategory] = useState("all");
  const [cart, setCart] = useState<CartItem[]>([]);
  const queryClient = useQueryClient();

  const handleBranchChange = (value: string) => {
    setSelectedBranch(value);
    localStorage.setItem("selectedBranch", value);
  };

  const { data: branches } = useQuery({ queryKey: ["branches"], queryFn: getBranches });
  const { data: products, isLoading: loadingProducts } = useQuery({ queryKey: ["products"], queryFn: getProducts });
  const { data: menus, isLoading: loadingMenus } = useQuery({ queryKey: ["menus"], queryFn: getMenus });

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
      items = items.filter(i => i.name.toLowerCase().includes(query));
    }

    return items;
  }, [activeProducts, activeMenus, searchQuery, activeTab, activeCategory]);

  const addToCart = (item: ProductDTO | MenuDTO, type: "PRODUCT" | "MENU") => {
    if (!item.id) return;
    setCart(prev => {
      const existing = prev.find(i => i.itemId === item.id);
      if (existing) return prev.map(i => i.itemId === item.id ? { ...i, quantity: i.quantity + 1 } : i);
      return [...prev, { itemId: item.id!, name: item.name, price: item.price, quantity: 1, icon: item.icon, type }];
    });
  };

  const updateQuantity = (itemId: number, delta: number) => {
    setCart(prev => prev.map(i => i.itemId === itemId ? { ...i, quantity: Math.max(1, i.quantity + delta) } : i));
  };

  const removeFromCart = (itemId: number) => setCart(prev => prev.filter(i => i.itemId !== itemId));
  const clearCart = () => window.confirm("Clear current order?") && setCart([]);
  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  const formatPrice = (amount: number) => new Intl.NumberFormat('en-US', { style: 'currency', currency: 'USD' }).format(amount);

  const createOrderMutation = useMutation({
    mutationFn: createOrder,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["orders"] });
      setCart([]);
      toast.success("Order placed successfully!", {
        description: "The kitchen has received your order.",
      });
    },
    onError: (error) => {
      toast.error("Failed to place order", {
        description: error.message || "Please try again later.",
      });
    }
  });

  const handleCheckout = () => {
    if (!selectedBranch) {
      toast.warning("Missing Branch", {
        description: "Please select a service branch before submitting.",
      });
      return;
    }
    if (cart.length === 0) {
      toast.warning("Empty Cart", {
        description: "Add some delicious items before checking out.",
      });
      return;
    }
    createOrderMutation.mutate({ branchId: parseInt(selectedBranch), items: cart.map(i => ({ itemId: i.itemId, quantity: i.quantity })) });
  };

  return (
    <div className="flex flex-col gap-8 mt-4 animate-in fade-in duration-700 pb-20">
      <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 bg-white p-8 rounded-[2.5rem] border-2 shadow-sm relative overflow-hidden">
        <div className="absolute top-0 left-0 w-2 h-full bg-primary" />
        <div className="space-y-1">
          <h2 className="text-4xl font-black italic tracking-tighter uppercase text-primary flex items-center gap-4">
            <UtensilsCrossed className="h-10 w-10 text-secondary" /> POS Terminal
          </h2>
          <p className="text-slate-400 font-bold uppercase text-[10px] tracking-[0.2em]">Kitchen Order Management System</p>
        </div>
        
        <div className="w-full lg:w-80">
          <label className="text-[10px] font-black uppercase tracking-widest text-primary/40 mb-2 block ml-1">Service Branch</label>
          <Select value={selectedBranch} onValueChange={handleBranchChange}>
            <SelectTrigger className="h-14 border-2 bg-slate-50 focus:ring-primary/10 rounded-2xl transition-all px-4">
              <div className="flex items-center gap-3 w-full">
                <Store className="h-5 w-5 text-secondary shrink-0" />
                <SelectValue placeholder="Select location..." />
              </div>
            </SelectTrigger>
            <SelectContent className="rounded-2xl">
              {branches?.map(b => (
                <SelectItem key={b.id} value={b.id?.toString() || ""} className="py-3">
                  <div className="flex items-center gap-2">
                    <span className="font-black text-sm uppercase tracking-tight">{b.name}</span>
                    <span className="text-[10px] text-slate-400 uppercase font-bold ml-1">— {b.city}</span>
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
            <div className="flex flex-col md:flex-row gap-6 items-center justify-between bg-white/50 p-4 rounded-3xl border-2 border-dashed border-slate-200">
              <div className="relative w-full md:w-96 group">
                <Search className="absolute left-4 top-1/2 -translate-y-1/2 h-5 w-5 text-slate-300 group-focus-within:text-primary transition-colors" />
                <Input 
                  placeholder="Search the menu..." 
                  className="pl-12 h-12 border-2 bg-white rounded-2xl focus-visible:ring-primary/10 text-lg font-medium"
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                />
                {searchQuery && <button onClick={() => setSearchQuery("")} className="absolute right-4 top-1/2 -translate-y-1/2 text-slate-300 hover:text-primary"><X className="h-5 w-5" /></button>}
              </div>

              <Tabs value={activeTab} onValueChange={setActiveTab} className="w-full md:w-auto">
                <TabsList className="h-12 p-1.5 bg-slate-200/50 rounded-2xl">
                  <TabsTrigger value="all" className="px-6 rounded-xl font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">All Items</TabsTrigger>
                  <TabsTrigger value="products" className="px-6 rounded-xl font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">A La Carte</TabsTrigger>
                  <TabsTrigger value="menus" className="px-6 rounded-xl font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">Combos</TabsTrigger>
                </TabsList>
              </Tabs>
            </div>

            <div className="flex gap-2 overflow-x-auto pb-2 scrollbar-hide">
              {CATEGORIES.map(cat => (
                <Button
                  key={cat.id}
                  variant={activeCategory === cat.id ? "default" : "outline"}
                  onClick={() => setActiveCategory(cat.id)}
                  className={cn(
                    "h-14 px-6 rounded-2xl flex items-center gap-3 transition-all border-2 shrink-0",
                    activeCategory === cat.id ? "bg-secondary text-primary border-secondary" : "bg-white text-slate-400 border-slate-100 hover:border-primary/20 hover:text-primary"
                  )}
                >
                  <span className={cn(activeCategory === cat.id ? "text-primary" : "text-slate-300")}>{cat.icon}</span>
                  <span className="font-black uppercase text-[10px] tracking-widest italic">{cat.label}</span>
                </Button>
              ))}
            </div>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {loadingProducts || loadingMenus ? (
              [1, 2, 3, 4, 5, 6].map(i => <div key={i} className="h-64 bg-white rounded-[2rem] border-2 animate-pulse" />)
            ) : filteredItems.length > 0 ? (
              filteredItems.map(item => (
                <Card 
                  key={`${item.type}-${item.id}`} 
                  className="group hover:border-secondary/50 transition-all cursor-pointer border-2 bg-white rounded-[2rem] overflow-hidden shadow-sm hover:shadow-2xl hover:-translate-y-1 select-none flex flex-col relative"
                  onClick={() => addToCart(item, item.type)}
                >
                  <div className={cn(
                    "h-32 shrink-0 flex items-center justify-center transition-colors relative overflow-hidden",
                    item.type === "MENU" ? "bg-slate-900" : "bg-slate-50 group-hover:bg-slate-100"
                  )}>
                    <ItemImage icon={item.icon} type={item.type} className={cn("h-full w-full z-10")} />
                    <div className="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity" />
                    {item.type === "MENU" && <div className="absolute top-4 left-4"><Badge className="bg-secondary text-primary font-black text-[9px] border-none px-2 rounded-lg">POPULAR</Badge></div>}
                  </div>
                  <CardContent className="p-6 space-y-4 flex-grow flex flex-col">
                    <div className="space-y-1 flex-grow">
                      <div className="flex justify-between items-center mb-1">
                        <span className="text-[9px] font-black uppercase tracking-[0.2em] text-slate-300">{item.type}</span>
                        {item.type === "MENU" && <span className="text-[10px] font-black text-secondary italic tracking-tighter uppercase">Save {(item as MenuDTO).formattedDiscount}</span>}
                      </div>
                      <h3 className="font-black text-slate-900 group-hover:text-primary transition-colors text-lg leading-tight uppercase tracking-tighter italic">{item.name}</h3>
                      {item.type === "MENU" && (
                        <p className="text-[10px] text-slate-400 font-bold uppercase leading-relaxed line-clamp-2 mt-2">
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
              <div className="col-span-full py-24 bg-white rounded-[3rem] border-2 border-dashed border-slate-200 text-center">
                <Utensils className="h-16 w-12 text-slate-200 mx-auto mb-6" />
                <h3 className="text-2xl font-black uppercase tracking-tighter italic text-slate-900">No items found</h3>
                <p className="text-slate-400 font-bold uppercase text-[10px] tracking-widest mt-2">Try adjusting your search or filters</p>
              </div>
            )}
          </div>
        </div>

        <div className="xl:col-span-4 lg:sticky lg:top-8">
          <Card className="border-2 shadow-2xl rounded-[2.5rem] overflow-hidden flex flex-col max-h-[calc(100vh-6rem)] py-0 bg-white select-none relative">
            <CardHeader className="bg-slate-900 text-white p-8 shrink-0 relative overflow-hidden">
              <div className="absolute top-0 right-0 w-32 h-32 bg-secondary/10 rounded-full -mr-16 -mt-16 blur-3xl" />
              <div className="flex justify-between items-center relative z-10">
                <CardTitle className="flex items-center gap-4 uppercase font-black italic tracking-tighter text-3xl">
                  <Receipt className="h-8 w-8 text-secondary" /> Ticket
                </CardTitle>
                <div className="text-right">
                  <p className="text-[10px] font-black text-slate-500 uppercase tracking-widest">ORDER TYPE</p>
                  <p className="text-xs font-black text-secondary uppercase tracking-widest">DINE-IN</p>
                </div>
              </div>
            </CardHeader>
            
            <CardContent className="flex-grow overflow-y-auto p-0 scrollbar-hide bg-[#fdfdfd]">
              <div className="px-8 pt-8 pb-4 space-y-1">
                <p className="font-mono text-[10px] text-slate-400 text-center uppercase">********************************</p>
                <p className="font-mono text-[10px] text-slate-400 text-center uppercase tracking-[0.2em]">FASTFOOD SYSTEM V1.0</p>
                <p className="font-mono text-[10px] text-slate-400 text-center uppercase">********************************</p>
              </div>

              {cart.length > 0 ? (
                <div className="px-2">
                  {cart.map(item => (
                    <div key={`${item.type}-${item.itemId}`} className="p-6 hover:bg-slate-50/80 transition-colors group">
                      <div className="flex justify-between items-start mb-4">
                        <div className="space-y-1">
                          <p className="font-black text-slate-900 leading-tight uppercase tracking-tight italic flex items-center gap-2">
                            <span className="text-secondary">●</span> {item.name}
                          </p>
                          <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-4">{item.quantity} x {formatPrice(item.price)}</p>
                        </div>
                        <p className="font-black text-slate-900 text-xl tracking-tighter">{formatPrice(item.price * item.quantity)}</p>
                      </div>
                      <div className="flex items-center justify-between ml-4">
                        <div className="flex items-center border-2 border-slate-100 rounded-2xl bg-white shadow-sm overflow-hidden p-0.5">
                          <Button size="icon" variant="ghost" className="h-8 w-8 hover:bg-slate-50 text-primary" onClick={() => updateQuantity(item.itemId, -1)}><Minus className="h-3 w-3" /></Button>
                          <span className="w-8 text-center font-black text-sm">{item.quantity}</span>
                          <Button size="icon" variant="ghost" className="h-8 w-8 hover:bg-slate-50 text-primary" onClick={() => updateQuantity(item.itemId, 1)}><Plus className="h-3 w-3" /></Button>
                        </div>
                        <Button size="icon" variant="ghost" className="h-10 w-10 text-slate-200 hover:text-destructive hover:bg-destructive/5 transition-all rounded-xl" onClick={() => removeFromCart(item.itemId)}><Trash2 className="h-4 w-4" /></Button>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-24 px-12">
                  <div className="w-24 h-24 rounded-[2.5rem] bg-slate-50 flex items-center justify-center mx-auto mb-8 relative">
                    <ShoppingCart className="h-10 w-10 text-slate-100" />
                    <div className="absolute inset-0 border-2 border-dashed border-slate-100 rounded-[2.5rem] animate-spin-slow" />
                  </div>
                  <h4 className="text-xl font-black uppercase italic tracking-tighter text-slate-300 mb-2">Cart is Empty</h4>
                  <p className="text-[10px] text-slate-300 font-bold uppercase tracking-widest leading-loose">Items will appear here</p>
                </div>
              )}
              
              <div className="px-8 py-8 space-y-1">
                <p className="font-mono text-[10px] text-slate-400 text-center uppercase">--------------------------------</p>
              </div>
            </CardContent>

            <CardFooter className="flex-col border-t-2 border-slate-100 bg-white p-8 gap-8 shrink-0 shadow-inner">
              <div className="space-y-2 w-full">
                <div className="flex justify-between text-[10px] text-slate-400 font-black uppercase tracking-[0.3em]">
                  <span>Subtotal</span>
                  <span>{formatPrice(total)}</span>
                </div>
                <div className="flex justify-between w-full text-4xl font-black text-slate-900 items-baseline">
                  <span className="italic tracking-tighter uppercase text-xl">Total Due</span>
                  <span className="text-secondary tracking-tighter">{formatPrice(total)}</span>
                </div>
              </div>
              
              <div className="flex flex-col gap-4 w-full">
                <Button 
                  className="w-full h-20 text-2xl font-black uppercase tracking-tighter italic shadow-2xl transition-all group relative overflow-hidden rounded-3xl bg-primary text-white shadow-primary/20 hover:scale-[1.02] active:scale-[0.98]"
                  disabled={cart.length === 0 || !selectedBranch || createOrderMutation.isPending}
                  onClick={handleCheckout}
                >
                  <span className="relative z-10 flex items-center gap-4">
                    {createOrderMutation.isPending ? "SENDING..." : <><CheckCircle2 className="h-8 w-8 text-secondary" /> Place Order</>}
                  </span>
                  <div className="absolute inset-0 bg-white/5 translate-y-full group-hover:translate-y-0 transition-transform duration-500" />
                </Button>
                {cart.length > 0 && <Button variant="ghost" className="w-full text-slate-300 hover:text-destructive font-black uppercase tracking-[0.2em] text-[9px] h-10" onClick={clearCart}><RotateCcw className="h-3 w-3 mr-3" /> Void Ticket</Button>}
              </div>
            </CardFooter>
            
            {/* Receipt jagged edge effect */}
            <div className="absolute bottom-0 left-0 right-0 h-2 bg-white" style={{ clipPath: 'polygon(0% 100%, 5% 0%, 10% 100%, 15% 0%, 20% 100%, 25% 0%, 30% 100%, 35% 0%, 40% 100%, 45% 0%, 50% 100%, 55% 0%, 60% 100%, 65% 0%, 70% 100%, 75% 0%, 80% 100%, 85% 0%, 90% 100%, 95% 0%, 100% 100%)' }} />
          </Card>
        </div>
      </div>
    </div>
  );
}
