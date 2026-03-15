import { render, screen, fireEvent } from '@testing-library/react';
import { describe, it, expect, vi } from 'vitest';
import { Navbar } from '../Navbar';

// Mock the hooks
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
    i18n: {
      language: 'en',
      changeLanguage: vi.fn(),
    },
  }),
}));

vi.mock('../../AuthContext', () => ({
  useAuth: () => ({
    username: 'test-user',
    isAdmin: true,
    logout: vi.fn(),
  }),
}));

vi.mock('../../ThemeContext', () => ({
  useTheme: () => ({
    theme: 'light',
    setTheme: vi.fn(),
  }),
}));

describe('Navbar Component', () => {
  it('renders all navigation links', () => {
    const onViewChange = vi.fn();
    render(<Navbar activeView="take-order" onViewChange={onViewChange} />);
    
    expect(screen.getByTestId('nav-take-order')).toBeInTheDocument();
    expect(screen.getByTestId('nav-orders')).toBeInTheDocument();
    expect(screen.getByTestId('nav-admin')).toBeInTheDocument();
  });

  it('calls onViewChange when a nav button is clicked', () => {
    const onViewChange = vi.fn();
    render(<Navbar activeView="take-order" onViewChange={onViewChange} />);
    
    fireEvent.click(screen.getByTestId('nav-orders'));
    expect(onViewChange).toHaveBeenCalledWith('orders');
  });

  it('shows the current username', () => {
    render(<Navbar activeView="take-order" onViewChange={() => {}} />);
    expect(screen.getByText('test-user')).toBeInTheDocument();
  });

  it('has a language switcher', () => {
    render(<Navbar activeView="take-order" onViewChange={() => {}} />);
    expect(screen.getByTestId('language-switcher')).toBeInTheDocument();
  });
});
