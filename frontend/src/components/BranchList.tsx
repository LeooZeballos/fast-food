import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getBranches, deleteBranch, createBranch, updateBranch } from "@/api";
import type { BranchDTO } from "@/api";
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
import { Trash2, Plus, Loader2, Store, Edit2 } from "lucide-react";
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

import { ErrorState } from "@/components/ui/error-state";
import { ConfirmDialog } from "@/components/ui/confirm-dialog";

export function BranchList() {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const [isOpen, setIsOpen] = useState(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState({ name: "", street: "", city: "" });

  const { data: branches, isLoading, error } = useQuery({
    queryKey: ["branches"],
    queryFn: getBranches,
  });

  const createMutation = useMutation({
    mutationFn: createBranch,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["branches"] });
      setIsOpen(false);
      setFormData({ name: "", street: "", city: "" });
      toast.success(t('admin.branches.successCreate'));
    },
    onError: () => {
      toast.error(t('admin.branches.errorCreate'));
    }
  });

  const updateMutation = useMutation({
    mutationFn: updateBranch,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["branches"] });
      setIsOpen(false);
      setEditingId(null);
      setFormData({ name: "", street: "", city: "" });
      toast.success(t('admin.branches.successUpdate'));
    },
    onError: () => {
      toast.error(t('admin.branches.errorUpdate'));
    }
  });

  const deleteMutation = useMutation({
    mutationFn: deleteBranch,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["branches"] });
      toast.success(t('admin.branches.successDelete'));
    },
  });

  const handleEdit = (branch: BranchDTO) => {
    setEditingId(branch.id || null);
    setFormData({ name: branch.name, street: branch.street, city: branch.city });
    setIsOpen(true);
  };

  const handleCreate = () => {
    setEditingId(null);
    setFormData({ name: "", street: "", city: "" });
    setIsOpen(true);
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    if (editingId) {
      updateMutation.mutate({ id: editingId, branch: formData });
    } else {
      createMutation.mutate(formData);
    }
  };

  if (isLoading) {
    return <TableSkeleton title={t('admin.branches.title')} columnCount={4} />;
  }

  if (error) {
    return (
      <ErrorState 
        variant="fetch"
        title={t('admin.branches.offlineTitle')}
        message={t('admin.branches.offlineMessage')}
        onRetry={() => queryClient.invalidateQueries({ queryKey: ["branches"] })}
      />
    );
  }

  return (
    <Card className="w-full border-2 shadow-2xl rounded-[2rem] md:rounded-[2.5rem] overflow-hidden bg-card">
      <CardHeader className="flex flex-col sm:flex-row items-start sm:items-center justify-between p-6 md:p-8 border-b-2 border-border/50 bg-muted/50/50 gap-4 sm:gap-0">
        <div className="space-y-1">
          <CardTitle className="text-2xl md:text-3xl font-black italic tracking-tighter uppercase text-primary flex items-center gap-3">
            <Store className="h-6 w-6 md:h-8 md:w-8 text-secondary" />
            {t('admin.branches.title')}
          </CardTitle>
          <p className="text-[9px] md:text-[10px] font-bold text-muted-foreground uppercase tracking-widest ml-1">{t('admin.branches.subtitle')}</p>
        </div>
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
          <DialogTrigger asChild>
            <Button size="lg" className="w-full sm:w-auto rounded-xl md:rounded-2xl font-black uppercase text-[10px] md:text-xs tracking-widest px-6 md:px-8 shadow-lg shadow-primary/20 hover:scale-105 transition-all" onClick={handleCreate}>
              <Plus className="mr-2 h-4 w-4 md:h-5 md:w-5 text-secondary" /> {t('admin.branches.new')}
            </Button>
          </DialogTrigger>
          <DialogContent className="rounded-[2rem] md:rounded-[2.5rem] p-0 overflow-hidden border-none shadow-2xl w-[95vw] sm:max-w-[425px]">
            <DialogHeader className="bg-primary text-white p-6 md:p-8 relative overflow-hidden">
               <div className="absolute top-0 right-0 w-32 h-32 bg-secondary/10 rounded-full -mr-16 -mt-16 blur-3xl text-white" />
              <DialogTitle className="text-2xl md:text-3xl font-black italic uppercase tracking-tighter relative z-10">
                {editingId ? t('admin.branches.edit') : t('admin.branches.new')}
              </DialogTitle>
            </DialogHeader>
            <form onSubmit={handleSubmit} className="p-6 md:p-8 space-y-4 md:space-y-6">
              <div className="space-y-2 md:space-y-3">
                <Label htmlFor="name" className="text-[9px] md:text-[10px] font-black uppercase tracking-widest text-primary ml-1">{t('admin.branches.name')}</Label>
                <Input
                  id="name"
                  placeholder={t('admin.branches.placeholderName')}
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  className="h-12 md:h-14 border-2 bg-muted/50 rounded-xl md:rounded-2xl focus-visible:ring-primary/10 text-base md:text-lg font-medium px-4"
                />
              </div>
              <div className="space-y-2 md:space-y-3">
                <Label htmlFor="street" className="text-[9px] md:text-[10px] font-black uppercase tracking-widest text-primary ml-1">{t('admin.branches.street')}</Label>
                <Input
                  id="street"
                  placeholder={t('admin.branches.placeholderStreet')}
                  value={formData.street}
                  onChange={(e) => setFormData({ ...formData, street: e.target.value })}
                  required
                  className="h-12 md:h-14 border-2 bg-muted/50 rounded-xl md:rounded-2xl focus-visible:ring-primary/10 text-base md:text-lg font-medium px-4"
                />
              </div>
              <div className="space-y-2 md:space-y-3">
                <Label htmlFor="city" className="text-[9px] md:text-[10px] font-black uppercase tracking-widest text-primary ml-1">{t('admin.branches.city')}</Label>
                <Input
                  id="city"
                  placeholder={t('admin.branches.placeholderCity')}
                  value={formData.city}
                  onChange={(e) => setFormData({ ...formData, city: e.target.value })}
                  required
                  className="h-12 md:h-14 border-2 bg-muted/50 rounded-xl md:rounded-2xl focus-visible:ring-primary/10 text-base md:text-lg font-medium px-4"
                />
              </div>
              <div className="flex gap-3 md:gap-4 pt-4">
                <Button type="button" variant="ghost" className="flex-1 h-12 md:h-14 rounded-xl md:rounded-2xl font-black uppercase tracking-widest text-[10px] md:text-xs text-muted-foreground hover:text-destructive" onClick={() => setIsOpen(false)}>
                  {t('common.cancel')}
                </Button>
                <Button type="submit" disabled={createMutation.isPending || updateMutation.isPending} className="flex-[2] h-12 md:h-14 rounded-xl md:rounded-2xl font-black uppercase tracking-widest text-[10px] md:text-xs shadow-xl shadow-primary/20 hover:scale-105 transition-all">
                  {(createMutation.isPending || updateMutation.isPending) && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                  {editingId ? t('common.saveChanges') : t('admin.branches.create')}
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
              <TableHead className="px-6 md:px-8 py-4 md:py-5 font-black uppercase text-[9px] md:text-[10px] tracking-[0.2em] text-muted-foreground">{t('admin.branches.name')}</TableHead>
              <TableHead className="py-4 md:py-5 font-black uppercase text-[9px] md:text-[10px] tracking-[0.2em] text-muted-foreground">{t('admin.branches.street')}</TableHead>
              <TableHead className="hidden sm:table-cell py-4 md:py-5 font-black uppercase text-[9px] md:text-[10px] tracking-[0.2em] text-muted-foreground">{t('admin.branches.city')}</TableHead>
              <TableHead className="px-6 md:px-8 py-4 md:py-5 font-black uppercase text-[9px] md:text-[10px] tracking-[0.2em] text-muted-foreground text-right">{t('common.actions')}</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {branches?.map((branch: BranchDTO) => (
              <TableRow key={branch.id} className="group hover:bg-muted/50/50 transition-colors border-b-2 border-border/50 last:border-0">
                <TableCell className="px-6 md:px-8 py-4 md:py-6 font-black text-foreground uppercase tracking-tight italic text-sm md:text-lg">{branch.name}</TableCell>
                <TableCell className="py-4 md:py-6 font-bold text-muted-foreground/80 uppercase text-[10px] md:text-xs">{branch.street}</TableCell>
                <TableCell className="hidden sm:table-cell py-4 md:py-6"><Badge variant="outline" className="font-black text-[8px] md:text-[10px] rounded-lg border-primary/20 text-primary uppercase px-2 tracking-widest">{branch.city}</Badge></TableCell>
                <TableCell className="px-6 md:px-8 py-4 md:py-6 text-right whitespace-nowrap">
                  <div className="flex justify-end gap-1 md:gap-2 opacity-60 group-hover:opacity-100 transition-opacity">
                    <Button
                      variant="outline"
                      size="icon"
                      className="h-10 w-10 md:h-12 md:w-12 border-2 text-primary hover:bg-primary hover:text-white transition-all rounded-xl md:rounded-2xl"
                      onClick={() => handleEdit(branch)}
                    >
                      <Edit2 className="h-4 w-4 md:h-5 md:w-5" />
                    </Button>
                    <ConfirmDialog
                      trigger={
                        <Button
                          variant="outline"
                          size="icon"
                          className="h-10 w-10 md:h-12 md:w-12 border-2 text-destructive hover:bg-destructive hover:text-white transition-all rounded-xl md:rounded-2xl"
                        >
                          <Trash2 className="h-4 w-4 md:h-5 md:w-5" />
                        </Button>
                      }
                      title={t('common.confirmTitle')}
                      description={t('common.confirmDelete')}
                      onConfirm={() => branch.id && deleteMutation.mutate(branch.id)}
                      destructive
                    />
                  </div>
                </TableCell>

              </TableRow>
            ))}
            {branches?.length === 0 && (
              <TableRow>
                <TableCell colSpan={4} className="text-center py-4 text-muted-foreground">
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
