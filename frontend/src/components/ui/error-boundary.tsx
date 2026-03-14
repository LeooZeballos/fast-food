import React, { Component, ErrorInfo, ReactNode } from "react";
import { ErrorState } from "./error-state";

interface Props {
  children?: ReactNode;
}

interface State {
  hasError: boolean;
  error?: Error;
}

export class ErrorBoundary extends Component<Props, State> {
  public state: State = {
    hasError: false
  };

  public static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }

  public componentDidCatch(error: Error, errorInfo: ErrorInfo) {
    console.error("Uncaught error:", error, errorInfo);
  }

  public render() {
    if (this.state.hasError) {
      return (
        <div className="min-h-screen bg-muted/50 flex items-center justify-center p-4">
          <ErrorState 
            variant="general"
            title="Application Crash"
            message={`The UI encountered an unexpected error: ${this.state.error?.message || "Internal failure"}`}
            onRetry={() => window.location.href = '/'}
          />
        </div>
      );
    }

    return this.props.children;
  }
}
