import React from "react";
import { cn } from "@/lib/utils";

interface BurgerIconProps extends React.SVGProps<SVGSVGElement> {
  className?: string;
}

export const BurgerIcon: React.FC<BurgerIconProps> = ({ className, ...props }) => {
  return (
    <svg 
      xmlns="http://www.w3.org/2000/svg" 
      viewBox="0 0 512 512" 
      className={cn("w-10 h-10", className)}
      {...props}
    >
      <defs>
        <linearGradient id="bun-grad" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" style={{ stopColor: "#FFB155", stopOpacity: 1 }} />
          <stop offset="100%" style={{ stopColor: "#FF8C00", stopOpacity: 1 }} />
        </linearGradient>
        <linearGradient id="meat-grad" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" style={{ stopColor: "#8B4513", stopOpacity: 1 }} />
          <stop offset="100%" style={{ stopColor: "#5D2E0C", stopOpacity: 1 }} />
        </linearGradient>
      </defs>
      {/* Top Bun */}
      <path d="M64 240c0-88.37 85.96-160 192-160s192 71.63 192 160H64z" fill="url(#bun-grad)"/>
      {/* Lettuce */}
      <path d="M48 240c0 13.25 10.75 24 24 24h368c13.25 0 24-10.75 24-24s-10.75-24-24-24H72c-13.25 0-24 10.75-24 24z" fill="#32CD32"/>
      {/* Cheese */}
      <path d="M64 272l384 16v32H64z" fill="#FFD700"/>
      {/* Meat */}
      <rect x="64" y="320" width="384" height="64" rx="32" fill="url(#meat-grad)"/>
      {/* Bottom Bun */}
      <path d="M64 400c0 26.51 85.96 48 192 48s192-21.49 192-48H64z" fill="url(#bun-grad)"/>
      {/* Sesame Seeds */}
      <circle cx="160" cy="140" r="8" fill="#FFF5E1"/>
      <circle cx="220" cy="110" r="8" fill="#FFF5E1"/>
      <circle cx="280" cy="120" r="8" fill="#FFF5E1"/>
      <circle cx="340" cy="150" r="8" fill="#FFF5E1"/>
      <circle cx="250" cy="180" r="8" fill="#FFF5E1"/>
    </svg>
  );
};
