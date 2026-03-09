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
  Coffee
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

const ItemIcon = ({ icon, type, className }: { icon?: string, type: "PRODUCT" | "MENU", className?: string }) => {
  if (type === "MENU" || icon === "combo") return <Sparkles className={className} />;
  
  switch (icon) {
    case "burger": return <Utensils className={className} />;
    case "fries": return <CircleDashed className={className} />;
    case "drink": return <CupSoda className={className} />;
    case "beer": return <Beer className={className} />;
    case "shake": return <IceCream className={className} />;
    case "coffee": return <Coffee className={className} />;
    default: return <Package className={className} />;
  }
};

export function TakeOrder() {
  const [selectedBranch, setSelectedBranch] = useState<string>(() => localStorage.getItem("selectedBranch") || "");
  const [searchQuery, setSearchQuery] = useState("");
  const [activeTab, setActiveTab] = useState("all");
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
    const p = activeProducts.filter(item => item.name.toLowerCase().includes(query)).map(item => ({ ...item, type: "PRODUCT" as const }));
    const m = activeMenus.filter(item => item.name.toLowerCase().includes(query)).map(item => ({ ...item, type: "MENU" as const }));
    if (activeTab === "products") return p;
    if (activeTab === "menus") return m;
    return [...p, ...m];
  }, [activeProducts, activeMenus, searchQuery, activeTab]);

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
      // We keep the branch selected for high-volume environments
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
    <div className="flex flex-col gap-8 mt-4 animate-in fade-in duration-700">
      <div className="flex flex-col lg:flex-row justify-between items-start lg:items-center gap-6 bg-white p-8 rounded-3xl border-2 shadow-sm relative overflow-hidden">
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

      <div className="grid grid-cols-1 xl:grid-cols-12 gap-8">
        <div className="xl:col-span-8 space-y-8">
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
                <TabsTrigger value="all" className="px-6 rounded-xl font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">All</TabsTrigger>
                <TabsTrigger value="products" className="px-6 rounded-xl font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">Products</TabsTrigger>
                <TabsTrigger value="menus" className="px-6 rounded-xl font-black uppercase text-[10px] tracking-widest data-[state=active]:bg-primary data-[state=active]:text-white">Menus</TabsTrigger>
              </TabsList>
            </Tabs>
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {loadingProducts || loadingMenus ? (
              [1, 2, 3, 4, 5, 6].map(i => <div key={i} className="h-64 bg-white rounded-3xl border-2 animate-pulse" />)
            ) : filteredItems.length > 0 ? (
              filteredItems.map(item => (
                <Card 
                  key={`${item.type}-${item.id}`} 
                  className="group hover:border-secondary transition-all cursor-pointer border-2 bg-white rounded-3xl overflow-hidden shadow-sm hover:shadow-2xl hover:-translate-y-1 select-none flex flex-col relative"
                  onClick={() => addToCart(item, item.type)}
                >
                  <div className={cn(
                    "h-32 shrink-0 flex items-center justify-center transition-colors relative overflow-hidden",
                    item.type === "MENU" ? "bg-slate-900" : "bg-slate-50 group-hover:bg-slate-100"
                  )}>
                    <ItemIcon icon={item.icon} type={item.type} className={cn("h-12 w-12 z-10", item.type === "MENU" ? "text-secondary" : "text-primary/20 group-hover:text-primary/40")} />
                    <div className="absolute inset-0 bg-gradient-to-t from-black/20 to-transparent opacity-0 group-hover:opacity-100 transition-opacity" />
                  </div>
                  <CardContent className="p-6 space-y-4 flex-grow flex flex-col">
                    <div className="space-y-1 flex-grow">
                      <div className="flex justify-between items-center mb-1">
                        <span className="text-[9px] font-black uppercase tracking-[0.2em] text-slate-400">{item.type}</span>
                        {item.type === "MENU" && <Badge className="bg-secondary text-primary font-black text-[9px] border-none px-2 rounded-lg">COMBO DEAL</Badge>}
                      </div>
                      <h3 className="font-black text-slate-900 group-hover:text-primary transition-colors text-lg leading-tight uppercase tracking-tighter italic">{item.name}</h3>
                      {item.type === "MENU" && (
                        <p className="text-[10px] text-slate-400 font-bold uppercase leading-relaxed line-clamp-2 mt-2">
                          Includes: {(item as MenuDTO).productsList}
                        </p>
                      )}
                    </div>
                    <div className="flex justify-between items-center pt-2 mt-auto">
                      <p className="font-black text-secondary text-2xl tracking-tighter">{item.formattedPrice}</p>
                      <div className="h-10 w-10 rounded-xl bg-primary flex items-center justify-center text-white shadow-lg shadow-primary/20 group-hover:scale-110 transition-transform">
                        <Plus className="h-6 w-6" />
                      </div>
                    </div>
                  </CardContent>
                  {/* Decorative background element */}
                  <div className="absolute -right-4 -bottom-4 opacity-[0.03] group-hover:opacity-[0.08] transition-opacity pointer-events-none">
                    <ItemIcon icon={item.icon} type={item.type} className="h-24 w-24" />
                  </div>
                </Card>
              ))
            ) : (
              <div className="col-span-full py-24 bg-white rounded-[2.5rem] border-2 border-dashed border-slate-200 text-center">
                <Utensils className="h-16 w-12 text-slate-200 mx-auto mb-6" />
                <h3 className="text-2xl font-black uppercase tracking-tighter italic text-slate-900">No items found</h3>
                <p className="text-slate-400 font-medium max-w-xs mx-auto">We couldn't find anything matching your search criteria.</p>
              </div>
            )}
          </div>
        </div>

        <div className="xl:col-span-4">
          <Card className="sticky top-8 border-2 shadow-2xl rounded-[2.5rem] overflow-hidden flex flex-col max-h-[calc(100vh-6rem)] py-0 bg-white select-none">
            <CardHeader className="bg-primary text-primary-foreground p-8 shrink-0 relative overflow-hidden">
              <div className="absolute top-0 right-0 w-32 h-32 bg-secondary/10 rounded-full -mr-16 -mt-16 blur-3xl" />
              <div className="flex justify-between items-center relative z-10">
                <CardTitle className="flex items-center gap-4 uppercase font-black italic tracking-tighter text-3xl">
                  <ShoppingCart className="h-8 w-8 text-secondary" /> Order
                </CardTitle>
                <Badge className="font-black px-4 py-1.5 bg-secondary text-primary rounded-xl">
                  {cart.reduce((s, i) => s + i.quantity, 0)} ITEMS
                </Badge>
              </div>
            </CardHeader>
            
            <CardContent className="flex-grow overflow-y-auto p-0 scrollbar-hide">
              {cart.length > 0 ? (
                <div className="divide-y-2 divide-slate-50">
                  {cart.map(item => (
                    <div key={`${item.type}-${item.itemId}`} className="p-6 hover:bg-slate-50/80 transition-colors">
                      <div className="flex justify-between items-start mb-4">
                        <div className="space-y-1">
                          <p className="font-black text-slate-900 leading-tight uppercase tracking-tight italic">{item.name}</p>
                          <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest">{formatPrice(item.price)} unit</p>
                        </div>
                        <p className="font-black text-primary text-xl tracking-tighter">{formatPrice(item.price * item.quantity)}</p>
                      </div>
                      <div className="flex items-center justify-between">
                        <div className="flex items-center border-2 border-slate-100 rounded-2xl bg-white shadow-sm overflow-hidden p-1">
                          <Button size="icon" variant="ghost" className="h-10 w-10 hover:bg-slate-50 text-primary" onClick={() => updateQuantity(item.itemId, -1)}><Minus className="h-4 w-4" /></Button>
                          <span className="w-12 text-center font-black text-base">{item.quantity}</span>
                          <Button size="icon" variant="ghost" className="h-10 w-10 hover:bg-slate-50 text-primary" onClick={() => updateQuantity(item.itemId, 1)}><Plus className="h-4 w-4" /></Button>
                        </div>
                        <Button size="icon" variant="ghost" className="h-12 w-12 text-slate-300 hover:text-destructive hover:bg-destructive/5 transition-all rounded-2xl" onClick={() => removeFromCart(item.itemId)}><Trash2 className="h-5 w-5" /></Button>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <div className="text-center py-24 px-12">
                  <div className="w-24 h-24 rounded-[2.5rem] bg-slate-50 flex items-center justify-center mx-auto mb-8 relative">
                    <ShoppingCart className="h-10 w-10 text-slate-200" />
                    <div className="absolute inset-0 border-2 border-dashed border-slate-200 rounded-[2.5rem] animate-spin-slow" />
                  </div>
                  <h4 className="text-xl font-black uppercase italic tracking-tighter text-slate-900 mb-2">Cart is Empty</h4>
                  <p className="text-slate-400 text-xs font-bold uppercase tracking-widest leading-loose">Waiting for selection...</p>
                </div>
              )}
            </CardContent>

            <CardFooter className="flex-col border-t-2 border-slate-100 bg-white p-8 gap-8 shrink-0 shadow-inner">
              <div className="space-y-2 w-full">
                <div className="flex justify-between text-[10px] text-slate-400 font-black uppercase tracking-[0.3em]">
                  <span>Subtotal</span>
                  <span>{formatPrice(total)}</span>
                </div>
                <div className="flex justify-between w-full text-4xl font-black text-slate-900 items-baseline">
                  <span className="italic tracking-tighter uppercase text-xl">Total</span>
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
                    {createOrderMutation.isPending ? "Processing..." : <><CheckCircle2 className="h-8 w-8 text-secondary" /> Submit Order</>}
                  </span>
                  <div className="absolute inset-0 bg-white/5 translate-y-full group-hover:translate-y-0 transition-transform duration-500" />
                </Button>
                {cart.length > 0 && <Button variant="ghost" className="w-full text-slate-300 hover:text-destructive font-black uppercase tracking-[0.2em] text-[9px]" onClick={clearCart}><RotateCcw className="h-3 w-3 mr-3" /> Cancel Transaction</Button>}
              </div>
            </CardFooter>
          </Card>
        </div>
      </div>
    </div>
  );
}
