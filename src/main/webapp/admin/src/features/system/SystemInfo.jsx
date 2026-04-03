import { useState } from 'react';
import HTTPServerConfig from './HTTPServerConfig';
import SystemEvents from './SystemEvents';
import ServerLogSearch from './ServerLogSearch';

export default function SystemInfo() {
  const [activeTab, setActiveTab] = useState('httpConfig');

  const tabStyle = {
    display: 'flex',
    borderBottom: '2px solid #dee2e6',
    marginBottom: '1.5rem',
    gap: '0.5rem'
  };

  const tabButtonStyle = (isActive) => ({
    padding: '0.75rem 1.5rem',
    backgroundColor: isActive ? '#007bff' : 'transparent',
    color: isActive ? 'white' : '#495057',
    border: 'none',
    borderBottom: isActive ? '2px solid #007bff' : '2px solid transparent',
    cursor: 'pointer',
    fontWeight: isActive ? '600' : 'normal',
    fontSize: '0.9rem',
    transition: 'all 0.2s',
    borderRadius: '4px 4px 0 0'
  });

  return (
    <div>
      <h1>System</h1>

      <div style={tabStyle}>
        <button
          style={tabButtonStyle(activeTab === 'httpConfig')}
          onClick={() => setActiveTab('httpConfig')}
        >
          HTTP Server Configuration
        </button>
        <button
          style={tabButtonStyle(activeTab === 'events')}
          onClick={() => setActiveTab('events')}
        >
          System Events
        </button>
        <button
          style={tabButtonStyle(activeTab === 'serverlog')}
          onClick={() => setActiveTab('serverlog')}
        >
          Search in Server Log
        </button>
      </div>

      {activeTab === 'httpConfig' && <HTTPServerConfig />}
      {activeTab === 'events' && <SystemEvents />}
      {activeTab === 'serverlog' && <ServerLogSearch />}
    </div>
  );
}
