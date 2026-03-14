import React, { useState } from "react";
import { useAuth } from "../AuthContext";
import { Button } from "./ui/button";
import { Input } from "./ui/input";
import { Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle } from "./ui/card";
import { Label } from "./ui/label";
import { toast } from "sonner";
import { BurgerIcon } from "./ui/burger-icon";
import { Languages } from "lucide-react";
import { useTranslation } from "react-i18next";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "./ui/select";
import { useTheme } from "../ThemeContext";
import { Sun, Moon } from "lucide-react";

export const Login: React.FC = () => {
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuth();
  const { t, i18n } = useTranslation();
  const { theme, setTheme } = useTheme();

  const handleLanguageChange = (lang: string) => {
    i18n.changeLanguage(lang);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsLoading(true);
    try {
      await login(username, password);
      toast.success(t('login.success'));
    } catch (error) {
      toast.error(t('login.error'));
      console.error("Login failed", error);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-background flex flex-col items-center justify-center p-4 relative overflow-hidden transition-colors duration-500">
      {/* Catchy Animated Background */}
      <div className="absolute inset-0 z-0 overflow-hidden pointer-events-none">
        {/* Animated Blobs */}
        <div className="absolute top-[-10%] -right-[10%] w-[50%] h-[50%] bg-primary/10 dark:bg-primary/20 rounded-full blur-3xl animate-blob" />
        <div className="absolute bottom-[-10%] -left-[10%] w-[50%] h-[50%] bg-secondary/10 dark:bg-secondary/20 rounded-full blur-3xl animate-blob [animation-delay:2s]" />
        <div className="absolute top-[20%] left-[10%] w-[30%] h-[30%] bg-primary/5 dark:bg-primary/10 rounded-full blur-3xl animate-blob [animation-delay:4s]" />

        {/* Floating Icons */}
        <div className="absolute top-20 left-[15%] opacity-10 animate-float">
          <BurgerIcon className="w-32 h-32 rotate-12" />
        </div>
        <div className="absolute bottom-40 right-[10%] opacity-10 animate-float-delayed">
          <BurgerIcon className="w-48 h-48 -rotate-12" />
        </div>
        <div className="absolute top-[60%] left-[5%] opacity-5 animate-float [animation-delay:1s]">
          <BurgerIcon className="w-24 h-24 rotate-45" />
        </div>
        <div className="absolute top-10 right-[25%] opacity-5 animate-float-delayed [animation-delay:3s]">
          <BurgerIcon className="w-20 h-20 -rotate-45" />
        </div>
      </div>

      {/* Top Right Controls */}
      <div className="absolute top-8 right-8 z-50 flex items-center gap-3 animate-in fade-in slide-in-from-top-4 duration-1000">
        <button
          onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
          className="flex items-center justify-center w-12 h-12 bg-card/80 backdrop-blur-md rounded-2xl border-2 hover:bg-card transition-all shadow-sm group"
        >
          {theme === "dark" ? (
            <Sun className="h-5 w-5 text-primary group-hover:rotate-90 transition-transform duration-500" />
          ) : (
            <Moon className="h-5 w-5 text-secondary group-hover:-rotate-12 transition-transform duration-500" />
          )}
        </button>

        <Select value={i18n.language} onValueChange={handleLanguageChange}>
          <SelectTrigger className="h-12 border-2 bg-card/80 backdrop-blur-md rounded-2xl focus:ring-primary/10 px-4 min-w-[140px] shadow-sm hover:shadow-md transition-all">
            <div className="flex items-center gap-3">
              <Languages className="h-4 w-4 text-secondary" />
              <SelectValue />
            </div>
          </SelectTrigger>
          <SelectContent className="rounded-2xl border-2">
            <SelectItem value="en" className="py-3 font-black text-[10px] uppercase tracking-widest cursor-pointer">🇺🇸 English</SelectItem>
            <SelectItem value="es" className="py-3 font-black text-[10px] uppercase tracking-widest cursor-pointer">🇪🇸 Español</SelectItem>
          </SelectContent>
        </Select>
      </div>

      <div className="w-full max-w-md space-y-8 animate-in fade-in zoom-in duration-500 relative z-10">
        <div className="flex flex-col items-center gap-2 mb-8">
          <div className="bg-primary p-4 rounded-[2rem] shadow-2xl shadow-primary/30 rotate-3 hover:rotate-0 transition-transform duration-500">
            <BurgerIcon className="w-12 h-12" />
          </div>
          <div className="text-center mt-4">
            <h1 className="text-4xl font-black text-foreground tracking-tight italic uppercase leading-none">
              FastFood<span className="text-primary">OS</span>
            </h1>
            <p className="text-muted-foreground font-bold uppercase text-[10px] tracking-[0.3em] mt-2">{t('login.signInToContinue')}</p>
          </div>
        </div>

        <Card className="border-2 shadow-[0_32px_64px_-12px_rgba(0,0,0,0.14)] rounded-[3rem] overflow-hidden bg-card/90 backdrop-blur-sm">
          <CardHeader className="space-y-1 p-10 pb-6 bg-muted/50 border-b-2 border-border/50">
            <CardTitle className="text-3xl font-black uppercase italic tracking-tighter text-primary leading-none">{t('login.welcomeBack')}</CardTitle>
            <CardDescription className="text-[10px] font-black uppercase tracking-[0.15em] text-muted-foreground">{t('login.enterCredentials')}</CardDescription>
          </CardHeader>
          <form onSubmit={handleSubmit}>
            <CardContent className="space-y-6 p-10">
              <div className="space-y-3">
                <Label htmlFor="username" className="text-[10px] font-black uppercase tracking-widest text-primary/60 ml-2">{t('login.username')}</Label>
                <Input
                  id="username"
                  placeholder="admin"
                  value={username}
                  onChange={(e) => setUsername(e.target.value)}
                  className="rounded-2xl border-2 border-border bg-card focus-visible:ring-primary/10 h-14 text-lg font-medium px-6 shadow-sm focus:border-primary/20 transition-all"
                  required
                />
              </div>
              <div className="space-y-3">
                <Label htmlFor="password" className="text-[10px] font-black uppercase tracking-widest text-primary/60 ml-2">{t('login.password')}</Label>
                <Input
                  id="password"
                  type="password"
                  placeholder="••••••••"
                  value={password}
                  onChange={(e) => setPassword(e.target.value)}
                  className="rounded-2xl border-2 border-border bg-card focus-visible:ring-primary/10 h-14 text-lg font-medium px-6 shadow-sm focus:border-primary/20 transition-all"
                  required
                />
              </div>
            </CardContent>
            <CardFooter className="p-10 pt-0">
              <Button
                type="submit"
                className="w-full rounded-[1.5rem] h-20 font-black uppercase italic tracking-tighter text-2xl shadow-2xl shadow-primary/30 hover:scale-[1.02] active:scale-[0.98] transition-all bg-primary text-white group relative overflow-hidden"
                disabled={isLoading}
              >
                <span className="relative z-10 flex items-center justify-center gap-3">
                  {isLoading ? t('login.signingIn') : t('login.signIn')}
                </span>
                <div className="absolute inset-0 bg-card/10 translate-y-full group-hover:translate-y-0 transition-transform duration-500" />
              </Button>
            </CardFooter>
          </form>
        </Card>
        
        <p className="text-center text-[10px] font-black text-muted-foreground uppercase tracking-[0.2em] animate-pulse">
          {t('login.terminal')} v1.0
        </p>
      </div>
    </div>
  );
};
