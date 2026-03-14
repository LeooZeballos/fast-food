import { useState, useEffect } from "react";
import { useQuery, useMutation, useQueryClient } from "@tanstack/react-query";
import { useTranslation } from "react-i18next";
import { 
  getOrders, 
  startPreparation, 
  finishPreparation, 
  confirmPayment, 
  cancelOrder, 
  rejectOrder
} from "@/api";
import type { FoodOrderDTO } from "@/api";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardFooter } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import { 
  Play, 
  CheckCircle, 
  CreditCard, 
  XCircle, 
  RotateCcw, 
  Clock, 
  Store, 
  ChefHat, 
  PackageCheck, 
  Ban, 
  History
} from "lucide-react";
import { cn } from "@/lib/utils";

import { ErrorState } from "@/components/ui/error-state";

function TimeAgo({ timestamp }: { timestamp: string }) {
  const { t } = useTranslation();
  const [minutes, setMinutes] = useState(0);

  useEffect(() => {
    const calculate = () => {
      const start = new Date(timestamp).getTime();
      const now = new Date().getTime();
      setMinutes(Math.floor((now - start) / 60000));
    };
    calculate();
    const interval = setInterval(calculate, 30000);
    return () => clearInterval(interval);
  }, [timestamp]);

  return (
    <div className={cn(
      "flex items-center gap-1.5 text-[10px] font-black uppercase tracking-widest px-2 py-1 rounded-md",
      minutes > 15 ? "text-destructive bg-destructive/10 animate-pulse" : 
      minutes > 5 ? "text-secondary bg-secondary/10" : "text-primary/40 bg-muted/50"
    )}>
      <Clock className="h-3 w-3" />
      {minutes === 0 ? t('common.justNow') : t('common.minutesAgo', { count: minutes })}
    </div>
  );
}

function OrderCard({ 
  order, 
  onAction 
}: { 
  order: FoodOrderDTO; 
  onAction: {
    start: (id: number) => void;
    finish: (id: number) => void;
    pay: (id: number) => void;
    cancel: (id: number) => void;
    reject: (id: number) => void;
  }
}) {
  const { t } = useTranslation();
  const config = {
    "Created": { color: "bg-muted/500", icon: <History className="h-4 w-4" />, label: t('kitchen.actions.start').toUpperCase() },
    "Inpreparation": { color: "bg-blue-500", icon: <ChefHat className="h-4 w-4" />, label: t('kitchen.actions.ready').toUpperCase() },
    "Done": { color: "bg-orange-500", icon: <PackageCheck className="h-4 w-4" />, label: t('kitchen.states.ready') },
    "Paid": { color: "bg-green-600", icon: <CreditCard className="h-4 w-4" />, label: t('kitchen.states.paid') },
    "Cancelled": { color: "bg-destructive", icon: <Ban className="h-4 w-4" />, label: t('kitchen.states.void') },
    "Rejected": { color: "bg-destructive", icon: <RotateCcw className="h-4 w-4" />, label: t('kitchen.states.rej') },
  }[order.formattedState] || { color: "bg-muted/500", icon: null, label: order.formattedState };

  return (
    <Card className="group border-2 bg-card rounded-[2rem] overflow-hidden shadow-sm hover:shadow-xl transition-all select-none flex flex-col relative mb-4">
      <CardHeader className="p-5 pb-0 space-y-3">
        <div className="flex justify-between items-start">
          <div className="space-y-0.5">
            <p className="font-mono text-[9px] font-black text-muted-foreground/60">#{order.id}</p>
            <div className="flex items-center gap-1.5 text-primary">
              <Store className="h-2.5 w-2.5 text-secondary" />
              <span className="text-[9px] font-black uppercase tracking-widest truncate max-w-[80px]">{order.branchName}</span>
            </div>
          </div>
          <TimeAgo timestamp={order.creationTimestamp} />
        </div>
      </CardHeader>
      
      <CardContent className="p-5 pt-3 flex-grow">
        <div className="space-y-3">
          <p className="text-[11px] font-black text-foreground leading-tight uppercase italic line-clamp-3">
            {order.formattedFoodOrderDetails}
          </p>
          <div className="flex justify-between items-center pt-3 border-t-2 border-border/50">
            <p className="text-lg font-black text-secondary tracking-tighter">{order.formattedTotal}</p>
            <Badge className={cn("rounded-lg px-2 py-0.5 font-black text-[8px] tracking-[0.15em] border-none text-white", config.color)}>
              {config.label}
            </Badge>
          </div>
        </div>
      </CardContent>

      <CardFooter className="p-5 pt-0 mt-auto">
        <div className="grid grid-cols-1 gap-2 w-full">
          {order.formattedState === "Created" && (
            <div className="flex gap-2">
              <Button className="flex-grow h-11 bg-primary text-white font-black uppercase tracking-tighter italic rounded-xl group hover:scale-[1.02] active:scale-[0.98] transition-all text-xs" onClick={() => order.id && onAction.start(order.id)}>
                <Play className="mr-2 h-4 w-4 text-secondary" /> {t('kitchen.actions.start')}
              </Button>
              <Button variant="ghost" className="w-11 h-11 p-0 text-destructive hover:bg-destructive/5 rounded-xl border-2 border-transparent hover:border-destructive/20" onClick={() => order.id && onAction.cancel(order.id)}>
                <XCircle className="h-5 w-5" />
              </Button>
            </div>
          )}
          {order.formattedState === "Inpreparation" && (
            <div className="flex gap-2">
              <Button className="flex-grow h-11 bg-green-600 hover:bg-green-700 text-white font-black uppercase tracking-tighter italic rounded-xl group hover:scale-[1.02] active:scale-[0.98] transition-all text-xs" onClick={() => order.id && onAction.finish(order.id)}>
                <CheckCircle className="mr-2 h-4 w-4" /> {t('kitchen.actions.ready')}
              </Button>
              <Button variant="ghost" className="w-11 h-11 p-0 text-destructive hover:bg-destructive/5 rounded-xl border-2 border-transparent hover:border-destructive/20" onClick={() => order.id && onAction.cancel(order.id)}>
                <XCircle className="h-5 w-5" />
              </Button>
            </div>
          )}
          {order.formattedState === "Done" && (
            <div className="flex gap-2">
              <Button className="flex-grow h-11 bg-blue-600 hover:bg-blue-700 text-white font-black uppercase tracking-tighter italic rounded-xl group hover:scale-[1.02] active:scale-[0.98] transition-all text-xs" onClick={() => order.id && onAction.pay(order.id)}>
                <CreditCard className="mr-2 h-4 w-4" /> {t('kitchen.actions.pay')}
              </Button>
              <Button variant="ghost" className="w-11 h-11 p-0 text-destructive hover:bg-destructive/5 rounded-xl border-2 border-transparent hover:border-destructive/20" onClick={() => order.id && onAction.reject(order.id)}>
                <RotateCcw className="h-5 w-5" />
              </Button>
            </div>
          )}
        </div>
      </CardFooter>
    </Card>
  );
}

