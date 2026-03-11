import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getBranches, deleteBranch, createBranch, updateBranch } from "@/api";
import type { BranchDTO } from "@/api";
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

export function BranchList() {
  const queryClient = useQueryClient();
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
      toast.success("Branch created successfully");
    },
    onError: () => {
      toast.error("Error creating branch");
    }
  });

  const updateMutation = useMutation({
    mutationFn: updateBranch,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["branches"] });
      setIsOpen(false);
      setEditingId(null);
      setFormData({ name: "", street: "", city: "" });
      toast.success("Branch updated successfully");
    },
    onError: () => {
      toast.error("Error updating branch");
    }
  });

  const deleteMutation = useMutation({
    mutationFn: deleteBranch,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["branches"] });
      toast.success("Branch deleted successfully");
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
    return <TableSkeleton title="Branches Management" columnCount={4} />;
  }

  if (error) return <div>Error loading branches</div>;

  return (
    <Card className="w-full border-2 shadow-2xl rounded-[2.5rem] overflow-hidden bg-white">
      <CardHeader className="flex flex-row items-center justify-between p-8 border-b-2 border-slate-50 bg-slate-50/50">
        <div className="space-y-1">
          <CardTitle className="text-3xl font-black italic tracking-tighter uppercase text-primary flex items-center gap-3">
            <Store className="h-8 w-8 text-secondary" />
            Branches
          </CardTitle>
          <p className="text-[10px] font-bold text-slate-400 uppercase tracking-widest ml-1">Location management</p>
        </div>
        <Dialog open={isOpen} onOpenChange={setIsOpen}>
          <DialogTrigger asChild>
            <Button size="lg" className="rounded-2xl font-black uppercase text-xs tracking-widest px-8 shadow-lg shadow-primary/20 hover:scale-105 transition-all" onClick={handleCreate}>
              <Plus className="mr-2 h-5 w-5 text-secondary" /> New Branch
            </Button>
          </DialogTrigger>
          <DialogContent className="rounded-[2.5rem] p-0 overflow-hidden border-none shadow-2xl sm:max-w-[425px]">
            <DialogHeader className="bg-primary text-white p-8 relative overflow-hidden">
               <div className="absolute top-0 right-0 w-32 h-32 bg-secondary/10 rounded-full -mr-16 -mt-16 blur-3xl text-white" />
              <DialogTitle className="text-3xl font-black italic uppercase tracking-tighter relative z-10">
                {editingId ? "Edit Branch" : "Add New Branch"}
              </DialogTitle>
            </DialogHeader>
            <form onSubmit={handleSubmit} className="p-8 space-y-6">
              <div className="space-y-3">
                <Label htmlFor="name" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">Branch Name</Label>
                <Input
                  id="name"
                  placeholder="e.g. Downtown Branch"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  required
                  className="h-14 border-2 bg-slate-50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4"
                />
              </div>
              <div className="space-y-3">
                <Label htmlFor="street" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">Street</Label>
                <Input
                  id="street"
                  placeholder="e.g. 123 Main St"
                  value={formData.street}
                  onChange={(e) => setFormData({ ...formData, street: e.target.value })}
                  required
                  className="h-14 border-2 bg-slate-50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4"
                />
              </div>
              <div className="space-y-3">
                <Label htmlFor="city" className="text-[10px] font-black uppercase tracking-widest text-primary ml-1">City</Label>
                <Input
                  id="city"
                  placeholder="e.g. Springfield"
                  value={formData.city}
                  onChange={(e) => setFormData({ ...formData, city: e.target.value })}
                  required
                  className="h-14 border-2 bg-slate-50 rounded-2xl focus-visible:ring-primary/10 text-lg font-medium px-4"
                />
              </div>
              <div className="flex gap-4 pt-4">
                <Button type="button" variant="ghost" className="flex-1 h-14 rounded-2xl font-black uppercase tracking-widest text-xs text-slate-400 hover:text-destructive" onClick={() => setIsOpen(false)}>
                  Cancel
                </Button>
                <Button type="submit" disabled={createMutation.isPending || updateMutation.isPending} className="flex-[2] h-14 rounded-2xl font-black uppercase tracking-widest text-xs shadow-xl shadow-primary/20 hover:scale-105 transition-all">
                  {(createMutation.isPending || updateMutation.isPending) && <Loader2 className="mr-2 h-4 w-4 animate-spin" />}
                  {editingId ? "Save Changes" : "Create Branch"}
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
              <TableHead className="px-8 py-5 font-black uppercase text-[10px] tracking-[0.2em] text-slate-400">Branch Name</TableHead>
              <TableHead className="py-5 font-black uppercase text-[10px] tracking-[0.2em] text-slate-400">Street</TableHead>
              <TableHead className="py-5 font-black uppercase text-[10px] tracking-[0.2em] text-slate-400">City</TableHead>
              <TableHead className="px-8 py-5 font-black uppercase text-[10px] tracking-[0.2em] text-slate-400 text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {branches?.map((branch: BranchDTO) => (
              <TableRow key={branch.id} className="group hover:bg-slate-50/50 transition-colors border-b-2 border-slate-50 last:border-0">
                <TableCell className="px-8 py-6 font-black text-slate-900 uppercase tracking-tight italic text-lg">{branch.name}</TableCell>
                <TableCell className="py-6 font-bold text-slate-500 uppercase text-xs">{branch.street}</TableCell>
                <TableCell className="py-6"><Badge variant="outline" className="font-black text-[10px] rounded-lg border-primary/20 text-primary uppercase px-2 tracking-widest">{branch.city}</Badge></TableCell>
                <TableCell className="px-8 py-6 text-right whitespace-nowrap">
                   <div className="flex justify-end gap-2">
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-12 w-12 text-slate-200 hover:text-primary hover:bg-primary/5 transition-all rounded-2xl group-hover:text-slate-300"
                      onClick={() => handleEdit(branch)}
                    >
                      <Edit2 className="h-5 w-5" />
                    </Button>
                    <Button
                      variant="ghost"
                      size="icon"
                      className="h-12 w-12 text-slate-200 hover:text-destructive hover:bg-destructive/5 transition-all rounded-2xl group-hover:text-slate-300"
                      onClick={() => branch.id && deleteMutation.mutate(branch.id)}
                    >
                      <Trash2 className="h-5 w-5" />
                    </Button>
                  </div>
                </TableCell>
              </TableRow>
            ))}
            {branches?.length === 0 && (
              <TableRow>
                <TableCell colSpan={4} className="text-center py-4 text-muted-foreground">
                  No branches found.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
