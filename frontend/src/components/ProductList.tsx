import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getProducts, deleteProduct, toggleProductStatus, createProduct, updateProduct } from "@/api";
import type { ProductDTO } from "@/api";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from "@/components/ui/card";
import { Switch } from "@/components/ui/switch";
import { Trash2, Plus, Loader2, Package, Edit2, DollarSign, Info } from "lucide-react";
import { Skeleton } from "@/components/ui/skeleton";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Badge } from "@/components/ui/badge";
import { toast } from "sonner";
import { cn } from "@/lib/utils";

import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";

import { ErrorState } from "@/components/ui/error-state";
import { ConfirmDialog } from "@/components/ui/confirm-dialog";

function ProductSkeleton() {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 2xl:grid-cols-4 gap-8">
      {[1, 2, 3, 4, 5, 6, 7, 8].map((i) => (
        <Card key={i} className="rounded-[2.5rem] border-2 overflow-hidden h-full flex flex-col">
          <Skeleton className="h-56 w-full" />
          <CardContent className="p-8 space-y-6 flex-grow">
            <div className="space-y-2">
              <Skeleton className="h-4 w-1/4" />
              <Skeleton className="h-8 w-3/4" />
            </div>
            <div className="flex justify-between items-center pt-6 border-t">
              <Skeleton className="h-10 w-24" />
              <div className="flex gap-2">
                <Skeleton className="h-12 w-12 rounded-2xl" />
                <Skeleton className="h-12 w-12 rounded-2xl" />
              </div>
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}

export function ProductList() {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
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
      toast.success(t('admin.products.successCreate'));
    },
    onError: (err: any) => {
      toast.error(err.message || t('admin.products.errorCreate'));
    }
  });

  const updateMutation = useMutation({
    mutationFn: updateProduct,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["products"] });
      setIsOpen(false);
      setEditingId(null);
      setFormData({ name: "", price: "", icon: "burger" });
      toast.success(t('admin.products.successUpdate'));
    },
    onError: (err: any) => {
      toast.error(err.message || t('admin.products.errorUpdate'));
    }
  });

  const deleteMutation = useMutation({
    mutationFn: deleteProduct,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["products"] });
      toast.success(t('admin.products.successDelete'));
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
    { id: "burger", label: t('admin.products.icons.burger') },
    { id: "fries", label: t('admin.products.icons.sides') },
    { id: "drink", label: t('admin.products.icons.drink') },
    { id: "beer", label: t('admin.products.icons.beer') },
    { id: "shake", label: t('admin.products.icons.shake') },
    { id: "coffee", label: t('admin.products.icons.coffee') },
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
    return images[icon] || "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?q=80&w=800&auto=format&fit=crop";
  };

  if (isLoading) {
    return (
      <div className="space-y-8 animate-pulse">
        <div className="flex justify-between items-center">
          <Skeleton className="h-10 w-48 rounded-xl" />
          <Skeleton className="h-12 w-32 rounded-2xl" />
        </div>
        <ProductSkeleton />
      </div>
    );
  }

  if (error) {
    return (
      <ErrorState 
        variant="fetch"
        title={t('admin.products.offlineTitle')}
        message={t('admin.products.offlineMessage')}
        onRetry={() => queryClient.invalidateQueries({ queryKey: ["products"] })}
      />
    );
  }

  return (
    <div className="space-y-10 animate-in fade-in duration-700">
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-6 bg-card p-8 rounded-[2.5rem] border-2 shadow-sm relative overflow-hidden">
        <div className="absolute top-0 left-0 w-2 h-full bg-primary" />
        <div className="space-y-1">
          <h2 className="text-4xl font-black italic tracking-tighter uppercase text-primary flex items-center gap-4">
            <Package className="h-10 w-10 text-secondary" />
            {t('admin.products.title')}
          </h2>
          <p className="text-muted-foreground font-bold uppercase text-[10px] tracking-[0.2em]">{t('admin.products.subtitle')}</p>
        </div>
        
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
          <DialogTrigger asChild>
            <Button data-testid="new-product-button" size="lg" className="rounded-2xl font-black uppercase text-xs tracking-widest px-8 shadow-xl shadow-primary/20 hover:scale-105 transition-all h-14" onClick={handleCreate}>
              <Plus className="mr-2 h-5 w-5 text-secondary" /> {t('admin.products.new')}
            </Button>
          </DialogTrigger>
          <DialogContent className="rounded-[2.5rem] p-0 overflow-hidden border-none shadow-2xl w-[95vw] sm:max-w-[425px]">
            <DialogHeader className="bg-primary text-white p-8 relative overflow-hidden">
               <div className="absolute top-0 right-0 w-32 h-32 bg-secondary/10 rounded-full -mr-16 -mt-16 blur-3xl text-white" />
              <DialogTitle className="text-3xl font-black italic uppercase tracking-tighter relative z-10">
                {editingId ? t('admin.products.edit') : t('admin.products.new')}
              </DialogTitle>
            </DialogHeader>
            <form onSubmit={handleSubmit} className="p-8 space-y-6">
              <div className="space-y-3">
                <Label htmlFor="name" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">{t('admin.products.name')}</Label>
                <Input
                  id="name"
                  data-testid="product-name-input"
                  placeholder={t('admin.products.placeholder')}
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  className="h-14 border-2 bg-muted/50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4"
                />
              </div>
              <div className="space-y-3">
                <Label htmlFor="price" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">{t('common.price')} ($)</Label>
                <Input
                  id="price"
                  data-testid="product-price-input"
                  type="number"
                  step="0.01"
                  placeholder="0.00"
                  value={formData.price}
                  onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                  required
                  className="h-14 border-2 bg-muted/50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4"
                />
              </div>
              <div className="space-y-3">
                <Label htmlFor="icon" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">{t('admin.products.category')}</Label>
                <Select value={formData.icon} onValueChange={(value) => setFormData({ ...formData, icon: value })}>
                  <SelectTrigger data-testid="product-icon-select" className="h-14 border-2 bg-muted/50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4">
                    <SelectValue placeholder={t('admin.products.selectIcon')} />
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
                <Button type="button" variant="ghost" className="flex-1 h-14 rounded-2xl font-black uppercase tracking-widest text-xs text-muted-foreground hover:text-destructive" onClick={() => setIsOpen(false)}>
                  {t('common.cancel')}
                </Button>
                <Button data-testid="create-product-submit" type="submit" disabled={createMutation.isPending || updateMutation.isPending} className="flex-[2] h-14 rounded-2xl font-black uppercase tracking-widest text-xs shadow-xl shadow-primary/20 hover:scale-105 transition-all">
                  {(createMutation.isPending || updateMutation.isPending) && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                  {editingId ? t('common.saveChanges') : t('admin.products.new')}
                </Button>
              </div>
            </form>
          </DialogContent>
        </Dialog>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
        {products?.map((product: ProductDTO) => (
          <Card key={product.id} className={cn(
            "group border-2 rounded-[2.5rem] overflow-hidden shadow-sm hover:shadow-2xl transition-all duration-500 bg-card flex flex-col p-0",
            !product.active && "opacity-60 grayscale"
          )}>
            <div className="relative h-56 overflow-hidden">
              <img 
                src={getImageUrl(product.icon || "burger")} 
                alt={product.name} 
                className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700" 
              />
              <div className="absolute inset-0 bg-gradient-to-t from-black/60 to-transparent" />
              <div className="absolute bottom-6 left-6 right-6 flex justify-between items-end">
                <div className="space-y-1">
                  <Badge className="bg-secondary text-primary font-black text-[8px] tracking-[0.2em] rounded-md px-2 py-0.5 border-none uppercase">
                    {product.icon || 'Item'}
                  </Badge>
                  <h3 className="text-xl font-black text-white uppercase italic tracking-tighter leading-none">
                    {product.name}
                  </h3>
                </div>
              </div>
              <div className="absolute top-6 right-6">
                <Switch
                  checked={product.active}
                  onCheckedChange={() => 
                    product.id && toggleMutation.mutate({ id: product.id, active: product.active })
                  }
                  className="data-[state=checked]:bg-green-500 border-2 border-white/20 backdrop-blur-md"
                />
              </div>
            </div>
            
            <CardContent className="p-8 space-y-6 flex-grow">
              <div className="flex justify-between items-center">
                <div className="space-y-1">
                  <p className="text-[10px] font-black text-muted-foreground uppercase tracking-widest">{t('common.price')}</p>
                  <p className="text-3xl font-black text-secondary tracking-tighter italic">
                    {product.formattedPrice || `$${product.price.toFixed(2)}`}
                  </p>
                </div>
                <div className="flex gap-2">
                  <Button
                    variant="outline"
                    size="icon"
                    className="h-12 w-12 border-2 text-primary hover:bg-primary hover:text-white transition-all rounded-2xl"
                    onClick={() => handleEdit(product)}
                  >
                    <Edit2 className="h-5 w-5" />
                  </Button>
                  <ConfirmDialog
                    trigger={
                      <Button
                        variant="outline"
                        size="icon"
                        className="h-12 w-12 border-2 text-destructive hover:bg-destructive hover:text-white transition-all rounded-2xl"
                      >
                        <Trash2 className="h-5 w-5" />
                      </Button>
                    }
                    title={t('common.confirmTitle')}
                    description={t('common.confirmDelete')}
                    onConfirm={() => product.id && deleteMutation.mutate(product.id)}
                    destructive
                  />
                </div>
              </div>
            </CardContent>
            
            {!product.active && (
              <div className="px-8 pb-8 pt-0">
                <div className="flex items-center gap-2 text-muted-foreground bg-muted/50 p-3 rounded-xl border-2">
                  <Info className="h-4 w-4" />
                  <span className="text-[10px] font-black uppercase tracking-widest">{t('common.disabled')}</span>
                </div>
              </div>
            )}
          </Card>
        ))}
        
        {products?.length === 0 && (
          <div className="col-span-full py-24 text-center bg-card rounded-[2.5rem] border-2 border-dashed">
            <Package className="h-16 w-16 mx-auto mb-4 text-muted-foreground/20" />
            <h3 className="text-2xl font-black text-muted-foreground uppercase italic tracking-tighter">{t('common.noResults')}</h3>
            <p className="text-[10px] font-black uppercase tracking-widest text-muted-foreground/40 mt-2">{t('common.noResultsAdjust')}</p>
          </div>
        )}
      </div>
    </div>
  );
}
