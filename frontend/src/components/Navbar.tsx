import { useState } from "react";
import { Utensils, ClipboardList, Store, Package, Menu as MenuIcon, ShoppingCart } from "lucide-react";
import type { View } from "../App";

interface NavbarProps {
  activeView: View;
  onViewChange: (view: View) => void;
}

export function Navbar({ activeView, onViewChange }: NavbarProps) {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  return (
    <nav className="bg-primary text-primary-foreground shadow-lg border-b-4 border-secondary">
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-16">
          {/* Brand */}
          <div 
            className="flex items-center cursor-pointer text-2xl font-black tracking-tighter italic uppercase"
            onClick={() => onViewChange("take-order")}
          >
            <span className="mr-2 not-italic">🍟</span>
            <span>FastFood<span className="text-secondary">App</span></span>
          </div>

          {/* Desktop Menu */}
          <div className="hidden md:flex items-center space-x-1">
            <button
              onClick={() => onViewChange("take-order")}
              className={`flex items-center px-4 py-2 rounded-md text-sm font-bold transition-all ${
                activeView === "take-order" 
                ? "bg-secondary text-secondary-foreground shadow-inner" 
                : "hover:bg-white/10"
              }`}
            >
              <ShoppingCart className="w-4 h-4 mr-2" />
              <span>POS / TAKE ORDER</span>
            </button>

            <button
              onClick={() => onViewChange("orders")}
              className={`flex items-center px-4 py-2 rounded-md text-sm font-bold transition-all ${
                activeView === "orders" 
                ? "bg-secondary text-secondary-foreground shadow-inner" 
                : "hover:bg-white/10"
              }`}
            >
              <ClipboardList className="w-4 h-4 mr-2" />
              <span>MANAGE ORDERS</span>
            </button>

            <button
              onClick={() => onViewChange("products")}
              className={`flex items-center px-4 py-2 rounded-md text-sm font-bold transition-all ${
                activeView === "products" 
                ? "bg-secondary text-secondary-foreground shadow-inner" 
                : "hover:bg-white/10"
              }`}
            >
              <Package className="w-4 h-4 mr-2" />
              <span>PRODUCTS</span>
            </button>

            <button
              onClick={() => onViewChange("menus")}
              className={`flex items-center px-4 py-2 rounded-md text-sm font-bold transition-all ${
                activeView === "menus" 
                ? "bg-secondary text-secondary-foreground shadow-inner" 
                : "hover:bg-white/10"
              }`}
            >
              <Utensils className="w-4 h-4 mr-2" />
              <span>MENUS</span>
            </button>

            <button
              onClick={() => onViewChange("branches")}
              className={`flex items-center px-4 py-2 rounded-md text-sm font-bold transition-all ${
                activeView === "branches" 
                ? "bg-secondary text-secondary-foreground shadow-inner" 
                : "hover:bg-white/10"
              }`}
            >
              <Store className="w-4 h-4 mr-2" />
              <span>BRANCHES</span>
            </button>
          </div>

          {/* Mobile Menu Button */}
          <div className="md:hidden">
            <button
              onClick={() => setIsMenuOpen(!isMenuOpen)}
              className="p-2 rounded-md text-gray-400 hover:text-white hover:bg-gray-700 focus:outline-none"
            >
              <MenuIcon className="h-6 w-6" />
            </button>
          </div>
        </div>
      </div>

      {/* Mobile Menu */}
      {isMenuOpen && (
        <div className="md:hidden bg-[#343a40] border-t border-gray-700 pb-3 pt-2 px-2 space-y-1">
          <button
            onClick={() => {
              onViewChange("orders");
              setIsMenuOpen(false);
            }}
            className="block px-3 py-2 rounded-md text-base font-medium text-white hover:bg-gray-700 w-full text-left"
          >
            Orders
          </button>
          <button
            onClick={() => {
              onViewChange("products");
              setIsMenuOpen(false);
            }}
            className="block px-3 py-2 rounded-md text-base font-medium text-gray-300 hover:text-white hover:bg-gray-700 w-full text-left"
          >
            Products
          </button>
          <button
            onClick={() => {
              onViewChange("menus");
              setIsMenuOpen(false);
            }}
            className="block px-3 py-2 rounded-md text-base font-medium text-gray-300 hover:text-white hover:bg-gray-700 w-full text-left"
          >
            Menus
          </button>
          <button
            onClick={() => {
              onViewChange("branches");
              setIsMenuOpen(false);
            }}
            className="block px-3 py-2 rounded-md text-base font-medium text-gray-300 hover:text-white hover:bg-gray-700 w-full text-left"
          >
            Branches
          </button>
        </div>
      )}
    </nav>
  );
}
