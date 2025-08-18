import React from 'react';
import { useQuery } from '@tanstack/react-query';
import axios from 'axios';

const TransactionList = ({ transactions }) => {
  const { data: currencyPrefs } = useQuery({
    queryKey: ['currencyPreferences'],
    queryFn: async () => {
      const response = await axios.get('/api/v1/currency/preferences');
      return response.data;
    }
  });

  const formatAmount = (amount, currency) => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: currency || 'USD'
    }).format(amount);
  };

  return (
    <div className="space-y-2">
      {transactions.map((transaction) => (
        <div key={transaction.id} className="bg-white dark:bg-gray-800 p-4 rounded-lg shadow">
          <div className="flex justify-between items-center">
            <div>
              <p className="font-medium">{transaction.description}</p>
              <p className="text-sm text-gray-600 dark:text-gray-400">
                {transaction.categoryName} • {new Date(transaction.date).toLocaleDateString()}
              </p>
            </div>
            <div className="text-right">
              <p className={`font-bold ${
                transaction.type === 'INCOME' ? 'text-green-600' : 'text-red-600'
              }`}>
                {transaction.type === 'INCOME' ? '+' : '-'}
                {formatAmount(transaction.amount, transaction.currency || currencyPrefs?.defaultCurrency)}
              </p>
              {currencyPrefs?.displaySecondary && transaction.convertedAmount && (
                <p className="text-sm text-gray-500">
                  ≈ {formatAmount(transaction.convertedAmount, currencyPrefs.secondaryCurrency)}
                </p>
              )}
            </div>
          </div>
        </div>
      ))}
    </div>
  );
};

export default TransactionList;
