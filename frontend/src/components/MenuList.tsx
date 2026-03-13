import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getMenus, deleteMenu, toggleMenuStatus, createMenu, updateMenu } from "@/api";
import type { MenuDTO } from "@/api";
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

export function MenuList() {
  const queryClient = useQueryClient();
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
      toast.success("Menu created successfully");
    },
    onError: () => {
      toast.error("Error creating menu");
    }
  });

  const updateMutation = useMutation({
    mutationFn: updateMenu,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["menus"] });
      setIsOpen(false);
      setEditingId(null);
      setFormData({ name: "", discountPercentage: "10", icon: "combo" });
      toast.success("Menu updated successfully");
    },
    onError: () => {
      toast.error("Error updating menu");
    }
  });

  const deleteMutation = useMutation({
    mutationFn: deleteMenu,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["menus"] });
      toast.success("Menu deleted successfully");
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
    { id: "combo", label: "Standard Combo" },
    { id: "burger", label: "Burger Special" },
    { id: "drink", label: "Drink Special" },
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
    return <TableSkeleton title="Menus Management" columnCount={6} />;
  }

  if (error) return <div>Error loading menus</div>;

  return (
    <Card className="w-full border-2 shadow-2xl rounded-[2.5rem] overflow-hidden bg-white">
      <CardHeader className="flex flex-row items-center justify-between p-8 border-b-2 border-slate-50 bg-slate-50/50">
        <div className="space-y-1">
          <CardTitle className="text-3xl font-black italic tracking-tighter uppercase text-primary flex items-center gap-3">
            <Utensils className="h-8 w-8 text-secondary" />
            Menus
          </CardTitle>
          <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Bundled item management</p>
        </div>
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
          <DialogTrigger asChild>
            <Button size="lg" className="rounded-2xl font-black uppercase text-xs tracking-widest px-8 shadow-lg shadow-primary/20 hover:scale-105 transition-all" onClick={handleCreate}>
              <Plus className="mr-2 h-5 w-5 text-secondary" /> New Menu
            </Button>
          </DialogTrigger>
          <DialogContent className="rounded-[2.5rem] p-0 overflow-hidden border-none shadow-2xl sm:max-w-[425px]">
            <DialogHeader className="bg-primary text-white p-8 relative overflow-hidden">
               <div className="absolute top-0 right-0 w-32 h-32 bg-secondary/10 rounded-full -mr-16 -mt-16 blur-3xl text-white" />
              <DialogTitle className="text-3xl font-black italic uppercase tracking-tighter relative z-10">
                {editingId ? "Edit Menu" : "Add New Menu"}
              </DialogTitle>
            </DialogHeader>
            <form onSubmit={handleSubmit} className="p-8 space-y-6">
              <div className="space-y-3">
                <Label htmlFor="name" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">Menu Name</Label>
                <Input
                  id="name"
                  placeholder="e.g. Combo Deluxe"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  className="h-14 border-2 bg-slate-50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4"
                />
              </div>
              <div className="space-y-3">
                <Label htmlFor="discount" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">Discount (%)</Label>
                <Input
                  id="discount"
                  type="number"
                  placeholder="10"
                  value={formData.discountPercentage}
                  onChange={(e) => setFormData({ ...formData, discountPercentage: e.target.value })}
                  required
                  className="h-14 border-2 bg-slate-50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4"
                />
              </div>
              <div className="space-y-3">
                <Label htmlFor="icon" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">Menu Style / Icon</Label>
                <Select value={formData.icon} onValueChange={(value) => setFormData({ ...formData, icon: value })}>
                  <SelectTrigger className="h-14 border-2 bg-slate-50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4">
                    <SelectValue placeholder="Select icon..." />
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
              <p className="text-[10px] text-slate-400 font-bold uppercase italic tracking-widest text-center mt-2">
                Items can be added later in the editor.
              </p>
              <div className="flex gap-4 pt-4">
                <Button type="button" variant="ghost" className="flex-1 h-14 rounded-2xl font-black uppercase tracking-widest text-xs text-slate-400 hover:text-destructive" onClick={() => setIsOpen(false)}>
                  Cancel
                </Button>
                <Button type="submit" disabled={createMutation.isPending || updateMutation.isPending} className="flex-[2] h-14 rounded-2xl font-black uppercase tracking-widest text-xs shadow-xl shadow-primary/20 hover:scale-105 transition-all">
                  {(createMutation.isPending || updateMutation.isPending) && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                  {editingId ? "Save Changes" : "Create Menu"}
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
              <TableHead className="px-8 py-5 font-black uppercase text-[10px] tracking-[0.2em] text-slate-400">Menu Name</TableHead>
              <TableHead className="py-5 font-black uppercase text-[10px] tracking-[0.2em] text-slate-400">Price</TableHead>
              <TableHead className="py-5 font-black uppercase text-[10px] tracking-[0.2em] text-slate-400">Discount</TableHead>
              <TableHead className="py-5 font-black uppercase text-[10px] tracking-[0.2em] text-slate-400">Status</TableHead>
              <TableHead className="px-8 py-5 font-black uppercase text-[10px] tracking-[0.2em] text-slate-400 text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {menus?.map((menu: MenuDTO) => (
              <TableRow key={menu.id} className="group hover:bg-slate-50/50 transition-colors border-b-2 border-slate-50 last:border-0">
                <TableCell className="px-8 py-6">
                  <div className="flex items-center gap-4">
                    <img src={getImageUrl(menu.icon || "combo")} alt={menu.name} className="w-12 h-12 rounded-xl object-cover shadow-sm group-hover:scale-110 transition-transform duration-500" />
                    <div className="space-y-1">
                      <p className="font-black text-slate-900 uppercase tracking-tight italic text-lg leading-tight">{menu.name}</p>
                      <p className="text-[10px] text-slate-400 font-bold uppercase truncate max-w-[200px]">{menu.productsList}</p>
                    </div>
                  </div>
                </TableCell>
                <TableCell className="py-6 font-black text-secondary text-xl tracking-tighter">{menu.formattedPrice || `$${menu.price.toFixed(2)}`}</TableCell>
                <TableCell className="py-6"><Badge variant="secondary" className="font-black text-[10px] rounded-lg bg-orange-100 text-orange-600 border-none px-2">-{menu.discountPercentage}%</Badge></TableCell>
                <TableCell className="py-6">
                   <div className="flex items-center gap-3">
                    <Switch
                      checked={menu.active}
                      onCheckedChange={() => 
                        menu.id && toggleMutation.mutate({ id: menu.id, active: menu.active })
                      }
                      className="data-[state=checked]:bg-green-500"
                    />
                    <span className={cn(
                      "text-[9px] font-black uppercase tracking-widest",
                      menu.active ? "text-green-500" : "text-slate-300"
                    )}>
                      {menu.active ? "Active" : "Disabled"}
                    </span>
                  </div>
                </TableCell>
                <TableCell className="px-8 py-6 text-right whitespace-nowrap">
                   <div className="flex justify-end gap-2">
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-12 w-12 text-slate-200 hover:text-primary hover:bg-primary/5 transition-all rounded-2xl group-hover:text-slate-300"
                      onClick={() => handleEdit(menu)}
                    >
                      <Edit2 className="h-5 w-5" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-12 w-12 text-slate-200 hover:text-destructive hover:bg-destructive/5 transition-all rounded-2xl group-hover:text-slate-300"
                      onClick={() => menu.id && deleteMutation.mutate(menu.id)}
                    >
                      <Trash2 className="h-5 w-5" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
            {menus?.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-4 text-muted-foreground">
                  No menus found.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
