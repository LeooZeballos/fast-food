import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { useState } from "react";
import { Toaster } from "sonner";
import { OrderList } from "./components/OrderList";
import { TakeOrder } from "./components/TakeOrder";
import { AdminPanel } from "./components/AdminPanel";
import { Navbar } from "./components/Navbar";
import { AuthProvider, useAuth } from "./AuthContext";
import { Login } from "./components/Login";
import { ThemeProvider } from "./ThemeContext";
import { ErrorBoundary } from "./components/ui/error-boundary";

const queryClient = new QueryClient();


export type View = "take-order" | "orders" | "admin";

function AppContent() {
  const [activeView, setActiveView] = useState<View>(() => (localStorage.getItem("activeView") as View) || "take-order");
  const { isAuthenticated, isLoading } = useAuth();

  const handleViewChange = (view: View) => {
    setActiveView(view);
    localStorage.setItem("activeView", view);
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center">
        <div className="w-12 h-12 border-4 border-primary border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Login />;
  }

  return (
    <div className="min-h-screen bg-background flex flex-col font-sans animate-in fade-in duration-500 transition-colors duration-500">
      <Navbar activeView={activeView} onViewChange={handleViewChange} />
      
      <main className="flex-grow container mx-auto px-4 py-8">
        <div className="max-w-6xl mx-auto">
          {activeView === "take-order" && <TakeOrder />}
          {activeView === "orders" && <OrderList />}
          {activeView === "admin" && <AdminPanel />}
        </div>
      </main>

      <footer id="sticky-footer" class="flex-shrink-0 py-4 bg-primary text-primary-foreground/50 mt-auto border-t-4 border-secondary">
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
  );
}

function App() {
  return (
    <ErrorBoundary>
      <ThemeProvider defaultTheme="light">
        <QueryClientProvider client={queryClient}>
          <Toaster richColors closeButton position="top-right" />
          <AuthProvider>
            <AppContent />
          </AuthProvider>
        </QueryClientProvider>
      </ThemeProvider>
    </ErrorBoundary>
  );
}

export default App;
