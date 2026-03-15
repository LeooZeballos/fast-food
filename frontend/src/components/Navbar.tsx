import { useState, useEffect } from "react";
import { ClipboardList, ShoppingCart, ShieldCheck, Menu as MenuIcon, Clock, Languages, LogOut, User, Sun, Moon } from "lucide-react";
import { BurgerIcon } from "./ui/burger-icon";
import type { View } from "../App";
import { cn } from "@/lib/utils";
import { useTranslation } from "react-i18next";
import { useAuth } from "../AuthContext";
import { useTheme } from "../ThemeContext";

interface NavbarProps {
  activeView: View;
  onViewChange: (view: View) => void;
}

function ThemeToggle() {
  const { theme, setTheme } = useTheme();

  return (
    <button
      onClick={() => setTheme(theme === "dark" ? "light" : "dark")}
      className="flex items-center justify-center w-10 h-10 bg-primary-foreground/5 rounded-2xl border border-primary-foreground/10 hover:bg-primary-foreground/10 transition-all group"
      title={theme === "dark" ? "Switch to Light Mode" : "Switch to Dark Mode"}
    >
      {theme === "dark" ? (
        <Sun className="h-4 w-4 text-secondary group-hover:rotate-90 transition-transform duration-500" />
      ) : (
        <Moon className="h-4 w-4 text-secondary group-hover:-rotate-12 transition-transform duration-500" />
      )}
    </button>
  );
}

function LanguageSwitcher() {
  const { i18n } = useTranslation();

  const toggleLanguage = () => {
    const newLang = i18n.language === 'en' ? 'es' : 'en';
    i18n.changeLanguage(newLang);
  };

  return (
    <button 
      onClick={toggleLanguage}
      data-testid="language-switcher"
      className="flex items-center gap-2 px-3 py-2 bg-primary-foreground/5 rounded-2xl border border-primary-foreground/10 hover:bg-primary-foreground/10 transition-colors group"
      title={i18n.language === 'en' ? 'Switch to Spanish' : 'Cambiar a Inglés'}
    >
      <Languages className="h-4 w-4 text-secondary group-hover:scale-110 transition-transform" />
      <span className="font-black text-[10px] tracking-widest text-primary-foreground/80 uppercase">
        {i18n.language === 'en' ? 'EN' : 'ES'}
      </span>
    </button>
  );
}

