import { useState } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { 
  getOrders, 
  startPreparation, 
  finishPreparation, 
  confirmPayment, 
  cancelOrder, 
  rejectOrder
} from "@/api";
import type { FoodOrderDTO } from "@/api";
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
import { Badge } from "@/components/ui/badge";
import {
  Select,
  SelectContent,
  SelectItem,
  SelectTrigger,
  SelectValue,
} from "@/components/ui/select";
import { Play, CheckCircle, CreditCard, XCircle, RotateCcw } from "lucide-react";

export function OrderList() {
  const [filter, setFilter] = useState("all");
  const queryClient = useQueryClient();

  const { data: orders, isLoading } = useQuery({
    queryKey: ["orders", filter],
    queryFn: () => getOrders(filter),
  });

  const mutationOptions = {
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["orders"] });
    },
  };

  const startMutation = useMutation({ mutationFn: startPreparation, ...mutationOptions });
  const finishMutation = useMutation({ mutationFn: finishPreparation, ...mutationOptions });
  const payMutation = useMutation({ mutationFn: confirmPayment, ...mutationOptions });
  const cancelMutation = useMutation({ mutationFn: cancelOrder, ...mutationOptions });
  const rejectMutation = useMutation({ mutationFn: rejectOrder, ...mutationOptions });

  const getStatusBadge = (state: string) => {
    switch (state) {
      case "Created": return <Badge variant="secondary">{state}</Badge>;
      case "Inpreparation": return <Badge className="bg-blue-500">{state}</Badge>;
      case "Done": return <Badge className="bg-green-500">{state}</Badge>;
      case "Paid": return <Badge variant="outline" className="border-green-500 text-green-500">{state}</Badge>;
      case "Cancelled": return <Badge variant="destructive">{state}</Badge>;
      case "Rejected": return <Badge variant="destructive">{state}</Badge>;
      default: return <Badge>{state}</Badge>;
    }
  };

  if (isLoading) return <div>Loading orders...</div>;

  return (
    <Card className="w-full max-w-5xl mx-auto mt-8">
      <CardHeader className="flex flex-row items-center justify-between">
        <CardTitle className="text-2xl font-bold">Orders Management</CardTitle>
        <div className="w-[180px]">
          <Select value={filter} onValueChange={setFilter}>
            <SelectTrigger>
              <SelectValue placeholder="Filter by status" />
            </SelectTrigger>
            <SelectContent>
              <SelectItem value="all">All Orders</SelectItem>
              <SelectItem value="created">Created</SelectItem>
              <SelectItem value="in_preparation">In Preparation</SelectItem>
              <SelectItem value="finished">Finished (Done)</SelectItem>
              <SelectItem value="paid">Paid</SelectItem>
            </SelectContent>
          </Select>
        </div>
      </CardHeader>
      <CardContent>
        <Table>
          <TableHeader>
            <TableRow>
              <TableHead>ID</TableHead>
              <TableHead>Branch</TableHead>
              <TableHead>Status</TableHead>
              <TableHead>Details</TableHead>
              <TableHead>Total</TableHead>
              <TableHead className="text-right">Actions</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {orders?.map((order: FoodOrderDTO) => (
              <TableRow key={order.id}>
                <TableCell className="font-mono text-xs">#{order.id}</TableCell>
                <TableCell>{order.branchName}</TableCell>
                <TableCell>{getStatusBadge(order.formattedState)}</TableCell>
                <TableCell className="max-w-[200px] truncate text-sm text-muted-foreground">
                  {order.formattedFoodOrderDetails}
                </TableCell>
                <TableCell className="font-bold">{order.formattedTotal}</TableCell>
                <TableCell className="text-right">
                  <div className="flex justify-end gap-2">
                    {order.formattedState === "Created" && (
                      <>
                        <Button size="sm" onClick={() => order.id && startMutation.mutate(order.id)}>
                          <Play className="mr-1 h-3 w-3" /> Start
                        </Button>
                        <Button size="sm" variant="destructive" onClick={() => order.id && cancelMutation.mutate(order.id)}>
                          <XCircle className="mr-1 h-3 w-3" /> Cancel
                        </Button>
                      </>
                    )}
                    {order.formattedState === "Inpreparation" && (
                      <>
                        <Button size="sm" className="bg-green-600 hover:bg-green-700" onClick={() => order.id && finishMutation.mutate(order.id)}>
                          <CheckCircle className="mr-1 h-3 w-3" /> Finish
                        </Button>
                        <Button size="sm" variant="destructive" onClick={() => order.id && cancelMutation.mutate(order.id)}>
                          <XCircle className="mr-1 h-3 w-3" /> Cancel
                        </Button>
                      </>
                    )}
                    {order.formattedState === "Done" && (
                      <>
                        <Button size="sm" className="bg-blue-600 hover:bg-blue-700" onClick={() => order.id && payMutation.mutate(order.id)}>
                          <CreditCard className="mr-1 h-3 w-3" /> Pay
                        </Button>
                        <Button size="sm" variant="destructive" onClick={() => order.id && rejectMutation.mutate(order.id)}>
                          <RotateCcw className="mr-1 h-3 w-3" /> Reject
                        </Button>
                      </>
                    )}
                  </div>
                </TableCell>
              </TableRow>
            ))}
            {orders?.length === 0 && (
              <TableRow>
                <TableCell colSpan={6} className="text-center py-8 text-muted-foreground">
                  No orders found for this filter.
                </TableCell>
              </TableRow>
            )}
          </TableBody>
        </Table>
      </CardContent>
    </Card>
  );
}
