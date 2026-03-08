import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { getMenus, deleteMenu, toggleMenuStatus } from "@/api";
import type { MenuDTO } from "@/api";
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
import { Trash2, Plus } from "lucide-react";

export function MenuList() {
  const queryClient = useQueryClient();
  const { data: menus, isLoading, error } = useQuery({
    queryKey: ["menus"],
    queryFn: getMenus,
  });

  const deleteMutation = useMutation({
    mutationFn: deleteMenu,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["menus"] });
    },
  });

  const toggleMutation = useMutation({
    mutationFn: toggleMenuStatus,
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["menus"] });
    },
  });

  if (isLoading) return <div>Loading menus...</div>;
  if (error) return <div>Error loading menus</div>;

  return (
    <Card className="w-full max-w-4xl mx-auto mt-8">
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle className="text-2xl font-bold">Menus Management</CardTitle>
        <Button size="sm">
          <Plus className="mr-2 h-4 w-4" /> New Menu
        </Button>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>Name</TableHead>
              <TableHead>Price</TableHead>
              <TableHead>Discount</TableHead>
              <TableHead>Products</TableHead>
              <TableHead>Active</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {menus?.map((menu: MenuDTO) => (
              <TableRow key={menu.id}>
                <TableCell className="font-medium">{menu.name}</TableCell>
                <TableCell>{menu.formattedPrice || `$${menu.price.toFixed(2)}`}</TableCell>
                <TableCell>{menu.formattedDiscount || `${menu.discountPercentage}%`}</TableCell>
                <TableCell className="max-w-xs truncate">{menu.productsList}</TableCell>
                <TableCell>
                  <Switch
                    checked={menu.active}
                    onCheckedChange={() => 
                      menu.id && toggleMutation.mutate({ id: menu.id, active: menu.active })
                    }
                  />
                </TableCell>
                <TableCell className="text-right">
                  <Button
                    variant="destructive"
                    size="icon"
                    onClick={() => menu.id && deleteMutation.mutate(menu.id)}
                  >
                    <Trash2 className="h-4 w-4" />
                  </Button>
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
