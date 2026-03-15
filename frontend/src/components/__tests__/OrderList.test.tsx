import { render, screen, waitFor } from '../../test/test-utils';
import { describe, it, expect, vi } from 'vitest';
import { OrderList } from '../OrderList';
import * as api from '../../api';

// Mock translations
vi.mock('react-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
    i18n: {
      language: 'en',
      changeLanguage: vi.fn(),
    },
  }),
}));

// Mock API
vi.mock('../../api', async () => {
  const actual = await vi.importActual('../../api');
  return {
    ...actual,
    getOrders: vi.fn(),
    startPreparation: vi.fn(),
    finishPreparation: vi.fn(),
    confirmPayment: vi.fn(),
    cancelOrder: vi.fn(),
    rejectOrder: vi.fn(),
  };
});

vi.mock('../../AuthContext', () => ({
  useAuth: () => ({
    branchId: 1,
    isAdmin: false,
  }),
}));

const mockOrders = [
  {
    id: 1,
    creationTimestamp: new Date().toISOString(),
    formattedState: 'Created',
    branchName: 'Main Branch',
    formattedFoodOrderDetails: '2x Burger',
    formattedTotal: '$20.00',
    foodOrderDetails: [],
    total: 20
  },
  {
    id: 2,
    creationTimestamp: new Date().toISOString(),
    formattedState: 'Inpreparation',
    branchName: 'Main Branch',
    formattedFoodOrderDetails: '1x Pizza',
    formattedTotal: '$15.00',
    foodOrderDetails: [],
    total: 15
  }
];

describe('OrderList Component', () => {
  it('renders columns and fetched orders', async () => {
    vi.mocked(api.getOrders).mockResolvedValue(mockOrders);
    
    render(<OrderList />);
    
    // Wait for the query to resolve and content to render
    await waitFor(() => {
      expect(screen.getByText('kitchen.columns.new')).toBeInTheDocument();
    });
    
    expect(screen.getByText('2x Burger')).toBeInTheDocument();
    expect(screen.getByText('1x Pizza')).toBeInTheDocument();
    expect(screen.getByText('#1')).toBeInTheDocument();
    expect(screen.getByText('#2')).toBeInTheDocument();
  });

  it('shows empty state when no orders', async () => {
    vi.mocked(api.getOrders).mockResolvedValue([]);
    
    render(<OrderList />);
    
    await waitFor(() => {
      // Check for empty state labels
      const emptyLabels = screen.getAllByText('common.clear');
      expect(emptyLabels.length).toBeGreaterThan(0);
    });
  });
});
