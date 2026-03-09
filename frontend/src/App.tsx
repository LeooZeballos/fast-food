import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { useState } from "react";
import { Toaster } from "sonner";
import { OrderList } from "./components/OrderList";
import { TakeOrder } from "./components/TakeOrder";
import { AdminPanel } from "./components/AdminPanel";
import { Navbar } from "./components/Navbar";

const queryClient = new QueryClient();

export type View = "take-order" | "orders" | "admin";

function App() {
  const [activeView, setActiveView] = useState<View>(() => (localStorage.getItem("activeView") as View) || "take-order");

  const handleViewChange = (view: View) => {
    setActiveView(view);
    localStorage.setItem("activeView", view);
  };

  return (
    <QueryClientProvider client={queryClient}>
      <Toaster richColors closeButton position="top-right" />
      <div className="min-h-screen bg-slate-50 flex flex-col font-sans">
        <Navbar activeView={activeView} onViewChange={handleViewChange} />
        
        <main className="flex-grow container mx-auto px-4 py-8">
          <div className="max-w-6xl mx-auto">
            {activeView === "take-order" && <TakeOrder />}
            {activeView === "orders" && <OrderList />}
            {activeView === "admin" && <AdminPanel />}
          </div>
        </main>

        <footer id="sticky-footer" className="flex-shrink-0 py-4 bg-primary text-primary-foreground/50 mt-auto border-t-4 border-secondary">
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
