import React, { useState } from 'react';
import HttpAuthPreferences from './HttpAuthPreferences';

const UserPreferences = () => {
  const [activeTab, setActiveTab] = useState('httpauth');

  const tabs = [
    { id: 'httpauth', label: 'HTTP Authentication' }
  ];

  const containerStyle = {
    padding: '1.5rem',
    maxWidth: '1400px',
    margin: '0 auto'
  };

  const headerStyle = {
    marginBottom: '1.5rem',
    paddingBottom: '1rem',
    borderBottom: '2px solid #e0e0e0'
  };

  const titleStyle = {
    fontSize: '1.5rem',
    fontWeight: '600',
    color: '#333',
    margin: 0
  };

  const tabsContainerStyle = {
    display: 'flex',
    gap: '0.5rem',
    marginBottom: '1.5rem',
    borderBottom: '1px solid #e0e0e0'
  };

  const tabButtonStyle = (isActive) => ({
    padding: '0.75rem 1.5rem',
    fontSize: '0.875rem',
    fontWeight: '500',
    border: 'none',
    borderBottom: isActive ? '2px solid #007bff' : '2px solid transparent',
    backgroundColor: 'transparent',
    color: isActive ? '#007bff' : '#666',
    cursor: 'pointer',
    transition: 'all 0.2s ease',
    ':hover': {
      color: '#007bff',
      backgroundColor: '#f8f9fa'
    }
  });

  const contentStyle = {
    backgroundColor: 'white',
    borderRadius: '8px',
    padding: '1.5rem',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)'
  };

  return (
    <div style={containerStyle}>
      <div style={headerStyle}>
        <h1 style={titleStyle}>User Preferences</h1>
      </div>

      <div style={tabsContainerStyle}>
        {tabs.map(tab => (
          <button
            key={tab.id}
            onClick={() => setActiveTab(tab.id)}
            style={tabButtonStyle(activeTab === tab.id)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      <div style={contentStyle}>
        {activeTab === 'httpauth' && <HttpAuthPreferences />}
      </div>
    </div>
  );
};

export default UserPreferences;
