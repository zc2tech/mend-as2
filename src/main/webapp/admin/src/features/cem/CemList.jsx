import { useCemEntries } from './useCem';
import { LoadingPage } from '../../components/Loading';

export default function CemList() {
  const { data: entries, isLoading, error } = useCemEntries();

  if (isLoading) {
    return <LoadingPage message="Loading CEM entries..." />;
  }

  if (error) {
    return <div style={{ color: 'red' }}>Error loading CEM entries: {error.message}</div>;
  }

  const tableStyle = {
    width: '100%',
    borderCollapse: 'collapse',
    backgroundColor: 'white',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    borderRadius: '8px',
    overflow: 'hidden'
  };

  const thStyle = {
    textAlign: 'left',
    padding: '1rem',
    backgroundColor: '#f8f9fa',
    borderBottom: '2px solid #dee2e6',
    fontWeight: '600'
  };

  const tdStyle = {
    padding: '0.75rem 1rem',
    borderBottom: '1px solid #dee2e6'
  };

  return (
    <div>
      <div style={{ marginBottom: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 style={{ margin: 0 }}>Certificate Exchange Mechanism (CEM)</h1>
        <button
          style={{
            padding: '0.5rem 1rem',
            backgroundColor: '#28a745',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
          onClick={() => alert('Send CEM request coming soon')}
        >
          Send CEM Request
        </button>
      </div>

      <table style={tableStyle}>
        <thead>
          <tr>
            <th style={thStyle}>Request ID</th>
            <th style={thStyle}>Initiator</th>
            <th style={thStyle}>Receiver</th>
            <th style={thStyle}>Category</th>
            <th style={thStyle}>Status</th>
            <th style={thStyle}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {!entries || entries.length === 0 ? (
            <tr>
              <td colSpan="6" style={{ ...tdStyle, textAlign: 'center', padding: '2rem' }}>
                No CEM entries found
              </td>
            </tr>
          ) : (
            entries.map((entry, index) => (
              <tr key={entry.requestId || index}>
                <td style={tdStyle}>{entry.requestId || '-'}</td>
                <td style={tdStyle}>{entry.initiatorAS2Id || '-'}</td>
                <td style={tdStyle}>{entry.receiverAS2Id || '-'}</td>
                <td style={tdStyle}>{entry.category || '-'}</td>
                <td style={tdStyle}>{entry.status || '-'}</td>
                <td style={tdStyle}>
                  <button
                    style={{
                      padding: '0.375rem 0.75rem',
                      marginRight: '0.5rem',
                      backgroundColor: '#ffc107',
                      color: '#000',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      fontSize: '0.875rem'
                    }}
                    onClick={() => alert('Cancel CEM coming soon')}
                  >
                    Cancel
                  </button>
                  <button
                    style={{
                      padding: '0.375rem 0.75rem',
                      backgroundColor: '#dc3545',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      fontSize: '0.875rem'
                    }}
                    onClick={() => alert('Delete CEM coming soon')}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}
