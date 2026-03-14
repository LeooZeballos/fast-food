import { Button } from "@/components/ui/button";
import { Card } from "@/components/ui/card";
import { AlertCircle, RefreshCcw, WifiOff, Home } from "lucide-react";
import { useTranslation } from "react-i18next";

interface ErrorStateProps {
  title?: string;
  message?: string;
  onRetry?: () => void;
  variant?: "fetch" | "general" | "connection";
}

export function ErrorState({ 
  title, 
  message, 
  onRetry, 
  variant = "fetch" 
}: ErrorStateProps) {
  const { t } = useTranslation();

  const config = {
    fetch: {
      icon: <AlertCircle className="h-12 w-12 text-destructive" />,
      defaultTitle: "Data Retrieval Failed",
      defaultMessage: "We couldn't fetch the requested information. The server might be temporarily busy."
    },
    connection: {
      icon: <WifiOff className="h-12 w-12 text-destructive" />,
      defaultTitle: "Connection Lost",
      defaultMessage: "It looks like you're offline or the backend service is unreachable."
    },
    general: {
      icon: <AlertCircle className="h-12 w-12 text-secondary" />,
      defaultTitle: "Something went wrong",
      defaultMessage: "An unexpected error occurred. Please try again or contact support."
    }
  }[variant];

  return (
    <div className="flex items-center justify-center min-h-[400px] p-6 animate-in fade-in zoom-in-95 duration-500">
      <Card className="max-w-md w-full border-2 border-dashed border-border bg-card/50 backdrop-blur-sm p-10 rounded-[3rem] text-center shadow-xl">
        <div className="flex flex-col items-center gap-6">
          <div className="p-6 bg-card rounded-3xl shadow-lg border-2 border-border/50 rotate-3 group-hover:rotate-0 transition-transform duration-500">
            {config.icon}
          </div>
          
          <div className="space-y-2">
            <h3 className="text-2xl font-black uppercase italic tracking-tighter text-foreground leading-tight">
              {title || config.defaultTitle}
            </h3>
            <p className="text-xs font-bold text-muted-foreground uppercase tracking-widest leading-relaxed">
              {message || config.defaultMessage}
            </p>
          </div>

          <div className="flex flex-col sm:flex-row gap-3 w-full pt-4">
            {onRetry && (
              <Button 
                onClick={onRetry}
                className="flex-1 h-14 bg-primary text-white font-black uppercase tracking-widest text-[10px] rounded-2xl shadow-xl shadow-primary/20 hover:scale-[1.02] active:scale-[0.98] transition-all"
              >
                <RefreshCcw className="mr-2 h-4 w-4 text-secondary" />
                Retry Connection
              </Button>
            )}
            <Button 
              variant="outline"
              onClick={() => window.location.reload()}
              className="flex-1 h-14 border-2 border-border bg-card font-black uppercase tracking-widest text-[10px] rounded-2xl hover:bg-muted/50 transition-all"
            >
              <RefreshCcw className="mr-2 h-4 w-4 text-muted-foreground" />
              Reload Page
            </Button>
          </div>
        </div>
      </Card>
    </div>
  );
}
