import { useState, useEffect } from 'react';
import { useQuery } from '@tanstack/react-query';
import { LoadingPage } from '../../components/Loading';
import api from '../../api/client';

export default function SystemEvents() {
  const [limit, setLimit] = useState(100);
  const [severityError, setSeverityError] = useState(true);
  const [severityWarning, setSeverityWarning] = useState(true);
  const [severityInfo, setSeverityInfo] = useState(true);
  const [originSystem, setOriginSystem] = useState(true);
  const [originUser, setOriginUser] = useState(true);
  const [originTransaction, setOriginTransaction] = useState(true);
  const [category, setCategory] = useState(-1);
  const [searchText, setSearchText] = useState('');
  const [categories, setCategories] = useState([]);

  // Fetch categories
  useEffect(() => {
    const fetchCategories = async () => {
      try {
        const response = await api.get('/system/event-categories');
        setCategories(response.data || []);
      } catch (error) {
        console.error('Failed to fetch categories:', error);
      }
    };
    fetchCategories();
  }, []);

  const { data: events = [], isLoading, error, refetch } = useQuery({
    queryKey: ['systemEvents', limit, severityError, severityWarning, severityInfo,
               originSystem, originUser, originTransaction, category, searchText],
    queryFn: async () => {
      const params = new URLSearchParams();
      params.append('limit', limit);
      params.append('severityError', severityError);
      params.append('severityWarning', severityWarning);
      params.append('severityInfo', severityInfo);
      params.append('originSystem', originSystem);
      params.append('originUser', originUser);
      params.append('originTransaction', originTransaction);
      params.append('category', category);
      if (searchText.trim()) {
        params.append('searchText', searchText.trim());
      }
      const response = await api.get(`/system/events?${params.toString()}`);
      return response.data;
    }
  });

  if (isLoading) {
    return <LoadingPage message="Loading system events..." />;
  }

  if (error) {
    return <div style={{ color: 'red' }}>Error loading system events: {error.message}</div>;
  }

  const getSeverityStyle = (severity) => {
    switch (severity) {
      case 'ERROR':
        return { backgroundColor: '#dc3545', color: 'white', padding: '0.25rem 0.5rem', borderRadius: '4px', fontSize: '0.75rem', fontWeight: '600' };
      case 'WARNING':
        return { backgroundColor: '#ffc107', color: '#212529', padding: '0.25rem 0.5rem', borderRadius: '4px', fontSize: '0.75rem', fontWeight: '600' };
      case 'INFO':
        return { backgroundColor: '#17a2b8', color: 'white', padding: '0.25rem 0.5rem', borderRadius: '4px', fontSize: '0.75rem', fontWeight: '600' };
      default:
        return { backgroundColor: '#6c757d', color: 'white', padding: '0.25rem 0.5rem', borderRadius: '4px', fontSize: '0.75rem', fontWeight: '600' };
    }
  };

  const getOriginStyle = (origin) => {
    switch (origin) {
      case 'SYSTEM':
        return { color: '#007bff', fontWeight: '600' };
      case 'USER':
        return { color: '#28a745', fontWeight: '600' };
      case 'TRANSACTION':
        return { color: '#6f42c1', fontWeight: '600' };
      default:
        return { color: '#6c757d' };
    }
  };

  const filterCardStyle = {
    backgroundColor: 'white',
    padding: '1rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '1rem'
  };

  const filterSectionStyle = {
    marginBottom: '1rem'
  };

  const filterLabelStyle = {
    fontWeight: '600',
    fontSize: '0.875rem',
    marginBottom: '0.5rem',
    color: '#495057'
  };

  const checkboxGroupStyle = {
    display: 'flex',
    gap: '1rem',
    flexWrap: 'wrap'
  };

  const checkboxLabelStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '0.25rem',
    fontSize: '0.875rem',
    cursor: 'pointer'
  };

  const tableStyle = {
    width: '100%',
    borderCollapse: 'collapse',
    backgroundColor: 'white',
    borderRadius: '8px',
    overflow: 'hidden',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    fontSize: '0.875rem'
  };

  const thStyle = {
    textAlign: 'left',
    padding: '0.75rem',
    backgroundColor: '#f8f9fa',
    borderBottom: '2px solid #dee2e6',
    fontWeight: '600',
    color: '#495057',
    fontSize: '0.875rem'
  };

  const tdStyle = {
    padding: '0.75rem',
    borderBottom: '1px solid #dee2e6',
    verticalAlign: 'top'
  };

  const handleResetFilters = () => {
    setSeverityError(true);
    setSeverityWarning(true);
    setSeverityInfo(true);
    setOriginSystem(true);
    setOriginUser(true);
    setOriginTransaction(true);
    setCategory(-1);
    setSearchText('');
  };

  return (
    <div>
      {/* Filters */}
      <div style={filterCardStyle}>
        <h3 style={{ marginTop: 0, marginBottom: '1rem' }}>Filters</h3>

        {/* Severity Filter */}
        <div style={filterSectionStyle}>
          <div style={filterLabelStyle}>Severity</div>
          <div style={checkboxGroupStyle}>
            <label style={checkboxLabelStyle}>
              <input
                type="checkbox"
                checked={severityError}
                onChange={(e) => setSeverityError(e.target.checked)}
              />
              <span style={{ color: '#dc3545', fontWeight: '600' }}>Error</span>
            </label>
            <label style={checkboxLabelStyle}>
              <input
                type="checkbox"
                checked={severityWarning}
                onChange={(e) => setSeverityWarning(e.target.checked)}
              />
              <span style={{ color: '#ffc107', fontWeight: '600' }}>Warning</span>
            </label>
            <label style={checkboxLabelStyle}>
              <input
                type="checkbox"
                checked={severityInfo}
                onChange={(e) => setSeverityInfo(e.target.checked)}
              />
              <span style={{ color: '#17a2b8', fontWeight: '600' }}>Info</span>
            </label>
          </div>
        </div>

        {/* Origin Filter */}
        <div style={filterSectionStyle}>
          <div style={filterLabelStyle}>Origin</div>
          <div style={checkboxGroupStyle}>
            <label style={checkboxLabelStyle}>
              <input
                type="checkbox"
                checked={originSystem}
                onChange={(e) => setOriginSystem(e.target.checked)}
              />
              <span style={{ color: '#007bff', fontWeight: '600' }}>System</span>
            </label>
            <label style={checkboxLabelStyle}>
              <input
                type="checkbox"
                checked={originUser}
                onChange={(e) => setOriginUser(e.target.checked)}
              />
              <span style={{ color: '#28a745', fontWeight: '600' }}>User</span>
            </label>
            <label style={checkboxLabelStyle}>
              <input
                type="checkbox"
                checked={originTransaction}
                onChange={(e) => setOriginTransaction(e.target.checked)}
              />
              <span style={{ color: '#6f42c1', fontWeight: '600' }}>Transaction</span>
            </label>
          </div>
        </div>

        {/* Category Filter */}
        <div style={filterSectionStyle}>
          <div style={filterLabelStyle}>Category</div>
          <select
            value={category}
            onChange={(e) => setCategory(Number(e.target.value))}
            style={{
              padding: '0.375rem 0.75rem',
              border: '1px solid #ced4da',
              borderRadius: '4px',
              fontSize: '0.875rem',
              minWidth: '200px'
            }}
          >
            <option value={-1}>All Categories</option>
            {categories.map((cat) => (
              <option key={cat.value} value={cat.value}>
                {cat.name}
              </option>
            ))}
          </select>
        </div>

        {/* Search Text Filter */}
        <div style={filterSectionStyle}>
          <div style={filterLabelStyle}>Search Text (in Subject, Body, or ID)</div>
          <input
            type="text"
            value={searchText}
            onChange={(e) => setSearchText(e.target.value)}
            placeholder="Enter search text..."
            style={{
              padding: '0.375rem 0.75rem',
              border: '1px solid #ced4da',
              borderRadius: '4px',
              fontSize: '0.875rem',
              width: '100%',
              maxWidth: '400px'
            }}
          />
        </div>

        {/* Limit and Actions */}
        <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center', marginTop: '1rem' }}>
          <label style={{ fontSize: '0.875rem' }}>Show:</label>
          <select
            value={limit}
            onChange={(e) => setLimit(Number(e.target.value))}
            style={{
              padding: '0.375rem 0.75rem',
              border: '1px solid #ced4da',
              borderRadius: '4px',
              fontSize: '0.875rem'
            }}
          >
            <option value={50}>50 events</option>
            <option value={100}>100 events</option>
            <option value={200}>200 events</option>
            <option value={500}>500 events</option>
          </select>
          <button
            onClick={handleResetFilters}
            style={{
              padding: '0.375rem 0.75rem',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '0.875rem'
            }}
          >
            Reset Filters
          </button>
          <button
            onClick={() => refetch()}
            style={{
              padding: '0.375rem 0.75rem',
              backgroundColor: '#007bff',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '0.875rem'
            }}
          >
            Refresh
          </button>
        </div>
      </div>

      {/* Results */}
      <div style={{ marginBottom: '1rem', fontSize: '0.875rem', color: '#6c757d' }}>
        Showing {events.length} events
      </div>

      {events.length === 0 ? (
        <div style={{ padding: '2rem', textAlign: 'center', color: '#6c757d', backgroundColor: 'white', borderRadius: '8px' }}>
          No system events found matching the current filters
        </div>
      ) : (
        <table style={tableStyle}>
          <thead>
            <tr>
              <th style={{...thStyle, width: '160px'}}>Timestamp</th>
              <th style={{...thStyle, width: '90px'}}>Severity</th>
              <th style={{...thStyle, width: '110px'}}>Origin</th>
              <th style={thStyle}>Subject</th>
              <th style={{...thStyle, width: '100px'}}>User</th>
              <th style={{...thStyle, width: '120px'}}>Host</th>
            </tr>
          </thead>
          <tbody>
            {events.map((event, index) => (
              <tr key={event.id || index} style={{ backgroundColor: index % 2 === 0 ? 'white' : '#f8f9fa' }}>
                <td style={tdStyle}>{event.timestamp}</td>
                <td style={tdStyle}>
                  <span style={getSeverityStyle(event.severity)}>{event.severity}</span>
                </td>
                <td style={tdStyle}>
                  <span style={getOriginStyle(event.origin)}>{event.origin}</span>
                </td>
                <td style={tdStyle}>
                  <div style={{ fontWeight: '600', marginBottom: '0.25rem' }}>{event.subject}</div>
                  {event.body && (
                    <div style={{ color: '#6c757d', fontSize: '0.8rem', maxHeight: '60px', overflow: 'auto' }}>
                      {event.body}
                    </div>
                  )}
                </td>
                <td style={tdStyle}>{event.userId || '-'}</td>
                <td style={tdStyle}>{event.processOriginHost || '-'}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