function LiveClock() {
  const [time, setTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="hidden lg:flex items-center gap-3 px-4 py-2 bg-black/20 rounded-2xl border border-primary-foreground/10 backdrop-blur-md">
      <Clock className="h-4 w-4 text-secondary animate-pulse" />
      <span className="font-mono text-xs font-black tracking-widest text-primary-foreground/80">
        {time.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' })}
      </span>
    </div>
  );
}

interface NavButtonProps {
  view: View;
  icon: any;
  label: string;
  sublabel: string;
  activeView: View;
  onViewChange: (view: View) => void;
}

const NavButton = ({ view, icon: Icon, label, sublabel, activeView, onViewChange }: NavButtonProps) => (
  <button
    onClick={() => onViewChange(view)}
    data-testid={`nav-${view}`}
    className={cn(
      "relative flex flex-col items-start px-6 py-3 rounded-[1.25rem] transition-all group overflow-hidden border-2",
      activeView === view 
      ? "bg-secondary border-secondary text-primary shadow-lg scale-[1.02]" 
      : "bg-transparent border-transparent text-primary-foreground/60 hover:text-primary-foreground hover:bg-primary-foreground/5 hover:border-primary-foreground/10"
    )}
  >
    <div className="flex items-center gap-3 relative z-10">
      <Icon className={cn("w-5 h-5", activeView === view ? "text-primary" : "group-hover:text-secondary transition-colors")} />
      <div className="text-left">
        <p className="text-[10px] font-black uppercase tracking-widest leading-none mb-1 opacity-60 group-hover:opacity-100 transition-opacity">{sublabel}</p>
        <p className="text-sm font-black uppercase tracking-tighter italic leading-none">{label}</p>
      </div>
    </div>
    {activeView === view && (
      <div className="absolute inset-0 bg-gradient-to-tr from-primary-foreground/20 to-transparent pointer-events-none" />
    )}
  </button>
);

export function Navbar({ activeView, onViewChange }: NavbarProps) {
  const [isMenuOpen, setIsMenuOpen] = useState(false);
  const { t } = useTranslation();
  const { username, isAdmin, logout } = useAuth();

  const handleLogout = async () => {
    try {
      await logout();
    } catch (error) {
      console.error("Logout failed", error);
    }
  };

  return (
    <nav className="bg-primary text-primary-foreground relative z-50">
      {/* Decorative top bar */}
      <div className="h-1 bg-secondary w-full opacity-50" />
      
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-24">
          {/* Brand */}
          <div 
            className="flex items-center cursor-pointer group"
            onClick={() => onViewChange("take-order")}
          >
            <div className="w-12 h-12 bg-primary-foreground/10 backdrop-blur-md rounded-2xl flex items-center justify-center mr-4 group-hover:rotate-12 transition-transform shadow-xl border border-primary-foreground/20 p-2">
              <BurgerIcon className="w-full h-full drop-shadow-lg" />
            </div>
            <div className="hidden sm:block">
              <h1 className="text-3xl font-black tracking-tighter italic uppercase leading-none">
                FastFood<span className="text-secondary">OS</span>
              </h1>
              <p className="text-[9px] font-black uppercase tracking-[0.3em] text-primary-foreground/40 mt-1">{t('nav.operationalSystem')} v1.0</p>
            </div>
          </div>

          {/* Desktop Menu */}
          <div className="hidden md:flex items-center gap-2">
            <NavButton 
              view="take-order" 
              icon={ShoppingCart} 
              label={t('nav.takeOrder')} 
              sublabel={t('nav.sales')} 
              activeView={activeView}
              onViewChange={onViewChange}
            />
            <NavButton 
              view="orders" 
              icon={ClipboardList} 
              label={t('nav.kitchen')} 
              sublabel={t('nav.production')} 
              activeView={activeView}
              onViewChange={onViewChange}
            />
            {isAdmin && (
              <NavButton 
                view="admin" 
                icon={ShieldCheck} 
                label={t('nav.admin')} 
                sublabel={t('nav.system')} 
                activeView={activeView}
                onViewChange={onViewChange}
              />
            )}
          </div>

          {/* Right Side Info */}
          <div className="flex items-center gap-4">
            <div className="hidden xl:flex items-center gap-3 px-4 py-2 bg-primary-foreground/5 rounded-2xl border border-primary-foreground/10">
              <User className="h-4 w-4 text-secondary" />
              <span className="text-xs font-black uppercase tracking-widest text-primary-foreground/80">{username}</span>
              <div className="w-px h-4 bg-primary-foreground/10 mx-2" />
              <button 
                onClick={handleLogout}
                className="hover:text-secondary transition-colors group flex items-center gap-2"
                title={t('nav.logout')}
              >
                <LogOut className="h-4 w-4 group-hover:translate-x-1 transition-transform" />
              </button>
            </div>

            <ThemeToggle />
            <LanguageSwitcher />
            <LiveClock />
            
            {/* Mobile Menu Button */}
            <div className="md:hidden">
              <button
                onClick={() => setIsMenuOpen(!isMenuOpen)}
                className="w-12 h-12 rounded-2xl bg-primary-foreground/5 border border-primary-foreground/10 flex items-center justify-center hover:bg-primary-foreground/10 transition-colors"
              >
                <MenuIcon className="h-6 w-6" />
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Mobile Menu */}
      {isMenuOpen && (
        <div className="md:hidden bg-primary/95 backdrop-blur-xl border-t border-primary-foreground/10 p-4 space-y-2 absolute top-full left-0 right-0 shadow-2xl">
          <button
            onClick={() => { onViewChange("take-order"); setIsMenuOpen(false); }}
            className={cn("w-full px-6 py-4 rounded-2xl text-left font-black uppercase italic tracking-tighter flex items-center gap-4", activeView === "take-order" ? "bg-secondary text-primary" : "hover:bg-primary-foreground/5")}
          >
            <ShoppingCart className="h-5 w-5" /> {t('nav.takeOrder')}
          </button>
          <button
            onClick={() => { onViewChange("orders"); setIsMenuOpen(false); }}
            className={cn("w-full px-6 py-4 rounded-2xl text-left font-black uppercase italic tracking-tighter flex items-center gap-4", activeView === "orders" ? "bg-secondary text-primary" : "hover:bg-primary-foreground/5")}
          >
            <ClipboardList className="h-5 w-5" /> {t('nav.kitchen')}
          </button>
          {isAdmin && (
            <button
              onClick={() => { onViewChange("admin"); setIsMenuOpen(false); }}
              className={cn("w-full px-6 py-4 rounded-2xl text-left font-black uppercase italic tracking-tighter flex items-center gap-4", activeView === "admin" ? "bg-secondary text-primary" : "hover:bg-primary-foreground/5")}
            >
              <ShieldCheck className="h-5 w-5" /> {t('nav.admin')}
            </button>
          )}
        </div>
      )}
    </nav>
  );
}
