import { Bar } from 'react-chartjs-2';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from 'chart.js';

ChartJS.register(
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend
);

function BalanceChart({ monthlyStats }) {
  const data = {
    labels: monthlyStats.map(stat => stat.monthName),
    datasets: [
      {
        label: 'Net Balance',
        data: monthlyStats.map(stat => stat.netChange),
        backgroundColor: monthlyStats.map(stat =>
          stat.netChange >= 0 ? 'rgba(39, 174, 96, 0.8)' : 'rgba(231, 76, 60, 0.8)'
        ),
        borderColor: monthlyStats.map(stat =>
          stat.netChange >= 0 ? '#27ae60' : '#e74c3c'
        ),
        borderWidth: 2,
      },
    ],
  };

  const options = {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        display: false,
      },
      title: {
        display: true,
        text: 'Monthly Net Balance',
        font: {
          size: 16,
        },
      },
      tooltip: {
        callbacks: {
          label: function(context) {
            const value = new Intl.NumberFormat('en-US', {
              style: 'currency',
              currency: 'USD'
            }).format(context.parsed.y);
            return `Net: ${value}`;
          }
        }
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: function(value) {
            return '$' + value.toLocaleString();
          }
        }
      },
    },
  };

  return (
    <div style={{ height: '300px', marginTop: '1rem' }}>
      <Bar data={data} options={options} />
    </div>
  );
}

export default BalanceChart;
