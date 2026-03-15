import { useQuery } from "@tanstack/react-query";
import { getAuditLogs, type AuditLogDTO } from "@/api";
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card";
import { Table, TableBody, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Badge } from "@/components/ui/badge";
import { useTranslation } from "react-i18next";
import { format } from "date-fns";
import { es, enUS } from "date-fns/locale";
import { Loader2, ShieldAlert } from "lucide-react";

const AuditLogList = () => {
  const { t, i18n } = useTranslation();
  const dateLocale = i18n.language === 'es' ? es : enUS;

  const { data: logs, isLoading, error } = useQuery({
    queryKey: ["audit-logs"],
    queryFn: getAuditLogs,
    refetchInterval: 30000, // Refresh every 30 seconds
  });

  const getActionColor = (action: string) => {
    if (action.includes("DELETE")) return "destructive";
    if (action.includes("CREATE")) return "default";
    if (action.includes("UPDATE") || action.includes("RESTOCK")) return "secondary";
    return "outline";
  };

  if (isLoading) {
    return (
      <div className="flex justify-center p-8">
        <Loader2 className="h-8 w-8 animate-spin text-primary" />
      </div>
    );
  }

  if (error) {
    return (
      <Card className="border-destructive">
        <CardContent className="pt-6">
          <div className="flex items-center gap-2 text-destructive">
            <ShieldAlert className="h-5 w-5" />
            <p>{t('audit.error_loading', 'Error loading audit logs')}</p>
          </div>
        </CardContent>
      </Card>
    );
  }

  return (
    <Card className="w-full">
      <CardHeader>
        <CardTitle className="text-2xl font-bold flex items-center gap-2">
          {t('audit.title', 'System Audit Trail')}
        </CardTitle>
        <CardDescription>
          {t('audit.description', 'History of security and business critical actions')}
        </CardDescription>
      </CardHeader>
      <CardContent>
        <div className="rounded-md border max-h-[600px] overflow-auto">
          <Table>
            <TableHeader className="sticky top-0 bg-background z-10">
              <TableRow>
                <TableHead className="w-[180px]">{t('audit.timestamp', 'Timestamp')}</TableHead>
                <TableHead>{t('audit.user', 'User')}</TableHead>
                <TableHead>{t('audit.action', 'Action')}</TableHead>
                <TableHead className="max-w-[300px]">{t('audit.details', 'Details')}</TableHead>
              </TableRow>
            </TableHeader>
            <TableBody>
              {logs?.length === 0 ? (
                <TableRow>
                  <TableCell colSpan={4} className="text-center py-8 text-muted-foreground">
                    {t('audit.no_logs', 'No audit logs found')}
                  </TableCell>
                </TableRow>
              ) : (
                logs?.map((log: AuditLogDTO) => (
                  <TableRow key={log.id}>
                    <TableCell className="font-mono text-xs whitespace-nowrap">
                      {format(new Date(log.timestamp), "MMM d, HH:mm:ss", { locale: dateLocale })}
                    </TableCell>
                    <TableCell className="font-medium">
                      <Badge variant="outline">{log.username}</Badge>
                    </TableCell>
                    <TableCell>
                      <Badge variant={getActionColor(log.action) as any}>
                        {log.action}
                      </Badge>
                    </TableCell>
                    <TableCell className="text-xs text-muted-foreground break-all">
                      {log.details}
                    </TableCell>
                  </TableRow>
                ))
              )}
            </TableBody>
          </Table>
        </div>
      </CardContent>
    </Card>
  );
};

export default AuditLogList;
