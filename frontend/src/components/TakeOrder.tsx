import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { 
  getBranches, 
  getProducts, 
  getMenus, 
  createOrder 
} from "@/api";
import type { BranchDTO, ProductDTO, MenuDTO, CreateOrderDTO } from "@/api";
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
import { ShoppingCart, Plus, Minus, Trash2, Store } from "lucide-react";

type CartItem = {
  itemId: number;
  name: string;
  price: number;
  quantity: number;
};

export function TakeOrder() {
  const [selectedBranch, setSelectedBranch] = useState<string>("");
  const [cart, setCart] = useState<CartItem[]>([]);
  const queryClient = useQueryClient();

  const { data: branches } = useQuery({ queryKey: ["branches"], queryFn: getBranches });
  const { data: products } = useQuery({ queryKey: ["products"], queryFn: getProducts });
  const { data: menus } = useQuery({ queryKey: ["menus"], queryFn: getMenus });

  const activeProducts = products?.filter(p => p.active) || [];
  const activeMenus = menus?.filter(m => m.active) || [];

  const addToCart = (item: ProductDTO | MenuDTO) => {
    if (!item.id) return;
    setCart(prev => {
      const existing = prev.find(i => i.itemId === item.id);
      if (existing) {
        return prev.map(i => i.itemId === item.id ? { ...i, quantity: i.quantity + 1 } : i);
      }
      return [...prev, { itemId: item.id!, name: item.name, price: item.price, quantity: 1 }];
    });
  };

  const updateQuantity = (itemId: number, delta: number) => {
    setCart(prev => prev.map(i => {
      if (i.itemId === itemId) {
        const newQty = Math.max(1, i.quantity + delta);
        return { ...i, quantity: newQty };
      }
      return i;
    }));
  };

  const removeFromCart = (itemId: number) => {
    setCart(prev => prev.filter(i => i.itemId !== itemId));
  };

  const total = cart.reduce((sum, item) => sum + (item.price * item.quantity), 0);
  const formatPrice = (amount: number) => new Intl.NumberFormat('es-ES', { style: 'currency', currency: 'EUR' }).format(amount);

  const createOrderMutation = useMutation({
    mutationFn: (order: CreateOrderDTO) => createOrder(order),
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["orders"] });
      setCart([]);
      setSelectedBranch("");
      alert("Order created successfully!");
    },
  });

  const handleCheckout = () => {
    if (!selectedBranch) {
      alert("Please select a branch");
      return;
    }
    if (cart.length === 0) {
      alert("Cart is empty");
      return;
    }

    createOrderMutation.mutate({
      branchId: parseInt(selectedBranch),
      items: cart.map(i => ({ itemId: i.itemId, quantity: i.quantity }))
    });
  };

  return (
    <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-8">
      {/* Items Selection */}
      <div className="md:col-span-2 space-y-6">
        <Card className="border-2">
          <CardHeader className="bg-slate-50 border-b">
            <CardTitle className="flex items-center gap-2 text-primary uppercase font-black italic tracking-tighter">
              <Store className="h-5 w-5" /> 1. Select Branch
            </CardTitle>
          </CardHeader>
          <CardContent className="pt-6">
            <Select value={selectedBranch} onValueChange={setSelectedBranch}>
              <SelectTrigger className="h-12 border-2">
                <SelectValue placeholder="Which branch are you at?" />
              </SelectTrigger>
              <SelectContent>
                {branches?.map(b => (
                  <SelectItem key={b.id} value={b.id?.toString() || ""}>{b.name} — {b.city}, {b.street}</SelectItem>
                ))}
              </SelectContent>
            </Select>
          </CardContent>
        </Card>

        <div className="space-y-4">
          <h3 className="text-lg font-bold uppercase tracking-tight text-slate-900 flex items-center gap-2">
            <span className="w-2 h-6 bg-primary rounded-full"></span>
            Individual Products
          </h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {activeProducts.map(product => (
              <Card key={product.id} className="hover:border-primary transition-all cursor-pointer group hover:shadow-md border-2" onClick={() => addToCart(product)}>
                <CardContent className="p-4 flex justify-between items-center">
                  <div>
                    <p className="font-bold text-slate-900 group-hover:text-primary transition-colors">{product.name}</p>
                    <p className="text-sm font-black text-primary">{product.formattedPrice}</p>
                  </div>
                  <div className="bg-secondary/20 group-hover:bg-secondary p-2 rounded-full transition-colors">
                    <Plus className="h-5 w-5 text-primary" />
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>

          <h3 className="text-lg font-bold mt-6 uppercase tracking-tight text-slate-900 flex items-center gap-2">
            <span className="w-2 h-6 bg-secondary rounded-full"></span>
            Special Menus
          </h3>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-4">
            {activeMenus.map(menu => (
              <Card key={menu.id} className="hover:border-primary transition-all cursor-pointer group hover:shadow-md border-2 bg-gradient-to-br from-white to-orange-50/30" onClick={() => addToCart(menu)}>
                <CardContent className="p-4 flex justify-between items-center">
                  <div>
                    <div className="flex items-center gap-2">
                      <p className="font-bold text-slate-900 group-hover:text-primary transition-colors">{menu.name}</p>
                      <Badge className="bg-red-600 text-[10px] font-black">{menu.formattedDiscount} OFF</Badge>
                    </div>
                    <p className="text-sm font-black text-primary">{menu.formattedPrice}</p>
                  </div>
                  <div className="bg-secondary/20 group-hover:bg-secondary p-2 rounded-full transition-colors">
                    <Plus className="h-5 w-5 text-primary" />
                  </div>
                </CardContent>
              </Card>
            ))}
          </div>
        </div>
      </div>

      {/* Cart / Summary */}
      <div className="md:col-span-1">
        <Card className="sticky top-8 border-2 shadow-xl ring-4 ring-secondary/20">
          <CardHeader className="bg-primary text-primary-foreground">
            <CardTitle className="flex items-center gap-2 uppercase font-black italic tracking-tighter">
              <ShoppingCart className="h-5 w-5" /> Current Order
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-4 max-h-[50vh] overflow-y-auto p-6">
            {cart.map(item => (
              <div key={item.itemId} className="flex flex-col gap-2 pb-4 border-b last:border-0">
                <div className="flex justify-between items-start">
                  <div className="flex-1">
                    <p className="font-bold text-slate-900">{item.name}</p>
                    <p className="text-xs text-muted-foreground">
                      Unit: {formatPrice(item.price)}
                    </p>
                  </div>
                  <div className="text-right">
                    <p className="font-black text-primary">{formatPrice(item.price * item.quantity)}</p>
                  </div>
                </div>
                
                <div className="flex items-center justify-between">
                  <div className="flex items-center border-2 rounded-lg bg-slate-50">
                    <Button size="icon" variant="ghost" className="h-8 w-8 hover:bg-secondary/20" onClick={() => updateQuantity(item.itemId, -1)}>
                      <Minus className="h-3 w-3" />
                    </Button>
                    <span className="w-10 text-center font-bold text-sm">{item.quantity}</span>
                    <Button size="icon" variant="ghost" className="h-8 w-8 hover:bg-secondary/20" onClick={() => updateQuantity(item.itemId, 1)}>
                      <Plus className="h-3 w-3" />
                    </Button>
                  </div>
                  <Button size="icon" variant="ghost" className="h-8 w-8 text-destructive hover:bg-destructive/10" onClick={() => removeFromCart(item.itemId)}>
                    <Trash2 className="h-4 w-4" />
                  </Button>
                </div>
              </div>
            ))}
            {cart.length === 0 && (
              <div className="text-center py-12">
                <div className="bg-slate-100 w-16 h-16 rounded-full flex items-center justify-center mx-auto mb-4 text-slate-300">
                  <ShoppingCart className="h-8 w-8" />
                </div>
                <p className="text-slate-400 font-medium">Your cart is empty</p>
                <p className="text-xs text-slate-300">Add items from the menu to start</p>
              </div>
            )}
          </CardContent>
          <CardFooter className="flex-col border-t bg-slate-50 p-6 gap-4">
            <div className="space-y-1 w-full">
              <div className="flex justify-between text-xs text-slate-500 font-medium">
                <span>Items Subtotal</span>
                <span>{formatPrice(total)}</span>
              </div>
              <div className="flex justify-between w-full text-2xl font-black text-slate-900 pt-2 border-t-2 border-slate-200">
                <span>TOTAL</span>
                <span className="text-primary">{formatPrice(total)}</span>
              </div>
            </div>
            <Button 
              className="w-full h-14 text-xl font-black uppercase tracking-tight shadow-lg shadow-primary/20 hover:scale-[1.02] active:scale-[0.98] transition-all" 
              disabled={cart.length === 0 || !selectedBranch || createOrderMutation.isPending}
              onClick={handleCheckout}
            >
              {createOrderMutation.isPending ? "Processing..." : "Submit Order"}
            </Button>
          </CardFooter>
        </Card>
      </div>
    </div>
  );
}
