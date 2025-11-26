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
import { useCurrency } from '../../hooks/useCurrency';

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

function ExpenseIncomeChart({ dailyStats }) {
  const { t, formatDate } = useLanguage();
  const { formatCurrency } = useCurrency();
  const data = {
    labels: dailyStats.map(stat => {
      const date = new Date(stat.date);
      return formatDate(date, { month: 'short', day: 'numeric' });
    }),
    datasets: [
      {
        label: t('transactions.income'),
        data: dailyStats.map(stat => stat.income),
        borderColor: '#27ae60',
        backgroundColor: 'rgba(39, 174, 96, 0.1)',
        tension: 0.4,
        fill: true,
      },
      {
        label: t('transactions.expense'),
        data: dailyStats.map(stat => stat.expense),
        borderColor: '#e74c3c',
        backgroundColor: 'rgba(231, 76, 60, 0.1)',
        tension: 0.4,
        fill: true,
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
      },
      title: {
        display: true,
        text: `${t('transactions.income')} vs ${t('transactions.expense')} (${t('search.last30Days')})`,
        font: {
          size: 16,
        },
      },
      tooltip: {
        mode: 'index',
        intersect: false,
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
        ticks: {
          callback: function(value) {
            return formatCurrency(value);
          }
        }
      },
    },
  };

  return (
    <div style={{ height: '300px', marginTop: '1rem' }}>
      <Line data={data} options={options} />
    </div>
  );
}

export default ExpenseIncomeChart;
