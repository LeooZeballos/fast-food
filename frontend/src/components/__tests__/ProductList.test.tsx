import { render, screen, waitFor, fireEvent } from '../../test/test-utils';
import { describe, it, expect, vi } from 'vitest';
import { ProductList } from '../ProductList';
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
    getProducts: vi.fn(),
    createProduct: vi.fn(),
    deleteProduct: vi.fn(),
    toggleProductStatus: vi.fn(),
    updateProduct: vi.fn(),
  };
});

const mockProducts = [
  {
    id: 1,
    name: 'Classic Burger',
    price: 10.5,
    active: true,
    formattedPrice: '$10.50',
    icon: 'burger'
  },
  {
    id: 2,
    name: 'French Fries',
    price: 4.0,
    active: false,
    formattedPrice: '$4.00',
    icon: 'fries'
  }
];

describe('ProductList Component', () => {
  it('renders products table', async () => {
    vi.mocked(api.getProducts).mockResolvedValue(mockProducts);
    
    render(<ProductList />);
    
    await waitFor(() => {
      expect(screen.getByText('Classic Burger')).toBeInTheDocument();
    });
    
    expect(screen.getByText('French Fries')).toBeInTheDocument();
    expect(screen.getByText('$10.50')).toBeInTheDocument();
  });

  it('opens create dialog when clicking new product', async () => {
    vi.mocked(api.getProducts).mockResolvedValue(mockProducts);
    
    render(<ProductList />);
    
    await waitFor(() => {
      expect(screen.getByTestId('new-product-button')).toBeInTheDocument();
    });
    
    fireEvent.click(screen.getByTestId('new-product-button'));
    
    expect(screen.getByTestId('product-name-input')).toBeInTheDocument();
    expect(screen.getByTestId('product-price-input')).toBeInTheDocument();
  });

  it('calls createProduct when form is submitted', async () => {
    vi.mocked(api.getProducts).mockResolvedValue(mockProducts);
    vi.mocked(api.createProduct).mockResolvedValue({ ...mockProducts[0], id: 3, name: 'New Burger' });
    
    render(<ProductList />);
    
    await waitFor(() => {
      fireEvent.click(screen.getByTestId('new-product-button'));
    });
    
    fireEvent.change(screen.getByTestId('product-name-input'), { target: { value: 'New Burger' } });
    fireEvent.change(screen.getByTestId('product-price-input'), { target: { value: '12.00' } });
    
    fireEvent.click(screen.getByTestId('create-product-submit'));
    
    await waitFor(() => {
      expect(api.createProduct).toHaveBeenCalled();
    });
  });
});
