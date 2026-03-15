import React, { createContext, useContext, useState, useEffect } from "react";
import { getMe, login as apiLogin, logout as apiLogout } from "./api";

interface AuthContextType {
  isAuthenticated: boolean;
  username: string | null;
  branchId: number | null;
  isAdmin: boolean;
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [username, setUsername] = useState<string | null>(null);
  const [branchId, setBranchId] = useState<number | null>(null);
  const [isAdmin, setIsAdmin] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    checkAuth();
  }, []);

  const checkAuth = async () => {
    try {
      const user = await getMe();
      if (user && user.name && user.name !== "anonymous") {
        setIsAuthenticated(true);
        setUsername(user.name);
        setBranchId(user.branchId && user.branchId !== "none" ? user.branchId : null);
        setIsAdmin(user.roles?.includes("ADMIN") || false);
      } else {
        resetAuth();
      }
    } catch (error) {
      resetAuth();
    } finally {
      setIsLoading(false);
    }
  };

  const resetAuth = () => {
    setIsAuthenticated(false);
    setUsername(null);
    setBranchId(null);
    setIsAdmin(false);
  };

  const login = async (user: string, pass: string) => {
    try {
      await apiLogin(user, pass);
      setIsAuthenticated(true);
      try {
        const userInfo = await getMe();
        if (userInfo && userInfo.name) {
          setUsername(userInfo.name);
          setBranchId(userInfo.branchId && userInfo.branchId !== "none" ? userInfo.branchId : null);
          setIsAdmin(userInfo.roles?.includes("ADMIN") || false);
        } else {
          setUsername(user);
        }
      } catch (e) {
        console.warn("getMe failed after login", e);
        setUsername(user);
      }
    } catch (error) {
      resetAuth();
      throw error;
    }
  };

  const logout = async () => {
    await apiLogout();
    resetAuth();
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, username, branchId, isAdmin, login, logout, isLoading }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error("useAuth must be used within an AuthProvider");
  }
  return context;
};
