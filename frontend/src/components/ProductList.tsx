import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getProducts, deleteProduct, toggleProductStatus, createProduct, updateProduct } from "@/api";
import type { ProductDTO } from "@/api";
import { useState } from "react";
import {
  Table,
  TableBody,
  TableCell,
  TableHead,
  TableHeader,
  TableRow,
} from "@/components/ui/table";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Switch } from "@/components/ui/switch";
import { Trash2, Plus, Loader2, Package, Edit2 } from "lucide-react";
import { TableSkeleton } from "@/components/ui/table-skeleton";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { toast } from "sonner";
import { cn } from "@/lib/utils";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

export function ProductList() {
  const queryClient = useQueryClient();
  const [isOpen, setIsOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState({ name: "", price: "", icon: "burger" });

  const { data: products, isLoading, error } = useQuery({
    queryKey: ["products"],
    queryFn: getProducts,
  });

  const createMutation = useMutation({
    mutationFn: createProduct,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["products"] });
      setIsOpen(false);
      setFormData({ name: "", price: "", icon: "burger" });
      toast.success("Product created successfully");
    },
    onError: () => {
      toast.error("Error creating product");
    }
  });

  const updateMutation = useMutation({
    mutationFn: updateProduct,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["products"] });
      setIsOpen(false);
      setEditingId(null);
      setFormData({ name: "", price: "", icon: "burger" });
      toast.success("Product updated successfully");
    },
    onError: () => {
      toast.error("Error updating product");
    }
  });

  const deleteMutation = useMutation({
    mutationFn: deleteProduct,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["products"] });
      toast.success("Product deleted successfully");
    },
  });

  const toggleMutation = useMutation({
    mutationFn: toggleProductStatus,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["products"] });
    },
  });

  const handleEdit = (product: ProductDTO) => {
    setEditingId(product.id || null);
    setFormData({ name: product.name, price: product.price.toString(), icon: product.icon || "burger" });
    setIsOpen(true);
  };

  const handleCreate = () => {
    setEditingId(null);
    setFormData({ name: "", price: "", icon: "burger" });
    setIsOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (editingId) {
      updateMutation.mutate({
        id: editingId,
        product: {
          name: formData.name,
          price: parseFloat(formData.price),
          icon: formData.icon,
        }
      });
    } else {
      createMutation.mutate({
        name: formData.name,
        price: parseFloat(formData.price),
        icon: formData.icon,
        active: true,
      });
    }
  };

  const PRODUCT_ICONS = [
    { id: "burger", label: "Burger" },
    { id: "fries", label: "Fries/Sides" },
    { id: "drink", label: "Soft Drink" },
    { id: "beer", label: "Beer" },
    { id: "shake", label: "Milkshake" },
    { id: "coffee", label: "Coffee" },
  ];

  const getImageUrl = (icon: string) => {
    const images: Record<string, string> = {
      burger: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?q=80&w=800&auto=format&fit=crop",
      fries: "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?q=80&w=800&auto=format&fit=crop",
      drink: "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?q=80&w=800&auto=format&fit=crop",
      beer: "https://images.unsplash.com/photo-1535958636474-b021ee887b13?q=80&w=800&auto=format&fit=crop",
      shake: "https://images.unsplash.com/photo-1572490122747-3968b75cc699?q=80&w=800&auto=format&fit=crop",
      coffee: "https://images.unsplash.com/photo-1541167760496-162955ed8a9f?q=80&w=800&auto=format&fit=crop",
    };
    return images[icon] || "https://images.unsplash.com/photo-1546069901-ba9599a7e63c?q=80&w=800&auto=format&fit=crop";
  };

  if (isLoading) {
    return <TableSkeleton title="Products Management" columnCount={4} />;
  }

  if (error) return <div>Error loading products</div>;

  return (
    <Card className="w-full border-2 shadow-2xl rounded-[2.5rem] overflow-hidden bg-white">
      <CardHeader className="flex flex-row items-center justify-between p-8 border-b-2 border-slate-50 bg-slate-50/50">
        <div className="space-y-1">
          <CardTitle className="text-3xl font-black italic tracking-tighter uppercase text-primary flex items-center gap-3">
            <Package className="h-8 w-8 text-secondary" />
            Products
          </CardTitle>
          <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Individual item management</p>
        </div>
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
          <DialogTrigger asChild>
            <Button size="lg" className="rounded-2xl font-black uppercase text-xs tracking-widest px-8 shadow-lg shadow-primary/20 hover:scale-105 transition-all" onClick={handleCreate}>
              <Plus className="mr-2 h-5 w-5 text-secondary" /> New Product
            </Button>
          </DialogTrigger>
          <DialogContent className="rounded-[2.5rem] p-0 overflow-hidden border-none shadow-2xl sm:max-w-[425px]">
            <DialogHeader className="bg-primary text-white p-8 relative overflow-hidden">
               <div className="absolute top-0 right-0 w-32 h-32 bg-secondary/10 rounded-full -mr-16 -mt-16 blur-3xl text-white" />
              <DialogTitle className="text-3xl font-black italic uppercase tracking-tighter relative z-10">
                {editingId ? "Edit Product" : "Add New Product"}
              </DialogTitle>
            </DialogHeader>
            <form onSubmit={handleSubmit} className="p-8 space-y-6">
              <div className="space-y-3">
                <Label htmlFor="name" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">Product Name</Label>
                <Input
                  id="name"
                  placeholder="e.g. Double Cheese Burger"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  className="h-14 border-2 bg-slate-50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4"
                />
              </div>
              <div className="space-y-3">
                <Label htmlFor="price" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">Price ($)</Label>
                <Input
                  id="price"
                  type="number"
                  step="0.01"
                  placeholder="0.00"
                  value={formData.price}
                  onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                  required
                  className="h-14 border-2 bg-slate-50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4"
                />
              </div>
              <div className="space-y-3">
                <Label htmlFor="icon" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">Category / Icon</Label>
                <Select value={formData.icon} onValueChange={(value) => setFormData({ ...formData, icon: value })}>
                  <SelectTrigger className="h-14 border-2 bg-slate-50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4">
                    <SelectValue placeholder="Select icon..." />
                  </SelectTrigger>
                  <SelectContent className="rounded-2xl">
                    {PRODUCT_ICONS.map(icon => (
                      <SelectItem key={icon.id} value={icon.id} className="py-3">
                        <div className="flex items-center gap-3">
                          <img src={getImageUrl(icon.id)} alt={icon.label} className="w-8 h-8 rounded-lg object-cover" />
                          <span className="font-black text-sm uppercase tracking-tight">{icon.label}</span>
                        </div>
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <div className="flex gap-4 pt-4">
                <Button type="button" variant="ghost" className="flex-1 h-14 rounded-2xl font-black uppercase tracking-widest text-xs text-slate-400 hover:text-destructive" onClick={() => setIsOpen(false)}>
                  Cancel
                </Button>
                <Button type="submit" disabled={createMutation.isPending || updateMutation.isPending} className="flex-[2] h-14 rounded-2xl font-black uppercase tracking-widest text-xs shadow-xl shadow-primary/20 hover:scale-105 transition-all">
                  {(createMutation.isPending || updateMutation.isPending) && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                  {editingId ? "Save Changes" : "Create Product"}
                </Button>
              </div>
            </form>
          </DialogContent>
        </Dialog>
      </CardHeader>
      <CardContent className="p-0">
        <Table>
          <TableHeader className="bg-slate-50/50">
            <TableRow className="hover:bg-transparent border-b-2 border-slate-50">
              <TableHead className="px-8 py-5 font-black uppercase text-[10px] tracking-[0.2em] text-slate-400">Item Name</TableHead>
              <TableHead className="py-5 font-black uppercase text-[10px] tracking-[0.2em] text-slate-400">Price</TableHead>
              <TableHead className="py-5 font-black uppercase text-[10px] tracking-[0.2em] text-slate-400">Status</TableHead>
              <TableHead className="px-8 py-5 font-black uppercase text-[10px] tracking-[0.2em] text-slate-400 text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {products?.map((product: ProductDTO) => (
              <TableRow key={product.id} className="group hover:bg-slate-50/50 transition-colors border-b-2 border-slate-50 last:border-0">
                <TableCell className="px-8 py-6">
                  <div className="flex items-center gap-4">
                    <img src={getImageUrl(product.icon || "burger")} alt={product.name} className="w-12 h-12 rounded-xl object-cover shadow-sm group-hover:scale-110 transition-transform duration-500" />
                    <span className="font-black text-slate-900 uppercase tracking-tight italic text-lg">{product.name}</span>
                  </div>
                </TableCell>
                <TableCell className="py-6 font-black text-secondary text-xl tracking-tighter">{product.formattedPrice || `$${product.price.toFixed(2)}`}</TableCell>
                <TableCell className="py-6">
                  <div className="flex items-center gap-3">
                    <Switch
                      checked={product.active}
                      onCheckedChange={() => 
                        product.id && toggleMutation.mutate({ id: product.id, active: product.active })
                      }
                      className="data-[state=checked]:bg-green-500"
                    />
                    <span className={cn(
                      "text-[9px] font-black uppercase tracking-widest",
                      product.active ? "text-green-500" : "text-slate-300"
                    )}>
                      {product.active ? "Active" : "Disabled"}
                    </span>
                  </div>
                </TableCell>
                <TableCell className="px-8 py-6 text-right whitespace-nowrap">
                  <div className="flex justify-end gap-2">
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-12 w-12 text-slate-200 hover:text-primary hover:bg-primary/5 transition-all rounded-2xl group-hover:text-slate-300"
                      onClick={() => handleEdit(product)}
                    >
                      <Edit2 className="h-5 w-5" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-12 w-12 text-slate-200 hover:text-destructive hover:bg-destructive/5 transition-all rounded-2xl group-hover:text-slate-300"
                      onClick={() => product.id && deleteMutation.mutate(product.id)}
                    >
                      <Trash2 className="h-5 w-5" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
            {products?.length === 0 && (
              <TableRow>
                <TableCell colSpan={4} className="text-center py-4 text-muted-foreground">
                  No products found.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