export function OrderList() {
  const queryClient = useQueryClient();
  const { t } = useTranslation();
  const { data: orders, isLoading } = useQuery({
    queryKey: ["orders", "all"],
    queryFn: () => getOrders("all"),
    refetchInterval: 5000,
  });

  const mutationOptions = {
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ["orders"] });
    },
  };

  const actions = {
    start: useMutation({ mutationFn: startPreparation, ...mutationOptions }).mutate,
    finish: useMutation({ mutationFn: finishPreparation, ...mutationOptions }).mutate,
    pay: useMutation({ mutationFn: confirmPayment, ...mutationOptions }).mutate,
    cancel: useMutation({ mutationFn: cancelOrder, ...mutationOptions }).mutate,
    reject: useMutation({ mutationFn: rejectOrder, ...mutationOptions }).mutate,
  };

  if (isLoading) {
    return (
      <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mt-8 animate-pulse">
        {[1, 2, 3].map(i => (
          <div key={i} className="space-y-4">
            <div className="h-12 bg-muted rounded-2xl w-1/2" />
            {[1, 2].map(j => <div key={j} className="h-48 bg-card border-2 rounded-[2rem]" />)}
          </div>
        ))}
      </div>
    );
  }

  if (orders === undefined) {
    return (
      <ErrorState 
        variant="connection"
        title={t('kitchen.offline')}
        message={t('kitchen.offlineMessage')}
        onRetry={() => queryClient.invalidateQueries({ queryKey: ["orders"] })}
      />
    );
  }

  const columns = [
    { id: "Created", label: t('kitchen.columns.new'), color: "text-muted-foreground", icon: <History className="h-5 w-5" /> },
    { id: "Inpreparation", label: t('kitchen.columns.prep'), color: "text-blue-500", icon: <ChefHat className="h-5 w-5" /> },
    { id: "Done", label: t('kitchen.columns.ready'), color: "text-orange-500", icon: <PackageCheck className="h-5 w-5" /> },
  ];

  return (
    <div className="flex flex-col gap-10 mt-4 animate-in fade-in duration-700 mb-20">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center gap-6 bg-card p-8 rounded-[2.5rem] border-2 shadow-sm relative overflow-hidden">
        <div className="absolute top-0 left-0 w-2 h-full bg-primary" />
        <div className="space-y-1">
          <h2 className="text-4xl font-black italic tracking-tighter uppercase text-primary flex items-center gap-4">
            <ChefHat className="h-10 w-10 text-secondary" /> {t('kitchen.kds')}
          </h2>
          <p className="text-muted-foreground font-bold uppercase text-[10px] tracking-[0.2em]">{t('kitchen.visualWorkflow')}</p>
        </div>
        <div className="flex items-center gap-3 bg-muted/50 px-4 py-2 rounded-2xl border-2">
          <div className="w-3 h-3 bg-green-500 rounded-full animate-pulse" />
          <span className="text-[10px] font-black uppercase tracking-widest text-muted-foreground/80">{t('kitchen.liveFeed')}</span>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-8 items-start">
        {columns.map(column => (
          <div key={column.id} className="flex flex-col gap-6">
            <div className="flex items-center justify-between px-4">
              <div className="flex items-center gap-3">
                <span className={cn("p-2 rounded-xl bg-card border-2", column.color)}>{column.icon}</span>
                <h3 className="font-black italic uppercase tracking-tighter text-xl text-foreground">{column.label}</h3>
              </div>
              <Badge variant="secondary" className="rounded-lg font-black text-xs px-2.5 py-0.5 bg-muted text-muted-foreground">
                {orders?.filter(o => o.formattedState === column.id).length || 0}
              </Badge>
            </div>
            
            <div className="flex flex-col min-h-[500px] bg-card rounded-[2.5rem] p-4 border-2 border-border shadow-sm relative">
              {orders?.filter(o => o.formattedState === column.id).map(order => (
                <OrderCard key={order.id} order={order} onAction={actions} />
              ))}
              {orders?.filter(o => o.formattedState === column.id).length === 0 && (
                <div className="flex-grow flex flex-col items-center justify-center opacity-20 py-12">
                  <PackageCheck className="h-12 w-12 mb-2" />
                  <p className="text-[10px] font-black uppercase tracking-widest">{t('common.clear')}</p>
                </div>
              )}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}
