import React, { useState, useRef, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import api from '../../services/api';
import { ChevronDown, Search } from 'lucide-react';

const CurrencySelector = ({ value, onChange, label = "Currency" }) => {
  const [isOpen, setIsOpen] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');
  const dropdownRef = useRef(null);
  const searchRef = useRef(null);

  const { data: currencies = [], isLoading, error } = useQuery({
    queryKey: ['currencies'],
    queryFn: async () => {
      const response = await api.get('/currency/available');
      return response.data;
    }
  });

  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setIsOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  useEffect(() => {
    if (isOpen && searchRef.current) {
      searchRef.current.focus();
    }
  }, [isOpen]);

  // Ensure currencies is always an array
  const currencyList = Array.isArray(currencies) ? currencies : [];
  
  const filteredCurrencies = currencyList.filter(currency =>
    currency.code?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    currency.name?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const selectedCurrency = currencyList.find(c => c.code === value);

  return (
    <div className="relative" ref={dropdownRef}>
      <label className="block text-sm font-medium text-gray-700 dark:text-gray-300 mb-1">
        {label}
      </label>

      <button
        type="button"
        onClick={() => !isLoading && setIsOpen(!isOpen)}
        disabled={isLoading}
        className="w-full px-3 py-2 border border-gray-300 dark:border-gray-600 rounded-lg bg-white dark:bg-gray-700 flex items-center justify-between hover:border-gray-400 focus:outline-none focus:ring-2 focus:ring-blue-500 disabled:opacity-50 disabled:cursor-not-allowed"
      >
        <div className="flex items-center">
          {isLoading ? (
            <span className="text-gray-500">Loading currencies...</span>
          ) : error ? (
            <span className="text-red-500">Failed to load currencies</span>
          ) : selectedCurrency ? (
            <>
              <span className="text-lg mr-2">{selectedCurrency.symbol}</span>
              <span className="font-medium">{selectedCurrency.code}</span>
              <span className="text-gray-500 dark:text-gray-400 ml-2 text-sm">
                {selectedCurrency.name}
              </span>
            </>
          ) : (
            <span className="text-gray-500">Select currency</span>
          )}
        </div>
        {isLoading ? (
          <div className="animate-spin rounded-full h-4 w-4 border-b-2 border-gray-600"></div>
        ) : (
          <ChevronDown className={`w-4 h-4 transition-transform ${isOpen ? 'rotate-180' : ''}`} />
        )}
      </button>

      {isOpen && (
        <div className="absolute z-50 w-full mt-1 bg-white dark:bg-gray-800 border border-gray-300 dark:border-gray-600 rounded-lg shadow-lg">
          <div className="p-2 border-b border-gray-200 dark:border-gray-700">
            <div className="relative">
              <Search className="absolute left-3 top-1/2 transform -translate-y-1/2 w-4 h-4 text-gray-400" />
              <input
                ref={searchRef}
                type="text"
                value={searchTerm}
                onChange={(e) => setSearchTerm(e.target.value)}
                placeholder="Search currencies..."
                className="w-full pl-9 pr-3 py-2 text-sm border border-gray-300 dark:border-gray-600 rounded focus:outline-none focus:ring-2 focus:ring-blue-500 dark:bg-gray-700"
              />
            </div>
          </div>

          <div className="max-h-64 overflow-y-auto">
            {filteredCurrencies.length > 0 ? (
              filteredCurrencies.map((currency) => (
                <button
                  key={currency.code}
                  onClick={() => {
                    onChange(currency.code);
                    setIsOpen(false);
                    setSearchTerm('');
                  }}
                  className="w-full px-3 py-2 text-left hover:bg-gray-100 dark:hover:bg-gray-700 flex items-center"
                >
                  <span className="text-lg mr-3 w-6">{currency.symbol}</span>
                  <span className="font-medium mr-2">{currency.code}</span>
                  <span className="text-gray-500 dark:text-gray-400 text-sm">
                    {currency.name}
                  </span>
                </button>
              ))
            ) : (
              <div className="px-3 py-4 text-center text-gray-500 dark:text-gray-400 text-sm">
                {searchTerm ? 'No currencies found' : 'No currencies available'}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};

export default CurrencySelector;
