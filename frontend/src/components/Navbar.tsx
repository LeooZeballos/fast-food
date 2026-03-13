import { useState, useEffect } from "react";
import { ClipboardList, ShoppingCart, ShieldCheck, Menu as MenuIcon, UtensilsCrossed, Clock } from "lucide-react";
import type { View } from "../App";
import { cn } from "@/lib/utils";

interface NavbarProps {
  activeView: View;
  onViewChange: (view: View) => void;
}

function LiveClock() {
  const [time, setTime] = useState(new Date());

  useEffect(() => {
    const timer = setInterval(() => setTime(new Date()), 1000);
    return () => clearInterval(timer);
  }, []);

  return (
    <div className="hidden lg:flex items-center gap-3 px-4 py-2 bg-black/20 rounded-2xl border border-white/10 backdrop-blur-md">
      <Clock className="h-4 w-4 text-secondary animate-pulse" />
      <span className="font-mono text-xs font-black tracking-widest text-white/80">
        {time.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit', second: '2-digit' })}
      </span>
    </div>
  );
}

export function Navbar({ activeView, onViewChange }: NavbarProps) {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const NavButton = ({ view, icon: Icon, label, sublabel }: { view: View, icon: any, label: string, sublabel: string }) => (
    <button
      onClick={() => onViewChange(view)}
      className={cn(
        "relative flex flex-col items-start px-6 py-3 rounded-[1.25rem] transition-all group overflow-hidden border-2",
        activeView === view 
        ? "bg-secondary border-secondary text-primary shadow-lg scale-[1.02]" 
        : "bg-transparent border-transparent text-white/60 hover:text-white hover:bg-white/5 hover:border-white/10"
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
        <div className="absolute inset-0 bg-gradient-to-tr from-white/20 to-transparent pointer-events-none" />
      )}
    </button>
  );

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
            <div className="w-12 h-12 bg-secondary rounded-2xl flex items-center justify-center mr-4 group-hover:rotate-12 transition-transform shadow-xl shadow-secondary/20">
              <UtensilsCrossed className="text-primary h-6 w-6" />
            </div>
            <div className="hidden sm:block">
              <h1 className="text-3xl font-black tracking-tighter italic uppercase leading-none">
                FastFood<span className="text-secondary">OS</span>
              </h1>
              <p className="text-[9px] font-black uppercase tracking-[0.3em] text-white/40 mt-1">Operational System v1.0</p>
            </div>
          </div>

          {/* Desktop Menu */}
          <div className="hidden md:flex items-center gap-2">
            <NavButton 
              view="take-order" 
              icon={ShoppingCart} 
              label="Terminal" 
              sublabel="Sales" 
            />
            <NavButton 
              view="orders" 
              icon={ClipboardList} 
              label="Kitchen" 
              sublabel="Production" 
            />
            <NavButton 
              view="admin" 
              icon={ShieldCheck} 
              label="Console" 
              sublabel="System" 
            />
          </div>

          {/* Right Side Info */}
          <div className="flex items-center gap-4">
            <LiveClock />
            
            {/* Mobile Menu Button */}
            <div className="md:hidden">
              <button
                onClick={() => setIsMenuOpen(!isMenuOpen)}
                className="w-12 h-12 rounded-2xl bg-white/5 border border-white/10 flex items-center justify-center hover:bg-white/10 transition-colors"
              >
                <MenuIcon className="h-6 w-6" />
              </button>
            </div>
          </div>
        </div>
      </div>

      {/* Mobile Menu */}
      {isMenuOpen && (
        <div className="md:hidden bg-primary/95 backdrop-blur-xl border-t border-white/10 p-4 space-y-2 absolute top-full left-0 right-0 shadow-2xl">
          <button
            onClick={() => { onViewChange("take-order"); setIsMenuOpen(false); }}
            className={cn("w-full px-6 py-4 rounded-2xl text-left font-black uppercase italic tracking-tighter flex items-center gap-4", activeView === "take-order" ? "bg-secondary text-primary" : "hover:bg-white/5")}
          >
            <ShoppingCart className="h-5 w-5" /> Sales Terminal
          </button>
          <button
            onClick={() => { onViewChange("orders"); setIsMenuOpen(false); }}
            className={cn("w-full px-6 py-4 rounded-2xl text-left font-black uppercase italic tracking-tighter flex items-center gap-4", activeView === "orders" ? "bg-secondary text-primary" : "hover:bg-white/5")}
          >
            <ClipboardList className="h-5 w-5" /> Kitchen Display
          </button>
          <button
            onClick={() => { onViewChange("admin"); setIsMenuOpen(false); }}
            className={cn("w-full px-6 py-4 rounded-2xl text-left font-black uppercase italic tracking-tighter flex items-center gap-4", activeView === "admin" ? "bg-secondary text-primary" : "hover:bg-white/5")}
          >
            <ShieldCheck className="h-5 w-5" /> System Console
          </button>
        </div>
      )}
    </nav>
  );
}
