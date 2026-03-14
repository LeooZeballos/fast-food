import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getMenus, deleteMenu, toggleMenuStatus, createMenu, updateMenu } from "@/api";
import type { MenuDTO } from "@/api";
import { useState } from "react";
import { useTranslation } from "react-i18next";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle, CardFooter } from "@/components/ui/card";
import { Switch } from "@/components/ui/switch";
import { Trash2, Plus, Loader2, Utensils, Edit2, Info, Sparkles, Tag } from "lucide-react";
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

function MenuSkeleton() {
  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-6 mt-8">
      {[1, 2, 3, 4].map((i) => (
        <Card key={i} className="rounded-[2rem] border-2 overflow-hidden">
          <Skeleton className="h-56 w-full" />
          <CardContent className="p-6 space-y-4">
            <Skeleton className="h-6 w-3/4" />
            <Skeleton className="h-4 w-full" />
            <div className="flex justify-between items-center pt-4">
              <Skeleton className="h-8 w-20" />
              <Skeleton className="h-10 w-10 rounded-xl" />
            </div>
          </CardContent>
        </Card>
      ))}
    </div>
  );
}

export function MenuList() {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const [isOpen, setIsOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState({ name: "", discountPercentage: "10", icon: "combo" });

  const { data: menus, isLoading, error } = useQuery({
    queryKey: ["menus"],
    queryFn: getMenus,
  });

  const createMutation = useMutation({
    mutationFn: createMenu,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["menus"] });
      setIsOpen(false);
      setFormData({ name: "", discountPercentage: "10", icon: "combo" });
      toast.success(t('admin.menus.successCreate'));
    },
    onError: (err: any) => {
      toast.error(err.message || t('admin.menus.errorCreate'));
    }
  });

  const updateMutation = useMutation({
    mutationFn: updateMenu,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["menus"] });
      setIsOpen(false);
      setEditingId(null);
      setFormData({ name: "", discountPercentage: "10", icon: "combo" });
      toast.success(t('admin.menus.successUpdate'));
    },
    onError: (err: any) => {
      toast.error(err.message || t('admin.menus.errorUpdate'));
    }
  });

  const deleteMutation = useMutation({
    mutationFn: deleteMenu,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["menus"] });
      toast.success(t('admin.menus.successDelete'));
    },
  });

  const toggleMutation = useMutation({
    mutationFn: toggleMenuStatus,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["menus"] });
    },
  });

  const handleEdit = (menu: MenuDTO) => {
    setEditingId(menu.id || null);
    setFormData({ name: menu.name, discountPercentage: menu.discountPercentage.toString(), icon: menu.icon || "combo" });
    setIsOpen(true);
  };

  const handleCreate = () => {
    setEditingId(null);
    setFormData({ name: "", discountPercentage: "10", icon: "combo" });
    setIsOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const payload = {
      name: formData.name,
      discountPercentage: parseFloat(formData.discountPercentage),
      icon: formData.icon,
    };

    if (editingId) {
      updateMutation.mutate({ id: editingId, menu: payload });
    } else {
      createMutation.mutate({
        ...payload,
        active: true,
      });
    }
  };

  const MENU_ICONS = [
    { id: "combo", label: t('admin.menus.icons.combo') },
    { id: "burger", label: t('admin.menus.icons.burger') },
    { id: "drink", label: t('admin.menus.icons.drink') },
  ];

  const getImageUrl = (icon: string) => {
    const images: Record<string, string> = {
      burger: "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?q=80&w=800&auto=format&fit=crop",
      combo: "https://images.unsplash.com/photo-1594212699903-ec8a3eca50f5?q=80&w=800&auto=format&fit=crop",
      drink: "https://images.unsplash.com/photo-1622483767028-3f66f32aef97?q=80&w=800&auto=format&fit=crop",
    };
    return images[icon] || images.combo;
  };

  if (isLoading) {
    return (
      <div className="space-y-8 animate-pulse">
        <div className="flex justify-between items-center">
          <Skeleton className="h-10 w-48 rounded-xl" />
          <Skeleton className="h-12 w-32 rounded-2xl" />
        </div>
        <MenuSkeleton />
      </div>
    );
  }

  if (error) {
    return (
      <ErrorState 
        variant="fetch"
        title={t('admin.menus.offlineTitle')}
        message={t('admin.menus.offlineMessage')}
        onRetry={() => queryClient.invalidateQueries({ queryKey: ["menus"] })}
      />
    );
  }

  return (
    <div className="space-y-10 animate-in fade-in duration-700">
      <div className="flex flex-col md:flex-row items-start md:items-center justify-between gap-6 bg-card p-8 rounded-[2.5rem] border-2 shadow-sm relative overflow-hidden">
        <div className="absolute top-0 left-0 w-2 h-full bg-secondary" />
        <div className="space-y-1">
          <h2 className="text-4xl font-black italic tracking-tighter uppercase text-primary flex items-center gap-4">
            <Utensils className="h-10 w-10 text-secondary" />
            {t('admin.menus.title')}
          </h2>
          <p className="text-muted-foreground font-bold uppercase text-[10px] tracking-[0.2em]">{t('admin.menus.subtitle')}</p>
        </div>
        
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
          <DialogTrigger asChild>
            <Button size="lg" className="rounded-2xl font-black uppercase text-xs tracking-widest px-8 shadow-xl shadow-primary/20 hover:scale-105 transition-all h-14" onClick={handleCreate}>
              <Plus className="mr-2 h-5 w-5 text-secondary" /> {t('admin.menus.new')}
            </Button>
          </DialogTrigger>
          <DialogContent className="rounded-[2.5rem] p-0 overflow-hidden border-none shadow-2xl w-[95vw] sm:max-w-[425px]">
            <DialogHeader className="bg-primary text-white p-8 relative overflow-hidden">
               <div className="absolute top-0 right-0 w-32 h-32 bg-secondary/10 rounded-full -mr-16 -mt-16 blur-3xl text-white" />
              <DialogTitle className="text-3xl font-black italic uppercase tracking-tighter relative z-10">
                {editingId ? t('admin.menus.edit') : t('admin.menus.new')}
              </DialogTitle>
            </DialogHeader>
            <form onSubmit={handleSubmit} className="p-8 space-y-6">
              <div className="space-y-3">
                <Label htmlFor="name" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">{t('admin.menus.name')}</Label>
                <Input
                  id="name"
                  placeholder={t('admin.menus.placeholder')}
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  className="h-14 border-2 bg-muted/50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4"
                />
              </div>
              <div className="space-y-3">
                <Label htmlFor="discount" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">{t('admin.menus.discount')} (%)</Label>
                <Input
                  id="discount"
                  type="number"
                  placeholder="10"
                  value={formData.discountPercentage}
                  onChange={(e) => setFormData({ ...formData, discountPercentage: e.target.value })}
                  required
                  className="h-14 border-2 bg-muted/50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4"
                />
              </div>
              <div className="space-y-3">
                <Label htmlFor="icon" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">{t('admin.menus.style')}</Label>
                <Select value={formData.icon} onValueChange={(value) => setFormData({ ...formData, icon: value })}>
                  <SelectTrigger className="h-14 border-2 bg-muted/50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4">
                    <SelectValue placeholder={t('admin.products.selectIcon')} />
                  </SelectTrigger>
                  <SelectContent className="rounded-2xl">
                    {MENU_ICONS.map(icon => (
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
              <p className="text-[10px] text-muted-foreground font-bold uppercase italic tracking-widest text-center mt-2 bg-muted/50 p-2 rounded-lg">
                {t('admin.menus.itemsNote')}
              </p>
              <div className="flex gap-4 pt-4">
                <Button type="button" variant="ghost" className="flex-1 h-14 rounded-2xl font-black uppercase tracking-widest text-xs text-muted-foreground hover:text-destructive" onClick={() => setIsOpen(false)}>
                  {t('common.cancel')}
                </Button>
                <Button type="submit" disabled={createMutation.isPending || updateMutation.isPending} className="flex-[2] h-14 rounded-2xl font-black uppercase tracking-widest text-xs shadow-xl shadow-primary/20 hover:scale-105 transition-all">
                  {(createMutation.isPending || updateMutation.isPending) && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                  {editingId ? t('common.saveChanges') : t('admin.menus.new')}
                </Button>
              </div>
            </form>
          </DialogContent>
        </Dialog>
      </div>

      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-8">
        {menus?.map((menu: MenuDTO) => (
          <Card key={menu.id} className={cn(
            "group border-2 rounded-[2.5rem] overflow-hidden shadow-sm hover:shadow-2xl transition-all duration-500 bg-card flex flex-col",
            !menu.active && "opacity-60 grayscale"
          )}>
            <div className="relative h-64 overflow-hidden">
              <img 
                src={getImageUrl(menu.icon || "combo")} 
                alt={menu.name} 
                className="w-full h-full object-cover group-hover:scale-110 transition-transform duration-700" 
              />
              <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-black/20 to-transparent" />
              
              {menu.discountPercentage > 0 && (
                <div className="absolute top-6 left-6 animate-bounce">
                  <Badge className="bg-secondary text-primary font-black text-xs px-3 py-1 rounded-xl border-none shadow-xl">
                    <Sparkles className="h-3 w-3 mr-1.5" />
                    -{menu.discountPercentage}% OFF
                  </Badge>
                </div>
              )}

              <div className="absolute bottom-6 left-6 right-6">
                <div className="space-y-2">
                  <h3 className="text-2xl font-black text-white uppercase italic tracking-tighter leading-tight drop-shadow-md">
                    {menu.name}
                  </h3>
                  <div className="flex flex-wrap gap-1">
                    {menu.productsList.split(',').slice(0, 3).map((item, idx) => (
                      <Badge key={idx} variant="outline" className="bg-white/10 backdrop-blur-md text-white border-white/20 text-[7px] font-black uppercase tracking-widest">
                        {item.trim()}
                      </Badge>
                    ))}
                    {menu.productsList.split(',').length > 3 && (
                      <Badge variant="outline" className="bg-white/10 backdrop-blur-md text-white border-white/20 text-[7px] font-black uppercase">
                        + {menu.productsList.split(',').length - 3}
                      </Badge>
                    )}
                  </div>
                </div>
              </div>
              
              <div className="absolute top-6 right-6">
                <Switch
                  checked={menu.active}
                  onCheckedChange={() => 
                    menu.id && toggleMutation.mutate({ id: menu.id, active: menu.active })
                  }
                  className="data-[state=checked]:bg-green-500 border-2 border-white/20 backdrop-blur-md"
                />
              </div>
            </div>
            
            <CardContent className="p-8 space-y-6 flex-grow">
              <div className="flex justify-between items-center">
                <div className="space-y-1">
                  <p className="text-[10px] font-black text-muted-foreground uppercase tracking-widest flex items-center gap-1.5">
                    <Tag className="h-3 w-3" /> {t('common.total')}
                  </p>
                  <p className="text-3xl font-black text-secondary tracking-tighter italic">
                    {menu.formattedPrice || `$${menu.price.toFixed(2)}`}
                  </p>
                </div>
                <div className="flex gap-2">
                  <Button
                    variant="outline"
                    size="icon"
                    className="h-12 w-12 border-2 text-primary hover:bg-primary hover:text-white transition-all rounded-2xl shadow-sm"
                    onClick={() => handleEdit(menu)}
                  >
                    <Edit2 className="h-5 w-5" />
                  </Button>
                  <Button
                    variant="outline"
                    size="icon"
                    className="h-12 w-12 border-2 text-destructive hover:bg-destructive hover:text-white transition-all rounded-2xl shadow-sm"
                    onClick={() => menu.id && deleteMutation.mutate(menu.id)}
                  >
                    <Trash2 className="h-5 w-5" />
                  </Button>
                </div>
              </div>
            </CardContent>
            
            {!menu.active && (
              <div className="px-8 pb-8 pt-0">
                <div className="flex items-center gap-2 text-muted-foreground bg-muted/50 p-3 rounded-xl border-2">
                  <Info className="h-4 w-4" />
                  <span className="text-[10px] font-black uppercase tracking-widest">{t('common.disabled')}</span>
                </div>
              </div>
            )}
          </Card>
        ))}
        
        {menus?.length === 0 && (
          <div className="col-span-full py-32 text-center bg-card rounded-[2.5rem] border-2 border-dashed">
            <Utensils className="h-16 w-16 mx-auto mb-4 text-muted-foreground/20" />
            <h3 className="text-2xl font-black text-muted-foreground uppercase italic tracking-tighter">{t('common.noResults')}</h3>
            <p className="text-[10px] font-black uppercase tracking-widest text-muted-foreground/40 mt-2">{t('common.noResultsAdjust')}</p>
          </div>
        )}
      </div>
    </div>
  );
}
