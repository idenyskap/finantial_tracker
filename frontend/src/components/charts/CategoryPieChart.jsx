import { Pie } from 'react-chartjs-2';
import { Chart as ChartJS, ArcElement, Tooltip, Legend } from 'chart.js';
import { useCurrency } from '../../contexts/CurrencyContext';

ChartJS.register(ArcElement, Tooltip, Legend);

function CategoryPieChart({ categories, title = 'Expenses by Category' }) {
  const { formatCurrency } = useCurrency();
  const data = {
    labels: categories.map(cat => cat.categoryName),
    datasets: [
      {
        data: categories.map(cat => cat.totalAmount),
        backgroundColor: categories.map(cat => cat.categoryColor),
        borderWidth: 2,
        borderColor: '#fff',
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'right',
        labels: {
          padding: 15,
          font: {
            size: 12,
          },
        },
      },
      title: {
        display: true,
        text: title,
        font: {
          size: 16,
        },
      },
      tooltip: {
        callbacks: {
          label: function(context) {
            const label = context.label || '';
            const value = formatCurrency(context.parsed);
            const percentage = context.dataset.data[context.dataIndex] /
              context.dataset.data.reduce((a, b) => a + b, 0) * 100;
            return `${label}: ${value} (${percentage.toFixed(1)}%)`;
          }
        }
      },
    },
  };

  return (
    <div style={{ height: '300px' }}>
      <Pie data={data} options={options} />
    </div>
  );
}

export default CategoryPieChart;
