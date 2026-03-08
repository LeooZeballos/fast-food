import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { useState } from "react";
import { BranchList } from "./components/BranchList";
import { ProductList } from "./components/ProductList";
import { MenuList } from "./components/MenuList";
import { OrderList } from "./components/OrderList";
import { TakeOrder } from "./components/TakeOrder";
import { Navbar } from "./components/Navbar";

const queryClient = new QueryClient();

export type View = "take-order" | "orders" | "branches" | "products" | "menus";

function App() {
  const [activeView, setActiveView] = useState<View>("take-order");

  return (
    <QueryClientProvider client={queryClient}>
      <div className="min-h-screen bg-slate-50 flex flex-col font-sans">
        <Navbar activeView={activeView} onViewChange={setActiveView} />
        
        <main className="flex-grow container mx-auto px-4 py-8">
          <div className="max-w-6xl mx-auto">
            {activeView === "take-order" && <TakeOrder />}
            {activeView === "orders" && <OrderList />}
            {activeView === "branches" && <BranchList />}
            {activeView === "products" && <ProductList />}
            {activeView === "menus" && <MenuList />}
          </div>
        </main>

        <footer id="sticky-footer" className="flex-shrink-0 py-4 bg-[#343a40] text-white/50 mt-auto">
          <div className="container mx-auto text-center">
            <small>
              Copyright &copy; Leonel Zeballos -
              <span className="inline-flex gap-3 ml-2">
                <a id="personalMail" href="mailto:zeballosleonel3@gmail.com" className="hover:text-white transition-colors">
                  <i className="fa-solid fa-envelope"></i>
                </a>
                <a id="gitHubPage" href="https://github.com/LeooZeballos" className="hover:text-white transition-colors">
                  <i className="fa-brands fa-github-square"></i>
                </a>
                <a id="linkedInPage" href="https://www.linkedin.com/in/leonelayrtonzeballos/" className="hover:text-white transition-colors">
                  <i className="fa-brands fa-linkedin"></i>
                </a>
              </span>
            </small>
          </div>
        </footer>
      </div>
    </QueryClientProvider>
  );
}

export default App;
