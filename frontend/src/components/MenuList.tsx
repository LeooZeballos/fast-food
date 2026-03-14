import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getMenus, deleteMenu, toggleMenuStatus, createMenu, updateMenu } from "@/api";
import type { MenuDTO } from "@/api";
import { useState } from "react";
import { useTranslation } from "react-i18next";
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
import { Trash2, Plus, Loader2, Utensils, Edit2 } from "lucide-react";
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
    onError: () => {
      toast.error(t('admin.menus.errorCreate'));
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
    onError: () => {
      toast.error(t('admin.menus.errorUpdate'));
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
    return <TableSkeleton title={t('admin.menus.title')} columnCount={6} />;
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
    <Card className="w-full border-2 shadow-2xl rounded-[2rem] md:rounded-[2.5rem] overflow-hidden bg-card">
      <CardHeader className="flex flex-col sm:flex-row items-start sm:items-center justify-between p-6 md:p-8 border-b-2 border-border/50 bg-muted/50/50 gap-4 sm:gap-0">
        <div className="space-y-1">
          <CardTitle className="text-2xl md:text-3xl font-black italic tracking-tighter uppercase text-primary flex items-center gap-3">
            <Utensils className="h-6 w-6 md:h-8 md:w-8 text-secondary" />
            {t('admin.menus.title')}
          </CardTitle>
          <p className="text-[9px] md:text-[10px] font-bold text-muted-foreground uppercase tracking-widest ml-1">{t('admin.menus.subtitle')}</p>
        </div>
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
          <DialogTrigger asChild>
            <Button size="lg" className="w-full sm:w-auto rounded-xl md:rounded-2xl font-black uppercase text-[10px] md:text-xs tracking-widest px-6 md:px-8 shadow-lg shadow-primary/20 hover:scale-105 transition-all" onClick={handleCreate}>
              <Plus className="mr-2 h-4 w-4 md:h-5 md:w-5 text-secondary" /> {t('admin.menus.new')}
            </Button>
          </DialogTrigger>
          <DialogContent className="rounded-[2rem] md:rounded-[2.5rem] p-0 overflow-hidden border-none shadow-2xl w-[95vw] sm:max-w-[425px]">
            <DialogHeader className="bg-primary text-white p-6 md:p-8 relative overflow-hidden">
               <div className="absolute top-0 right-0 w-32 h-32 bg-secondary/10 rounded-full -mr-16 -mt-16 blur-3xl text-white" />
              <DialogTitle className="text-2xl md:text-3xl font-black italic uppercase tracking-tighter relative z-10">
                {editingId ? t('admin.menus.edit') : t('admin.menus.new')}
              </DialogTitle>
            </DialogHeader>
            <form onSubmit={handleSubmit} className="p-6 md:p-8 space-y-4 md:space-y-6">
              <div className="space-y-2 md:space-y-3">
                <Label htmlFor="name" className="text-[9px] md:text-[10px] font-black uppercase tracking-widest text-primary ml-1">{t('admin.menus.name')}</Label>
                <Input
                  id="name"
                  placeholder={t('admin.menus.placeholder')}
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  className="h-12 md:h-14 border-2 bg-muted/50 rounded-xl md:rounded-2xl focus-visible:ring-primary/10 text-base md:text-lg font-medium px-4"
                />
              </div>
              <div className="space-y-2 md:space-y-3">
                <Label htmlFor="discount" className="text-[9px] md:text-[10px] font-black uppercase tracking-widest text-primary ml-1">{t('admin.menus.discount')}</Label>
                <Input
                  id="discount"
                  type="number"
                  placeholder="10"
                  value={formData.discountPercentage}
                  onChange={(e) => setFormData({ ...formData, discountPercentage: e.target.value })}
                  required
                  className="h-12 md:h-14 border-2 bg-muted/50 rounded-xl md:rounded-2xl focus-visible:ring-primary/10 text-base md:text-lg font-medium px-4"
                />
              </div>
              <div className="space-y-2 md:space-y-3">
                <Label htmlFor="icon" className="text-[9px] md:text-[10px] font-black uppercase tracking-widest text-primary ml-1">{t('admin.menus.style')}</Label>
                <Select value={formData.icon} onValueChange={(value) => setFormData({ ...formData, icon: value })}>
                  <SelectTrigger className="h-12 md:h-14 border-2 bg-muted/50 rounded-xl md:rounded-2xl focus-visible:ring-primary/10 text-base md:text-lg font-medium px-4">
                    <SelectValue placeholder={t('admin.products.selectIcon')} />
                  </SelectTrigger>
                  <SelectContent className="rounded-xl md:rounded-2xl">
                    {MENU_ICONS.map(icon => (
                      <SelectItem key={icon.id} value={icon.id} className="py-2 md:py-3">
                        <div className="flex items-center gap-3">
                          <img src={getImageUrl(icon.id)} alt={icon.label} className="w-6 h-6 md:w-8 md:h-8 rounded-lg object-cover" />
                          <span className="font-black text-xs md:text-sm uppercase tracking-tight">{icon.label}</span>
                        </div>
                      </SelectItem>
                    ))}
                  </SelectContent>
                </Select>
              </div>
              <p className="text-[9px] md:text-[10px] text-muted-foreground font-bold uppercase italic tracking-widest text-center mt-2">
                {t('admin.menus.itemsNote')}
              </p>
              <div className="flex gap-3 md:gap-4 pt-4">
                <Button type="button" variant="ghost" className="flex-1 h-12 md:h-14 rounded-xl md:rounded-2xl font-black uppercase tracking-widest text-[10px] md:text-xs text-muted-foreground hover:text-destructive" onClick={() => setIsOpen(false)}>
                  {t('common.cancel')}
                </Button>
                <Button type="submit" disabled={createMutation.isPending || updateMutation.isPending} className="flex-[2] h-12 md:h-14 rounded-xl md:rounded-2xl font-black uppercase tracking-widest text-[10px] md:text-xs shadow-xl shadow-primary/20 hover:scale-105 transition-all">
                  {(createMutation.isPending || updateMutation.isPending) && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                  {editingId ? t('common.saveChanges') : t('admin.menus.new')}
                </Button>
              </div>
            </form>
          </DialogContent>
        </Dialog>
      </CardHeader>
      <CardContent className="p-0 overflow-x-auto">
        <Table>
          <TableHeader className="bg-muted/50/50">
            <TableRow className="hover:bg-transparent border-b-2 border-border/50">
              <TableHead className="px-6 md:px-8 py-4 md:py-5 font-black uppercase text-[9px] md:text-[10px] tracking-[0.2em] text-muted-foreground">{t('admin.menus.name')}</TableHead>
              <TableHead className="py-4 md:py-5 font-black uppercase text-[9px] md:text-[10px] tracking-[0.2em] text-muted-foreground">{t('common.price')}</TableHead>
              <TableHead className="hidden sm:table-cell py-4 md:py-5 font-black uppercase text-[9px] md:text-[10px] tracking-[0.2em] text-muted-foreground">{t('common.discount')}</TableHead>
              <TableHead className="hidden sm:table-cell py-4 md:py-5 font-black uppercase text-[9px] md:text-[10px] tracking-[0.2em] text-muted-foreground">{t('common.status')}</TableHead>
              <TableHead className="px-6 md:px-8 py-4 md:py-5 font-black uppercase text-[9px] md:text-[10px] tracking-[0.2em] text-muted-foreground text-right">{t('common.actions')}</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {menus?.map((menu: MenuDTO) => (
              <TableRow key={menu.id} className="group hover:bg-muted/50/50 transition-colors border-b-2 border-border/50 last:border-0">
                <TableCell className="px-6 md:px-8 py-4 md:py-6">
                  <div className="flex items-center gap-3 md:gap-4">
                    <img src={getImageUrl(menu.icon || "combo")} alt={menu.name} className="w-10 h-10 md:w-12 md:h-12 rounded-lg md:rounded-xl object-cover shadow-sm group-hover:scale-110 transition-transform duration-500" />
                    <div className="space-y-1">
                      <p className="font-black text-foreground uppercase tracking-tight italic text-sm md:text-lg leading-tight">{menu.name}</p>
                      <p className="text-[8px] md:text-[10px] text-muted-foreground font-bold uppercase truncate max-w-[120px] md:max-w-[200px]">{menu.productsList}</p>
                    </div>
                  </div>
                </TableCell>
                <TableCell className="py-4 md:py-6 font-black text-secondary text-base md:text-xl tracking-tighter">{menu.formattedPrice || `$${menu.price.toFixed(2)}`}</TableCell>
                <TableCell className="hidden sm:table-cell py-4 md:py-6"><Badge variant="secondary" className="font-black text-[8px] md:text-[10px] rounded-lg bg-orange-100 text-orange-600 border-none px-2">-{menu.discountPercentage}%</Badge></TableCell>
                <TableCell className="hidden sm:table-cell py-4 md:py-6">
                   <div className="flex items-center gap-3">
                    <Switch
                      checked={menu.active}
                      onCheckedChange={() => 
                        menu.id && toggleMutation.mutate({ id: menu.id, active: menu.active })
                      }
                      className="data-[state=checked]:bg-green-500"
                    />
                    <span className={cn(
                      "text-[8px] md:text-[9px] font-black uppercase tracking-widest",
                      menu.active ? t('common.active') : t('common.disabled')
                    )}>
                      {menu.active ? t('common.active') : t('common.disabled')}
                    </span>
                  </div>
                </TableCell>
                <TableCell className="px-6 md:px-8 py-4 md:py-6 text-right whitespace-nowrap">
                  <div className="flex justify-end gap-1 md:gap-2 opacity-60 group-hover:opacity-100 transition-opacity">
                    <Button
                      variant="outline"
                      size="icon"
                      className="h-10 w-10 md:h-12 md:w-12 border-2 text-primary hover:bg-primary hover:text-white transition-all rounded-xl md:rounded-2xl"
                      onClick={() => handleEdit(menu)}
                    >
                      <Edit2 className="h-4 w-4 md:h-5 md:w-5" />
                    </Button>
                    <Button
                      variant="outline"
                      size="icon"
                      className="h-10 w-10 md:h-12 md:w-12 border-2 text-destructive hover:bg-destructive hover:text-white transition-all rounded-xl md:rounded-2xl"
                      onClick={() => menu.id && deleteMutation.mutate(menu.id)}
                    >
                      <Trash2 className="h-4 w-4 md:h-5 md:w-5" />
                    </Button>
                  </div>
                </TableCell>

              </TableRow>
            ))}
            {menus?.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-4 text-muted-foreground">
                  {t('common.noResults')}
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
