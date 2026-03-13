import React, { createContext, useContext, useState, useEffect } from "react";
import { getMe, login as apiLogin, logout as apiLogout } from "./api";

interface AuthContextType {
  isAuthenticated: boolean;
  username: string | null;
  login: (username: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
  isLoading: boolean;
}

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [username, setUsername] = useState<string | null>(null);
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
      } else {
        setIsAuthenticated(false);
        setUsername(null);
      }
    } catch (error) {
      // 401 is expected if not logged in
      setIsAuthenticated(false);
      setUsername(null);
    } finally {
      setIsLoading(false);
    }
  };

  const login = async (user: string, pass: string) => {
    try {
      await apiLogin(user, pass);
      setIsAuthenticated(true);
      try {
        const userInfo = await getMe();
        if (userInfo && userInfo.name) {
          setUsername(userInfo.name);
        } else {
          setUsername(user);
        }
      } catch (e) {
        console.warn("getMe failed after login, using provided username", e);
        setUsername(user);
      }
    } catch (error) {
      setIsAuthenticated(false);
      setUsername(null);
      throw error;
    }
  };

  const logout = async () => {
    await apiLogout();
    setIsAuthenticated(false);
    setUsername(null);
  };

  return (
    <AuthContext.Provider value={{ isAuthenticated, username, login, logout, isLoading }}>
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
