import React, { useState, useEffect } from 'react';
import { useToast } from '../../components/Toast';
import api from '../../api/client';

const HttpAuthPreferences = () => {
  const [preferences, setPreferences] = useState([]);
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState({});
  const toast = useToast();

  useEffect(() => {
    fetchPreferences();
  }, []);

  const fetchPreferences = async () => {
    setLoading(true);
    try {
      const response = await api.get('/user-preferences/http-auth');
      setPreferences(response.data || []);
    } catch (error) {
      console.error('Failed to fetch preferences:', error);
      toast.error('Failed to load HTTP authentication preferences');
    } finally {
      setLoading(false);
    }
  };

  const handleToggleMessageAuth = async (pref) => {
    const newValue = !pref.useMessageAuth;
    await savePreference({
      ...pref,
      useMessageAuth: newValue
    });
  };

  const handleToggleMdnAuth = async (pref) => {
    const newValue = !pref.useMdnAuth;
    await savePreference({
      ...pref,
      useMdnAuth: newValue
    });
  };

  const handleMessageUsernameChange = (index, value) => {
    const updated = [...preferences];
    updated[index].messageUsername = value;
    setPreferences(updated);
  };

  const handleMessagePasswordChange = (index, value) => {
    const updated = [...preferences];
    updated[index].messagePassword = value;
    setPreferences(updated);
  };

  const handleMdnUsernameChange = (index, value) => {
    const updated = [...preferences];
    updated[index].mdnUsername = value;
    setPreferences(updated);
  };

  const handleMdnPasswordChange = (index, value) => {
    const updated = [...preferences];
    updated[index].mdnPassword = value;
    setPreferences(updated);
  };

  const savePreference = async (pref) => {
    setSaving(prev => ({ ...prev, [pref.partnerId]: true }));
    try {
      await api.post('/user-preferences/http-auth', {
        userId: pref.userId,
        partnerId: pref.partnerId,
        useMessageAuth: pref.useMessageAuth,
        messageUsername: pref.messageUsername || '',
        messagePassword: pref.messagePassword || '',
        useMdnAuth: pref.useMdnAuth,
        mdnUsername: pref.mdnUsername || '',
        mdnPassword: pref.mdnPassword || ''
      });
      toast.success(`Saved preferences for ${pref.partnerName}`);
      // Refresh to get latest data
      await fetchPreferences();
    } catch (error) {
      console.error('Failed to save preference:', error);
      toast.error('Failed to save preference: ' + (error.response?.data?.error || error.message));
    } finally {
      setSaving(prev => ({ ...prev, [pref.partnerId]: false }));
    }
  };

  const handleSave = async (index) => {
    const pref = preferences[index];
    await savePreference(pref);
  };

  const handleDelete = async (pref) => {
    if (!window.confirm(`Remove HTTP authentication preferences for ${pref.partnerName}?`)) {
      return;
    }

    try {
      await api.delete(`/user-preferences/http-auth/${pref.partnerId}`);
      toast.success(`Removed preferences for ${pref.partnerName}`);
      await fetchPreferences();
    } catch (error) {
      console.error('Failed to delete preference:', error);
      toast.error('Failed to delete preference: ' + (error.response?.data?.error || error.message));
    }
  };

  const containerStyle = {
    width: '100%'
  };

  const infoBoxStyle = {
    backgroundColor: '#e7f3ff',
    border: '1px solid #b3d9ff',
    borderRadius: '4px',
    padding: '1rem',
    marginBottom: '1.5rem',
    fontSize: '0.875rem',
    color: '#004085'
  };

  const tableContainerStyle = {
    overflowX: 'auto',
    border: '1px solid #dee2e6',
    borderRadius: '4px'
  };

  const tableStyle = {
    width: '100%',
    borderCollapse: 'collapse',
    fontSize: '0.875rem'
  };

  const thStyle = {
    backgroundColor: '#f8f9fa',
    padding: '0.75rem',
    textAlign: 'left',
    fontWeight: '600',
    borderBottom: '2px solid #dee2e6',
    whiteSpace: 'nowrap'
  };

  const tdStyle = {
    padding: '0.75rem',
    borderBottom: '1px solid #dee2e6',
    verticalAlign: 'middle'
  };

  const inputStyle = {
    width: '100%',
    padding: '0.375rem 0.5rem',
    fontSize: '0.875rem',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    boxSizing: 'border-box'
  };

  const checkboxStyle = {
    width: '18px',
    height: '18px',
    cursor: 'pointer',
    margin: '0 auto',
    display: 'block'
  };

  const buttonStyle = {
    padding: '0.375rem 0.75rem',
    fontSize: '0.75rem',
    fontWeight: '500',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    transition: 'all 0.2s ease',
    marginRight: '0.5rem'
  };

  const saveButtonStyle = {
    ...buttonStyle,
    backgroundColor: '#007bff',
    color: 'white'
  };

  const deleteButtonStyle = {
    ...buttonStyle,
    backgroundColor: '#dc3545',
    color: 'white'
  };

  const sectionHeaderStyle = {
    backgroundColor: '#f1f3f5',
    padding: '0.5rem 0.75rem',
    fontWeight: '600',
    fontSize: '0.8rem',
    textTransform: 'uppercase',
    color: '#495057',
    borderBottom: '1px solid #dee2e6'
  };

  if (loading) {
    return (
      <div style={{ textAlign: 'center', padding: '2rem', color: '#666' }}>
        Loading preferences...
      </div>
    );
  }

  if (preferences.length === 0) {
    return (
      <div style={containerStyle}>
        <div style={infoBoxStyle}>
          Configure HTTP authentication credentials for partners that use "User Preference" mode.
          When a partner is configured to use user preferences, the credentials you set here will be used
          for HTTP authentication when sending messages or MDNs.
        </div>
        <div style={{ textAlign: 'center', padding: '2rem', color: '#666' }}>
          No partners available. Partners must be configured to use "User Preference" mode for HTTP authentication.
        </div>
      </div>
    );
  }

  return (
    <div style={containerStyle}>
      <div style={infoBoxStyle}>
        <strong>HTTP Authentication Preferences</strong>
        <p style={{ margin: '0.5rem 0 0 0' }}>
          Configure HTTP authentication credentials for partners that use "User Preference" mode.
          When a partner is configured to use user preferences, the credentials you set here will be used
          for HTTP authentication when sending messages or MDNs.
        </p>
      </div>

      <div style={tableContainerStyle}>
        <table style={tableStyle}>
          <thead>
            <tr>
              <th style={thStyle} rowSpan="2">Partner</th>
              <th style={thStyle} rowSpan="2">AS2 ID</th>
              <th style={{ ...thStyle, textAlign: 'center' }} colSpan="3">Message Authentication</th>
              <th style={{ ...thStyle, textAlign: 'center' }} colSpan="3">MDN Authentication</th>
              <th style={thStyle} rowSpan="2">Actions</th>
            </tr>
            <tr>
              <th style={sectionHeaderStyle}>Enable</th>
              <th style={sectionHeaderStyle}>Username</th>
              <th style={sectionHeaderStyle}>Password</th>
              <th style={sectionHeaderStyle}>Enable</th>
              <th style={sectionHeaderStyle}>Username</th>
              <th style={sectionHeaderStyle}>Password</th>
            </tr>
          </thead>
          <tbody>
            {preferences.map((pref, index) => (
              <tr key={pref.partnerId || index}>
                <td style={tdStyle}>
                  <strong>{pref.partnerName || 'Unknown'}</strong>
                </td>
                <td style={tdStyle}>
                  <code style={{ fontSize: '0.8rem', color: '#495057' }}>
                    {pref.partnerAs2Id || 'N/A'}
                  </code>
                </td>

                {/* Message Authentication */}
                <td style={{ ...tdStyle, textAlign: 'center', width: '60px' }}>
                  <input
                    type="checkbox"
                    checked={pref.useMessageAuth || false}
                    onChange={() => handleToggleMessageAuth(pref)}
                    style={checkboxStyle}
                  />
                </td>
                <td style={{ ...tdStyle, minWidth: '150px' }}>
                  <input
                    type="text"
                    value={pref.messageUsername || ''}
                    onChange={(e) => handleMessageUsernameChange(index, e.target.value)}
                    disabled={!pref.useMessageAuth}
                    placeholder="Username"
                    style={{
                      ...inputStyle,
                      backgroundColor: pref.useMessageAuth ? 'white' : '#f8f9fa'
                    }}
                  />
                </td>
                <td style={{ ...tdStyle, minWidth: '150px' }}>
                  <input
                    type="password"
                    value={pref.messagePassword || ''}
                    onChange={(e) => handleMessagePasswordChange(index, e.target.value)}
                    disabled={!pref.useMessageAuth}
                    placeholder="Password"
                    style={{
                      ...inputStyle,
                      backgroundColor: pref.useMessageAuth ? 'white' : '#f8f9fa'
                    }}
                  />
                </td>

                {/* MDN Authentication */}
                <td style={{ ...tdStyle, textAlign: 'center', width: '60px' }}>
                  <input
                    type="checkbox"
                    checked={pref.useMdnAuth || false}
                    onChange={() => handleToggleMdnAuth(pref)}
                    style={checkboxStyle}
                  />
                </td>
                <td style={{ ...tdStyle, minWidth: '150px' }}>
                  <input
                    type="text"
                    value={pref.mdnUsername || ''}
                    onChange={(e) => handleMdnUsernameChange(index, e.target.value)}
                    disabled={!pref.useMdnAuth}
                    placeholder="Username"
                    style={{
                      ...inputStyle,
                      backgroundColor: pref.useMdnAuth ? 'white' : '#f8f9fa'
                    }}
                  />
                </td>
                <td style={{ ...tdStyle, minWidth: '150px' }}>
                  <input
                    type="password"
                    value={pref.mdnPassword || ''}
                    onChange={(e) => handleMdnPasswordChange(index, e.target.value)}
                    disabled={!pref.useMdnAuth}
                    placeholder="Password"
                    style={{
                      ...inputStyle,
                      backgroundColor: pref.useMdnAuth ? 'white' : '#f8f9fa'
                    }}
                  />
                </td>

                {/* Actions */}
                <td style={{ ...tdStyle, whiteSpace: 'nowrap' }}>
                  <button
                    onClick={() => handleSave(index)}
                    disabled={saving[pref.partnerId]}
                    style={saveButtonStyle}
                  >
                    {saving[pref.partnerId] ? 'Saving...' : 'Save'}
                  </button>
                  <button
                    onClick={() => handleDelete(pref)}
                    disabled={saving[pref.partnerId]}
                    style={deleteButtonStyle}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};

export default HttpAuthPreferences;
