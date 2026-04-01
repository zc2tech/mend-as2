import { useSystemInfo } from './useSystem';
import { LoadingPage } from '../../components/Loading';

export default function SystemInfo() {
  const { data: info, isLoading, error } = useSystemInfo();

  if (isLoading) {
    return <LoadingPage message="Loading system information..." />;
  }

  if (error) {
    return <div style={{ color: 'red' }}>Error loading system info: {error.message}</div>;
  }

  const cardStyle = {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '1.5rem'
  };

  const labelStyle = {
    fontWeight: '600',
    color: '#495057',
    marginBottom: '0.25rem'
  };

  const valueStyle = {
    color: '#212529',
    marginBottom: '1rem'
  };

  return (
    <div>
      <h1>System Information</h1>

      <div style={cardStyle}>
        <h2 style={{ marginTop: 0 }}>Server Details</h2>
        <div>
          <div style={labelStyle}>Product Name</div>
          <div style={valueStyle}>{info?.productName || 'Mendelson AS2 Server'}</div>

          <div style={labelStyle}>Version</div>
          <div style={valueStyle}>{info?.version || '1.1 build 67'}</div>

          <div style={labelStyle}>Build Date</div>
          <div style={valueStyle}>{info?.buildDate || '-'}</div>

          <div style={labelStyle}>Start Time</div>
          <div style={valueStyle}>
            {info?.startTime ? new Date(parseInt(info.startTime)).toLocaleString() : '-'}
          </div>

          <div style={labelStyle}>Capabilities</div>
          <div style={valueStyle}>{info?.capabilities || 'REST API, Partner Management, Message Monitoring'}</div>
        </div>
      </div>

      <div style={cardStyle}>
        <h2 style={{ marginTop: 0 }}>Environment</h2>
        <div>
          <div style={labelStyle}>Java Version</div>
          <div style={valueStyle}>{info?.javaVersion || '-'}</div>

          <div style={labelStyle}>Operating System</div>
          <div style={valueStyle}>{info?.os || '-'}</div>

          <div style={labelStyle}>CPU Cores</div>
          <div style={valueStyle}>{info?.cpuCores || '-'}</div>
        </div>
      </div>

      <div style={cardStyle}>
        <h2 style={{ marginTop: 0 }}>Actions</h2>
        <button
          style={{
            padding: '0.5rem 1rem',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            marginRight: '0.5rem'
          }}
          onClick={() => alert('Configuration check coming soon')}
        >
          Run Configuration Check
        </button>
        <button
          style={{
            padding: '0.5rem 1rem',
            backgroundColor: '#17a2b8',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer'
          }}
          onClick={() => alert('Connection test coming soon')}
        >
          Test Partner Connection
        </button>
      </div>
    </div>
  );
}
