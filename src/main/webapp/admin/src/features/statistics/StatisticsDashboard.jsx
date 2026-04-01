import { useState } from 'react';
import { useToast } from '../../components/Toast';
import api from '../../api/client';

export default function StatisticsDashboard() {
  const [loading, setLoading] = useState(false);
  const toast = useToast();
  const [filters, setFilters] = useState({
    startTime: Date.now() - 30 * 24 * 60 * 60 * 1000, // 30 days ago
    endTime: Date.now(),
    timestep: 'day'
  });

  const handleExport = async () => {
    setLoading(true);
    try {
      const params = new URLSearchParams({
        startDate: filters.startTime,
        endDate: filters.endTime,
        timestep: filters.timestep
      });

      const response = await api.get(`/statistics/export?${params.toString()}`, {
        responseType: 'blob'
      });

      const blob = response.data;
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'statistics.csv';
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success('Statistics exported successfully');
    } catch (error) {
      toast.error('Failed to export statistics: ' + (error.response?.data?.error || error.message));
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <h1>Statistics</h1>

      <div style={{
        backgroundColor: 'white',
        padding: '1.5rem',
        borderRadius: '8px',
        boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
        marginTop: '1.5rem'
      }}>
        <h2 style={{ marginTop: 0 }}>Export Statistics</h2>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: '1rem', marginBottom: '1.5rem' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600' }}>
              Start Date
            </label>
            <input
              type="date"
              value={new Date(filters.startTime).toISOString().split('T')[0]}
              onChange={(e) => setFilters({ ...filters, startTime: new Date(e.target.value).getTime() })}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            />
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600' }}>
              End Date
            </label>
            <input
              type="date"
              value={new Date(filters.endTime).toISOString().split('T')[0]}
              onChange={(e) => setFilters({ ...filters, endTime: new Date(e.target.value).getTime() })}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            />
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600' }}>
              Time Step
            </label>
            <select
              value={filters.timestep}
              onChange={(e) => setFilters({ ...filters, timestep: e.target.value })}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            >
              <option value="hour">Hour</option>
              <option value="day">Day</option>
              <option value="month">Month</option>
            </select>
          </div>
        </div>

        <button
          onClick={handleExport}
          disabled={loading}
          style={{
            padding: '0.75rem 1.5rem',
            backgroundColor: loading ? '#6c757d' : '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: loading ? 'not-allowed' : 'pointer',
            fontSize: '1rem'
          }}
        >
          {loading ? 'Exporting...' : 'Export as CSV'}
        </button>
      </div>
    </div>
  );
}
