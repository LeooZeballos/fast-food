import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
  DialogClose,
} from "@/components/ui/dialog";
import { Button } from "@/components/ui/button";
import { useTranslation } from "react-i18next";

interface ConfirmDialogProps {
  trigger: React.ReactNode;
  title: string;
  description: string;
  onConfirm: () => void;
  destructive?: boolean;
}

export function ConfirmDialog({
  trigger,
  title,
  description,
  onConfirm,
  destructive,
}: ConfirmDialogProps) {
  const { t } = useTranslation();

  return (
    <Dialog>
      <DialogTrigger asChild>{trigger}</DialogTrigger>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>{title}</DialogTitle>
          <DialogDescription>{description}</DialogDescription>
        </DialogHeader>
        <DialogFooter className="gap-2 sm:gap-0">
          <DialogClose asChild>
            <Button type="button" variant="secondary">
              {t("common.goBack")}
            </Button>
          </DialogClose>
          <DialogClose asChild>
            <Button
              type="button"
              variant={destructive ? "destructive" : "default"}
              onClick={onConfirm}
            >
              {t("common.proceed")}
            </Button>
          </DialogClose>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
