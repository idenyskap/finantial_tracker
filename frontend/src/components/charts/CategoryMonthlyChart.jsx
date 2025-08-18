import { Line } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
} from 'chart.js';
import { useLanguage } from '../../hooks/useLanguage';
import { useCurrency } from '../../contexts/CurrencyContext';

ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
  Filler
);

const colors = [
  '#3498db', '#e74c3c', '#2ecc71', '#f39c12', '#9b59b6',
  '#1abc9c', '#e67e22', '#34495e', '#95a5a6', '#d35400'
];

function CategoryMonthlyChart({ categoryStats }) {
  const { t } = useLanguage();
  const { formatCurrency } = useCurrency();

  if (!categoryStats || categoryStats.length === 0) {
    return (
      <div style={{ 
        display: 'flex', 
        alignItems: 'center', 
        justifyContent: 'center', 
        height: '300px',
        color: '#6c757d',
        fontSize: '1.1rem'
      }}>
        {t('analytics.noDataAvailable')}
      </div>
    );
  }

  const allMonths = [];
  categoryStats.forEach(category => {
    category.monthlyData.forEach(month => {
      if (!allMonths.includes(month.month)) {
        allMonths.push(month.month);
      }
    });
  });

  allMonths.sort();

  const data = {
    labels: allMonths.map(month => {
      const [year, monthNum] = month.split('-');
      const date = new Date(year, monthNum - 1);
      return date.toLocaleDateString('en-US', { month: 'short', year: 'numeric' });
    }),
    datasets: categoryStats.map((category, index) => {
      const monthlyMap = {};
      category.monthlyData.forEach(month => {
        monthlyMap[month.month] = month.amount;
      });

      const data = allMonths.map(month => monthlyMap[month] || 0);

      return {
        label: category.categoryName,
        data: data,
        borderColor: category.categoryColor || colors[index % colors.length],
        backgroundColor: `${category.categoryColor || colors[index % colors.length]}20`,
        tension: 0.4,
        fill: false,
        pointBackgroundColor: category.categoryColor || colors[index % colors.length],
        pointBorderColor: category.categoryColor || colors[index % colors.length],
        pointRadius: 4,
        pointHoverRadius: 6,
      };
    }),
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
        labels: {
          usePointStyle: true,
          padding: 20,
        }
      },
      title: {
        display: true,
        text: t('analytics.categoryMonthlyTrends'),
        font: {
          size: 16,
          weight: 'bold'
        },
        padding: {
          bottom: 20
        }
      },
      tooltip: {
        mode: 'index',
        intersect: false,
        backgroundColor: 'rgba(0, 0, 0, 0.8)',
        titleColor: 'white',
        bodyColor: 'white',
        borderColor: 'rgba(255, 255, 255, 0.1)',
        borderWidth: 1,
        cornerRadius: 8,
        padding: 12,
        callbacks: {
          label: function(context) {
            let label = context.dataset.label || '';
            if (label) {
              label += ': ';
            }
            if (context.parsed.y !== null) {
              label += formatCurrency(context.parsed.y);
            }
            return label;
          }
        }
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        grid: {
          color: 'rgba(0, 0, 0, 0.1)',
        },
        ticks: {
          callback: function(value) {
            return formatCurrency(value);
          }
        }
      },
      x: {
        grid: {
          color: 'rgba(0, 0, 0, 0.1)',
        }
      }
    },
    interaction: {
      mode: 'index',
      intersect: false,
    },
    elements: {
      line: {
        tension: 0.4
      }
    }
  };

  return (
    <div style={{ height: '400px', marginTop: '1rem' }}>
      <Line data={data} options={options} />
    </div>
  );
}

export default CategoryMonthlyChart;