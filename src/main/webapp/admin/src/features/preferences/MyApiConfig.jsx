/*
 * Copyright (C) 2026 Julian Xu
 * Email: julian.xu@aliyun.com
 * GitHub: https://github.com/zc2tech
 *
 * This file is part of mend-as2, a fork of mendelson AS2.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

import { useState, useEffect } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { LoadingPage } from '../../components/Loading';
import { useToast } from '../../components/Toast';
import { useAuth } from '../auth/useAuth';
import api from '../../api/client';

export default function MyApiConfig() {
  const [activeTab, setActiveTab] = useState('response'); // 'response' or 'auth'

  const tabStyle = {
    padding: '0.75rem 1.5rem',
    border: 'none',
    backgroundColor: 'transparent',
    cursor: 'pointer',
    fontSize: '1rem',
    fontWeight: '600',
    borderBottom: '3px solid transparent',
    transition: 'all 0.2s'
  };

  const activeTabStyle = {
    ...tabStyle,
    borderBottom: '3px solid #007bff',
    color: '#007bff'
  };

  const inactiveTabStyle = {
    ...tabStyle,
    color: '#6c757d'
  };

  return (
    <div>
      {/* Tab Navigation */}
      <div style={{
        borderBottom: '2px solid #dee2e6',
        marginBottom: '1.5rem',
        display: 'flex',
        gap: '1rem'
      }}>
        <button
          style={activeTab === 'response' ? activeTabStyle : inactiveTabStyle}
          onClick={() => setActiveTab('response')}
        >
          My REST API Response
        </button>
        <button
          style={activeTab === 'auth' ? activeTabStyle : inactiveTabStyle}
          onClick={() => setActiveTab('auth')}
        >
          My REST API Auth
        </button>
      </div>

      {/* Tab Content */}
      {activeTab === 'response' && <MyApiResponseConfig />}
      {activeTab === 'auth' && <MyApiAuthConfig />}
    </div>
  );
}

// Auth Configuration Component (extracted from original MyApiConfig)
function MyApiAuthConfig() {
  const toast = useToast();
  const { user } = useAuth();
  const queryClient = useQueryClient();

  // State
  const [basicAuthEnabled, setBasicAuthEnabled] = useState(false);
  const [certAuthEnabled, setCertAuthEnabled] = useState(false);
  const [basicAuthList, setBasicAuthList] = useState([]);
  const [certAuthList, setCertAuthList] = useState([]);
  const [certificates, setCertificates] = useState([]);
  const [passwordVisibility, setPasswordVisibility] = useState({});

  // Load data
  const { isLoading } = useQuery({
    queryKey: ['userApiAuth'],
    queryFn: async () => {
      const [configRes, certsRes] = await Promise.all([
        api.get('/user/api-auth/config'),
        api.get('/certificates', { params: { keystoreType: 'sign' } })
      ]);

      const config = configRes.data;
      setBasicAuthEnabled(config.basicAuthEnabled === true);
      setCertAuthEnabled(config.certAuthEnabled === true);

      const credentials = config.credentialsList || [];
      setBasicAuthList(credentials.filter(c => c.authType === 1));
      setCertAuthList(credentials.filter(c => c.authType === 2));

      const publicCerts = (certsRes.data || []).filter(cert => cert.isKeyPair === false);
      setCertificates(publicCerts);

      return config;
    },
    staleTime: 0,
    refetchOnMount: 'always',
    refetchOnWindowFocus: false
  });

  // Save mutation
  const saveMutation = useMutation({
    mutationFn: async () => {
      const config = {
        basicAuthEnabled,
        certAuthEnabled,
        credentialsList: [...basicAuthList, ...certAuthList]
      };
      const response = await api.post('/user/api-auth/config', config);
      return response.data;
    },
    onSuccess: async (savedConfig) => {
      toast.success('API authentication settings saved successfully');
      queryClient.setQueryData(['userApiAuth'], savedConfig);
      setBasicAuthEnabled(savedConfig.basicAuthEnabled === true);
      setCertAuthEnabled(savedConfig.certAuthEnabled === true);

      const credentials = savedConfig.credentialsList || [];
      setBasicAuthList(credentials.filter(c => c.authType === 1));
      setCertAuthList(credentials.filter(c => c.authType === 2));
    },
    onError: (error) => {
      toast.error('Failed to save: ' + (error.response?.data?.error || error.message));
    }
  });

  // Basic Auth operations
  const addBasicAuthRow = () => {
    setBasicAuthList([...basicAuthList, {
      dbId: -1,
      authType: 1,
      username: '',
      password: '',
      certFingerprint: '',
      certAlias: '',
      enabled: true
    }]);
  };

  const removeBasicAuthRow = (index) => {
    setBasicAuthList(basicAuthList.filter((_, i) => i !== index));
    const newVisibility = { ...passwordVisibility };
    delete newVisibility[`basic-${index}`];
    setPasswordVisibility(newVisibility);
  };

  const updateBasicAuthRow = (index, field, value) => {
    const updated = [...basicAuthList];
    updated[index][field] = value;
    setBasicAuthList(updated);
  };

  const togglePasswordVisibility = (index) => {
    const key = `basic-${index}`;
    setPasswordVisibility({
      ...passwordVisibility,
      [key]: !passwordVisibility[key]
    });
  };

  // Cert Auth operations
  const addCertAuthRow = () => {
    if (certificates.length > 0) {
      const firstCert = certificates[0];
      setCertAuthList([...certAuthList, {
        dbId: -1,
        authType: 2,
        username: '',
        password: '',
        certAlias: firstCert.alias,
        certFingerprint: firstCert.fingerprintSHA1,
        enabled: true
      }]);
    } else {
      toast.warning('No certificates available. Please add certificates in My Sign/Crypt/Auth first.');
    }
  };

  const removeCertAuthRow = (index) => {
    setCertAuthList(certAuthList.filter((_, i) => i !== index));
  };

  const updateCertAuthRow = (index, alias) => {
    const selectedCert = certificates.find(c => c.alias === alias);
    if (selectedCert) {
      const updated = [...certAuthList];
      updated[index].certAlias = alias;
      updated[index].certFingerprint = selectedCert.fingerprintSHA1;
      setCertAuthList(updated);
    }
  };

  const toggleCertEnabled = (index) => {
    const updated = [...certAuthList];
    updated[index].enabled = !updated[index].enabled;
    setCertAuthList(updated);
  };

  if (isLoading) {
    return <LoadingPage message="Loading API authentication settings..." />;
  }

  // Styles (same as before)
  const cardStyle = {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '1.5rem'
  };

  const tableStyle = {
    width: '100%',
    borderCollapse: 'collapse',
    marginTop: '1rem'
  };

  const thStyle = {
    textAlign: 'left',
    padding: '0.5rem',
    borderBottom: '2px solid #dee2e6',
    fontWeight: '600',
    backgroundColor: '#f8f9fa'
  };

  const tdStyle = {
    padding: '0.5rem',
    borderBottom: '1px solid #dee2e6'
  };

  const inputStyle = {
    padding: '0.5rem',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    fontSize: '0.875rem',
    width: '100%'
  };

  const passwordContainerStyle = {
    position: 'relative',
    display: 'flex',
    alignItems: 'center'
  };

  const eyeIconStyle = {
    position: 'absolute',
    right: '10px',
    cursor: 'pointer',
    fontSize: '1.2rem',
    userSelect: 'none',
    color: '#6c757d'
  };

  const saveButtonStyle = {
    padding: '0.5rem 1rem',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.875rem',
    fontWeight: '600'
  };

  const addButtonStyle = {
    padding: '0.5rem 1rem',
    backgroundColor: '#28a745',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    marginTop: '1rem',
    fontSize: '0.875rem'
  };

  const deleteButtonStyle = {
    padding: '0.5rem 1rem',
    backgroundColor: '#dc3545',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.875rem'
  };

  const hostname = window.location.hostname;
  const httpsPort = window.location.protocol === 'https:' ? (window.location.port || '8443') : '8443';
  const httpsTrackerUrl = `https://${hostname}:${httpsPort}/as2/userapi/${user.username}`;

  const copyToClipboard = (url, protocol) => {
    navigator.clipboard.writeText(url);
    alert(`${protocol.toUpperCase()} API URL copied to clipboard!`);
  };

  const canAddCert = certificates.length > 0;

  return (
    <div>
      {/* Header Card */}
      <div style={cardStyle}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h2 style={{ margin: 0 }}>Authentication Configuration</h2>
          <button
            onClick={() => saveMutation.mutate()}
            disabled={saveMutation.isPending}
            style={saveButtonStyle}
          >
            {saveMutation.isPending ? 'Saving...' : 'Save Configuration'}
          </button>
        </div>
        <p style={{ color: '#6c757d', fontSize: '0.875rem' }}>
          Configure authentication for your personal REST API endpoint.
          Your base endpoint accepts any sub-path and HTTP method(GET,POST,PUT,DELETE) - authentication applies to all requests.
        </p>

        <div style={{
          marginBottom: '1rem',
          padding: '0.75rem 1rem',
          backgroundColor: '#e7f3ff',
          border: '1px solid #b3d9ff',
          borderRadius: '8px'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', flexWrap: 'wrap' }}>
            <strong style={{ fontSize: '0.875rem', color: '#0056b3', marginRight: '0.5rem' }}>
              Base URL:
            </strong>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <code style={{
                fontSize: '0.875rem',
                padding: '0.375rem 0.5rem',
                backgroundColor: 'white',
                border: '1px solid #b3d9ff',
                borderRadius: '4px',
                display: 'inline-block',
                maxWidth: 'fit-content'
              }}>
                {httpsTrackerUrl}
              </code>
              <button
                onClick={() => copyToClipboard(httpsTrackerUrl, 'base')}
                style={{
                  padding: '0.375rem 0.75rem',
                  backgroundColor: '#007bff',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  fontSize: '0.8rem',
                  fontWeight: '600',
                  whiteSpace: 'nowrap'
                }}
              >
                📋 Copy
              </button>
            </div>
          </div>
        </div>
        <p style={{ color: '#6c757d', fontSize: '0.875rem', marginTop: '1rem' }}>
          Your API base endpoint accepts <strong>any sub-path</strong> with <strong>any HTTP method</strong> (GET, POST, PUT, DELETE).
          All requests are authenticated using the settings below and logged for your review.
        </p>
        <div style={{
          marginTop: '1rem',
          padding: '1rem',
          backgroundColor: '#f8f9fa',
          border: '1px solid #dee2e6',
          borderRadius: '8px'
        }}>
          <p style={{ margin: '0 0 0.5rem 0', fontWeight: '600', fontSize: '0.875rem' }}>Examples:</p>
          <ul style={{ margin: 0, paddingLeft: '1.5rem', fontSize: '0.875rem', color: '#6c757d' }}>
            <li><code>GET {httpsTrackerUrl}/orders</code></li>
            <li><code>POST {httpsTrackerUrl}/webhook/incoming</code></li>
            <li><code>PUT {httpsTrackerUrl}/data/update</code></li>
            <li><code>DELETE {httpsTrackerUrl}/cache/clear</code></li>
          </ul>
        </div>
      </div>

      {/* Authentication Settings Card */}
      <div style={cardStyle}>
        <h3 style={{ marginTop: 0 }}>Authentication Settings</h3>
        <p style={{ color: '#6c757d', fontSize: '0.875rem', marginBottom: '1.5rem' }}>
          These authentication settings apply to <strong>all endpoints</strong> defined above.
          Enable at least one authentication method to secure your API.
        </p>

        <div style={{ marginBottom: '1rem' }}>
          <label style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
            <input
              type="checkbox"
              checked={basicAuthEnabled}
              onChange={(e) => setBasicAuthEnabled(e.target.checked)}
              style={{ marginRight: '0.5rem' }}
            />
            <span>Enable Basic Authentication</span>
          </label>
        </div>

        <div style={{ marginBottom: '1.5rem' }}>
          <label style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
            <input
              type="checkbox"
              checked={certAuthEnabled}
              onChange={(e) => setCertAuthEnabled(e.target.checked)}
              style={{ marginRight: '0.5rem' }}
            />
            <span>Enable Certificate Authentication</span>
          </label>
        </div>
      </div>

      {/* Basic Auth Table */}
      <div style={cardStyle}>
        <h3 style={{ marginTop: 0 }}>Basic Authentication Credentials</h3>
        <p style={{ color: '#6c757d', fontSize: '0.875rem' }}>
          Add username/password pairs. Messages will be accepted if ANY enabled credential matches.
        </p>

        {basicAuthList.length === 0 ? (
          <p style={{ color: '#999', fontStyle: 'italic', marginTop: '1rem' }}>
            No credentials configured. Click "Add Credential" to add one.
          </p>
        ) : (
          <table style={tableStyle}>
            <thead>
              <tr>
                <th style={thStyle}>#</th>
                <th style={thStyle}>Username</th>
                <th style={thStyle}>Password</th>
                <th style={thStyle}>Enabled</th>
                <th style={thStyle}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {basicAuthList.map((cred, index) => (
                <tr key={index}>
                  <td style={tdStyle}>{index + 1}</td>
                  <td style={tdStyle}>
                    <input
                      type="text"
                      value={cred.username}
                      onChange={(e) => updateBasicAuthRow(index, 'username', e.target.value)}
                      style={inputStyle}
                      disabled={!basicAuthEnabled}
                      placeholder="username"
                    />
                  </td>
                  <td style={tdStyle}>
                    <div style={passwordContainerStyle}>
                      <input
                        type={passwordVisibility[`basic-${index}`] ? 'text' : 'password'}
                        value={cred.password}
                        onChange={(e) => updateBasicAuthRow(index, 'password', e.target.value)}
                        style={inputStyle}
                        disabled={!basicAuthEnabled}
                        placeholder="password"
                      />
                      <span
                        style={eyeIconStyle}
                        onClick={() => togglePasswordVisibility(index)}
                      >
                        {passwordVisibility[`basic-${index}`] ? '👁️' : '👁️‍🗨️'}
                      </span>
                    </div>
                  </td>
                  <td style={tdStyle}>
                    <input
                      type="checkbox"
                      checked={cred.enabled}
                      onChange={(e) => updateBasicAuthRow(index, 'enabled', e.target.checked)}
                      disabled={!basicAuthEnabled}
                    />
                  </td>
                  <td style={tdStyle}>
                    <button
                      onClick={() => removeBasicAuthRow(index)}
                      style={deleteButtonStyle}
                      disabled={!basicAuthEnabled}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        <button
          onClick={addBasicAuthRow}
          style={addButtonStyle}
          disabled={!basicAuthEnabled}
        >
          Add Credential
        </button>
      </div>

      {/* Certificate Auth Table */}
      <div style={cardStyle}>
        <h3 style={{ marginTop: 0 }}>Certificate Authentication</h3>
        <p style={{ color: '#6c757d', fontSize: '0.875rem' }}>
          Add certificates from your Sign/Crypt keystore. Messages will be accepted if the client certificate matches ANY enabled credential.
        </p>

        {certificates.length === 0 && (
          <div style={{
            padding: '1rem',
            backgroundColor: '#fff3cd',
            border: '1px solid #ffc107',
            borderRadius: '4px',
            marginTop: '1rem',
            marginBottom: '1rem'
          }}>
            <strong>⚠️ No certificates available</strong>
            <p style={{ marginTop: '0.5rem', marginBottom: 0 }}>
              Please add certificates in <strong>My Sign/Crypt/Auth</strong> first, then return here to configure tracker authentication.
            </p>
          </div>
        )}

        {certAuthList.length === 0 ? (
          <p style={{ color: '#999', fontStyle: 'italic', marginTop: '1rem' }}>
            No certificates configured. {certificates.length > 0 ? 'Click "Add Certificate" to add one.' : ''}
          </p>
        ) : (
          <table style={tableStyle}>
            <thead>
              <tr>
                <th style={thStyle}>#</th>
                <th style={thStyle}>Certificate</th>
                <th style={thStyle}>Fingerprint (SHA-1)</th>
                <th style={thStyle}>Enabled</th>
                <th style={thStyle}>Actions</th>
              </tr>
            </thead>
            <tbody>
              {certAuthList.map((cred, index) => (
                <tr key={index}>
                  <td style={tdStyle}>{index + 1}</td>
                  <td style={tdStyle}>
                    <select
                      value={cred.certAlias}
                      onChange={(e) => updateCertAuthRow(index, e.target.value)}
                      style={inputStyle}
                      disabled={!certAuthEnabled}
                    >
                      {certificates.map(cert => (
                        <option key={cert.alias} value={cert.alias}>
                          {cert.alias}
                        </option>
                      ))}
                    </select>
                  </td>
                  <td style={tdStyle}>
                    <code style={{ fontSize: '0.75rem', wordBreak: 'break-all' }}>
                      {cred.certFingerprint}
                    </code>
                  </td>
                  <td style={tdStyle}>
                    <input
                      type="checkbox"
                      checked={cred.enabled}
                      onChange={() => toggleCertEnabled(index)}
                      disabled={!certAuthEnabled}
                    />
                  </td>
                  <td style={tdStyle}>
                    <button
                      onClick={() => removeCertAuthRow(index)}
                      style={deleteButtonStyle}
                      disabled={!certAuthEnabled}
                    >
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        <button
          onClick={addCertAuthRow}
          style={{
            ...addButtonStyle,
            opacity: canAddCert ? 1 : 0.5,
            cursor: canAddCert ? 'pointer' : 'not-allowed'
          }}
          disabled={!canAddCert}
        >
          Add Certificate
        </button>
        {certificates.length === 0 && (
          <span style={{ marginLeft: '1rem', color: '#6c757d', fontSize: '0.875rem' }}>
            No certificates available
          </span>
        )}
      </div>

      {/* Bottom Save Button */}
      <div style={cardStyle}>
        <button
          onClick={() => saveMutation.mutate()}
          disabled={saveMutation.isPending}
          style={saveButtonStyle}
        >
          {saveMutation.isPending ? 'Saving...' : 'Save Configuration'}
        </button>
      </div>
    </div>
  );
}

// Response Configuration Component
function MyApiResponseConfig() {
  const toast = useToast();
  const { user } = useAuth();
  const queryClient = useQueryClient();
  const [rules, setRules] = useState([]);
  const [hasUnsavedChanges, setHasUnsavedChanges] = useState(false);

  // Load rules
  const { isLoading } = useQuery({
    queryKey: ['userApiResponse'],
    queryFn: async () => {
      const response = await api.get('/user/api-response/rules');
      setRules(response.data || []);
      setHasUnsavedChanges(false); // Reset unsaved flag on load
      return response.data;
    },
    staleTime: 0,
    refetchOnMount: 'always',
    refetchOnWindowFocus: false
  });

  // Warn user before leaving with unsaved changes
  useEffect(() => {
    const handleBeforeUnload = (e) => {
      if (hasUnsavedChanges) {
        e.preventDefault();
        e.returnValue = ''; // Chrome requires returnValue to be set
      }
    };

    window.addEventListener('beforeunload', handleBeforeUnload);
    return () => window.removeEventListener('beforeunload', handleBeforeUnload);
  }, [hasUnsavedChanges]);

  // Save all rules mutation
  const saveAllMutation = useMutation({
    mutationFn: async () => {
      // Save all rules in sequence
      const promises = rules.map(rule => {
        if (rule.id && rule.id > 0) {
          // Update existing rule
          return api.put(`/user/api-response/rules/${rule.id}`, rule);
        } else {
          // Create new rule (shouldn't happen in save-all, but handle it)
          return api.post('/user/api-response/rules', rule);
        }
      });

      // Also update priorities in case they changed
      if (rules.length > 0) {
        await api.put('/user/api-response/rules/reorder', { rules });
      }

      return Promise.all(promises);
    },
    onSuccess: () => {
      toast.success('All rules saved successfully');
      setHasUnsavedChanges(false);
      queryClient.invalidateQueries(['userApiResponse']);
    },
    onError: (error) => {
      toast.error('Failed to save rules: ' + (error.response?.data?.error || error.message));
    }
  });

  // Create rule mutation (for Add New Rule button)
  const createMutation = useMutation({
    mutationFn: async (rule) => {
      const response = await api.post('/user/api-response/rules', rule);
      return response.data;
    },
    onSuccess: (newRule) => {
      toast.success('Rule created successfully');
      setRules([...rules, newRule]);
      setHasUnsavedChanges(false); // Just created, no unsaved changes
      queryClient.invalidateQueries(['userApiResponse']);
    },
    onError: (error) => {
      toast.error('Failed to create rule: ' + (error.response?.data?.error || error.message));
    }
  });

  // Delete rule mutation
  const deleteMutation = useMutation({
    mutationFn: async (ruleId) => {
      await api.delete(`/user/api-response/rules/${ruleId}`);
      return ruleId;
    },
    onSuccess: (ruleId) => {
      toast.success('Rule deleted successfully');
      setRules(rules.filter(r => r.id !== ruleId));
      setHasUnsavedChanges(false);
      queryClient.invalidateQueries(['userApiResponse']);
    },
    onError: (error) => {
      toast.error('Failed to delete rule: ' + (error.response?.data?.error || error.message));
    }
  });

  const addNewRule = () => {
    const newRule = {
      // Don't send id field for new rules - let database auto-generate it
      priority: rules.length,
      enabled: true,
      httpMethod: 'GET',
      pathPattern: '/',
      pathMatchType: 'exact',
      statusCode: 200,
      contentType: 'application/json',
      responseBody: '{"message": "Hello World"}'
    };
    createMutation.mutate(newRule);
  };

  const updateRule = (index, field, value) => {
    const updated = [...rules];
    updated[index][field] = value;
    setRules(updated);
    setHasUnsavedChanges(true); // Mark as changed
  };

  const deleteRule = (ruleId) => {
    if (window.confirm('Are you sure you want to delete this rule?')) {
      deleteMutation.mutate(ruleId);
    }
  };

  const moveRuleUp = (index) => {
    if (index === 0) return;
    const updated = [...rules];
    [updated[index - 1], updated[index]] = [updated[index], updated[index - 1]];
    updated.forEach((rule, idx) => rule.priority = idx);
    setRules(updated);
    setHasUnsavedChanges(true); // Mark as changed
  };

  const moveRuleDown = (index) => {
    if (index === rules.length - 1) return;
    const updated = [...rules];
    [updated[index], updated[index + 1]] = [updated[index + 1], updated[index]];
    updated.forEach((rule, idx) => rule.priority = idx);
    setRules(updated);
    setHasUnsavedChanges(true); // Mark as changed
  };

  const discardChanges = () => {
    if (window.confirm('Discard all unsaved changes?')) {
      queryClient.invalidateQueries(['userApiResponse']);
      setHasUnsavedChanges(false);
    }
  };

  if (isLoading) {
    return <LoadingPage message="Loading response rules..." />;
  }

  const cardStyle = {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '1.5rem'
  };

  const inputStyle = {
    padding: '0.5rem',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    fontSize: '0.875rem',
    width: '100%'
  };

  const selectStyle = {
    ...inputStyle,
    width: 'auto',
    minWidth: '120px'
  };

  const textareaStyle = {
    ...inputStyle,
    minHeight: '100px',
    fontFamily: 'monospace',
    fontSize: '0.8rem'
  };

  const addButtonStyle = {
    padding: '0.5rem 1rem',
    backgroundColor: '#28a745',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.875rem',
    fontWeight: '600'
  };

  const saveButtonStyle = {
    padding: '0.4rem 0.8rem',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.8rem',
    marginRight: '0.5rem'
  };

  const deleteButtonStyle = {
    padding: '0.4rem 0.8rem',
    backgroundColor: '#dc3545',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.8rem',
    marginRight: '0.5rem'
  };

  const moveButtonStyle = {
    padding: '0.2rem 0.5rem',
    backgroundColor: '#6c757d',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.75rem',
    marginRight: '0.25rem'
  };

  return (
    <div>
      {/* Unsaved Changes Warning */}
      {hasUnsavedChanges && (
        <div style={{
          backgroundColor: '#fff3cd',
          border: '1px solid #ffc107',
          borderRadius: '8px',
          padding: '1rem',
          marginBottom: '1.5rem',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span style={{ fontSize: '1.2rem' }}>⚠️</span>
            <strong>You have unsaved changes!</strong>
            <span style={{ color: '#856404', marginLeft: '0.5rem' }}>
              Click "Save All Rules" to persist your changes.
            </span>
          </div>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <button
              onClick={() => saveAllMutation.mutate()}
              disabled={saveAllMutation.isPending}
              style={{
                padding: '0.5rem 1rem',
                backgroundColor: '#28a745',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '0.875rem',
                fontWeight: '600'
              }}
            >
              {saveAllMutation.isPending ? 'Saving...' : 'Save All Rules'}
            </button>
            <button
              onClick={discardChanges}
              style={{
                padding: '0.5rem 1rem',
                backgroundColor: '#6c757d',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '0.875rem'
              }}
            >
              Discard Changes
            </button>
          </div>
        </div>
      )}

      {/* Header Card */}
      <div style={cardStyle}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h2 style={{ margin: 0 }}>Response Rules Configuration</h2>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <button
              onClick={() => saveAllMutation.mutate()}
              disabled={saveAllMutation.isPending || !hasUnsavedChanges}
              style={{
                padding: '0.5rem 1rem',
                backgroundColor: hasUnsavedChanges ? '#007bff' : '#6c757d',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: hasUnsavedChanges ? 'pointer' : 'not-allowed',
                fontSize: '0.875rem',
                fontWeight: '600',
                opacity: hasUnsavedChanges ? 1 : 0.6
              }}
            >
              {saveAllMutation.isPending ? 'Saving...' : 'Save All Rules'}
            </button>
            <button onClick={addNewRule} style={addButtonStyle}>
              Add New Rule
            </button>
          </div>
        </div>
        <p style={{ color: '#6c757d', fontSize: '0.875rem' }}>
          Define custom responses based on HTTP method and path. Rules are evaluated in priority order (top to bottom).
          First matching rule wins. If no rule matches, the default JSON response is returned.
        </p>

        <div style={{
          marginBottom: '1rem',
          padding: '0.75rem 1rem',
          backgroundColor: '#e7f3ff',
          border: '1px solid #b3d9ff',
          borderRadius: '8px'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', flexWrap: 'wrap' }}>
            <strong style={{ fontSize: '0.875rem', color: '#0056b3', marginRight: '0.5rem' }}>
              Base URL:
            </strong>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <code style={{
                fontSize: '0.875rem',
                padding: '0.375rem 0.5rem',
                backgroundColor: 'white',
                border: '1px solid #b3d9ff',
                borderRadius: '4px',
                display: 'inline-block',
                maxWidth: 'fit-content'
              }}>
                {(() => {
                  const hostname = window.location.hostname;
                  const httpsPort = window.location.protocol === 'https:' ? (window.location.port || '8443') : '8443';
                  return `https://${hostname}:${httpsPort}/as2/userapi/${user.username}`;
                })()}
              </code>
              <button
                onClick={() => {
                  const hostname = window.location.hostname;
                  const httpsPort = window.location.protocol === 'https:' ? (window.location.port || '8443') : '8443';
                  const url = `https://${hostname}:${httpsPort}/as2/userapi/${user.username}`;
                  navigator.clipboard.writeText(url);
                  alert('Base API URL copied to clipboard!');
                }}
                style={{
                  padding: '0.375rem 0.75rem',
                  backgroundColor: '#007bff',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                  fontSize: '0.8rem',
                  fontWeight: '600',
                  whiteSpace: 'nowrap'
                }}
              >
                📋 Copy
              </button>
            </div>
          </div>
        </div>

        <div style={{
          marginTop: '1rem',
          padding: '1rem',
          backgroundColor: '#f8f9fa',
          border: '1px solid #dee2e6',
          borderRadius: '8px'
        }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
            <p style={{ margin: 0, fontWeight: '600', fontSize: '0.875rem' }}>Match Types:</p>
            <a
              href="https://github.com/zc2tech/mend-as2/blob/main/md-memo/REGEX_CAPTURE_GROUPS_GUIDE.md"
              target="_blank"
              rel="noopener noreferrer"
              style={{
                fontSize: '0.875rem',
                color: '#007bff',
                textDecoration: 'none',
                fontWeight: '600'
              }}
              onMouseOver={(e) => e.target.style.textDecoration = 'underline'}
              onMouseOut={(e) => e.target.style.textDecoration = 'none'}
            >
              📖 Detailed Guide & Examples →
            </a>
          </div>
          <ul style={{ margin: 0, paddingLeft: '1.5rem', fontSize: '0.875rem', color: '#6c757d' }}>
            <li><strong>exact</strong>: Path must match exactly (e.g., "/" matches only "/")</li>
            <li><strong>prefix</strong>: Path must start with pattern (e.g., "/api" matches "/api/test")</li>
            <li><strong>wildcard</strong>: Use * for any sequence (e.g., "/api/*" matches "/api/anything")</li>
            <li><strong>regex</strong>: Use regular expressions with capture groups (e.g., "^/customers/(?&lt;id&gt;[^/]+)$")</li>
          </ul>
          <div style={{
            marginTop: '0.75rem',
            padding: '0.75rem',
            backgroundColor: '#e7f3ff',
            border: '1px solid #b3d9ff',
            borderRadius: '4px',
            fontSize: '0.8rem',
            color: '#004085'
          }}>
            <strong>💡 Pro Tip:</strong> Use regex with named capture groups like <code>(?&lt;customerId&gt;[^/]+)</code> to extract path values
            and reference them in Response Body as <code>${'${customerId}'}</code>
          </div>
        </div>
      </div>

      {/* Rules List */}
      {rules.length === 0 ? (
        <div style={cardStyle}>
          <p style={{ color: '#999', fontStyle: 'italic', textAlign: 'center', margin: '2rem 0' }}>
            No rules configured. Click "Add New Rule" to create one.
          </p>
        </div>
      ) : (
        rules.map((rule, index) => (
          <div key={rule.id || index} style={{
            ...cardStyle,
            borderLeft: rule.enabled ? '4px solid #28a745' : '4px solid #dc3545'
          }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
                <span style={{ fontWeight: 'bold', fontSize: '1.1rem', color: '#6c757d' }}>
                  #{index + 1}
                </span>
                <label style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
                  <input
                    type="checkbox"
                    checked={rule.enabled}
                    onChange={(e) => {
                      updateRule(index, 'enabled', e.target.checked);
                      saveRule({ ...rule, enabled: e.target.checked });
                    }}
                    style={{ marginRight: '0.5rem' }}
                  />
                  <span style={{ fontWeight: '600' }}>Enabled</span>
                </label>
              </div>
              <div>
                <button
                  onClick={() => moveRuleUp(index)}
                  disabled={index === 0}
                  style={{
                    ...moveButtonStyle,
                    opacity: index === 0 ? 0.5 : 1,
                    cursor: index === 0 ? 'not-allowed' : 'pointer'
                  }}
                >
                  ↑
                </button>
                <button
                  onClick={() => moveRuleDown(index)}
                  disabled={index === rules.length - 1}
                  style={{
                    ...moveButtonStyle,
                    opacity: index === rules.length - 1 ? 0.5 : 1,
                    cursor: index === rules.length - 1 ? 'not-allowed' : 'pointer'
                  }}
                >
                  ↓
                </button>
                <button onClick={() => deleteRule(rule.id)} style={deleteButtonStyle}>
                  Delete
                </button>
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '150px 150px 1fr', gap: '1rem', marginBottom: '1rem' }}>
              <div>
                <label style={{ display: 'block', marginBottom: '0.25rem', fontWeight: '600', fontSize: '0.875rem' }}>
                  HTTP Method
                </label>
                <select
                  value={rule.httpMethod}
                  onChange={(e) => updateRule(index, 'httpMethod', e.target.value)}
                  style={selectStyle}
                >
                  <option value="*">* (Any)</option>
                  <option value="GET">GET</option>
                  <option value="POST">POST</option>
                  <option value="PUT">PUT</option>
                  <option value="DELETE">DELETE</option>
                </select>
              </div>

              <div>
                <label style={{ display: 'block', marginBottom: '0.25rem', fontWeight: '600', fontSize: '0.875rem' }}>
                  Match Type
                </label>
                <select
                  value={rule.pathMatchType}
                  onChange={(e) => updateRule(index, 'pathMatchType', e.target.value)}
                  style={selectStyle}
                >
                  <option value="exact">Exact</option>
                  <option value="prefix">Prefix</option>
                  <option value="wildcard">Wildcard</option>
                  <option value="regex">Regex</option>
                </select>
              </div>

              <div style={{ flex: 1 }}>
                <label style={{ display: 'block', marginBottom: '0.25rem', fontWeight: '600', fontSize: '0.875rem' }}>
                  Path Pattern
                </label>
                <input
                  type="text"
                  value={rule.pathPattern}
                  onChange={(e) => updateRule(index, 'pathPattern', e.target.value)}
                  style={{
                    ...inputStyle,
                    fontFamily: '"Courier New", "Monaco", "Consolas", monospace',
                    fontSize: '1rem',
                    fontWeight: '500',
                    letterSpacing: '0.02em',
                    padding: '0.6rem',
                    borderWidth: '2px',
                    borderColor: rule.pathMatchType === 'regex' && rule.pathPattern ?
                      ((() => {
                        try {
                          new RegExp(rule.pathPattern);
                          return '#28a745'; // green for valid
                        } catch (e) {
                          return '#dc3545'; // red for invalid
                        }
                      })()) : '#ced4da'
                  }}
                  placeholder="/path/pattern"
                />
                {rule.pathMatchType === 'regex' && rule.pathPattern && (
                  <div style={{
                    marginTop: '0.25rem',
                    fontSize: '0.75rem',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '0.25rem'
                  }}>
                    {(() => {
                      try {
                        new RegExp(rule.pathPattern);
                        return (
                          <>
                            <span style={{ color: '#28a745' }}>✓ Valid regex</span>
                            {rule.pathPattern.includes('(?<') && (
                              <span style={{ color: '#6c757d', marginLeft: '0.5rem' }}>
                                (Named groups detected)
                              </span>
                            )}
                          </>
                        );
                      } catch (e) {
                        return (
                          <span style={{ color: '#dc3545' }}>
                            ✗ Invalid regex: {e.message}
                          </span>
                        );
                      }
                    })()}
                  </div>
                )}
              </div>
            </div>

            {/* Regex Helper Section */}
            {rule.pathMatchType === 'regex' && (
              <div style={{
                marginBottom: '1rem',
                padding: '0.75rem',
                backgroundColor: '#f8f9fa',
                border: '1px solid #dee2e6',
                borderRadius: '4px'
              }}>
                <div style={{ fontSize: '0.8rem', fontWeight: '600', marginBottom: '0.5rem', color: '#495057' }}>
                  💡 Quick Insert:
                </div>
                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '0.5rem' }}>
                  <button
                    type="button"
                    onClick={() => {
                      const cursorPos = document.activeElement?.selectionStart || rule.pathPattern.length;
                      const newValue = rule.pathPattern.slice(0, cursorPos) + '[^/]+' + rule.pathPattern.slice(cursorPos);
                      updateRule(index, 'pathPattern', newValue);
                    }}
                    style={{
                      padding: '0.25rem 0.5rem',
                      fontSize: '0.75rem',
                      backgroundColor: 'white',
                      border: '1px solid #ced4da',
                      borderRadius: '3px',
                      cursor: 'pointer'
                    }}
                    title="One or more non-slash characters"
                  >
                    [^/]+
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      const cursorPos = document.activeElement?.selectionStart || rule.pathPattern.length;
                      const newValue = rule.pathPattern.slice(0, cursorPos) + '(?<name>[^/]+)' + rule.pathPattern.slice(cursorPos);
                      updateRule(index, 'pathPattern', newValue);
                    }}
                    style={{
                      padding: '0.25rem 0.5rem',
                      fontSize: '0.75rem',
                      backgroundColor: 'white',
                      border: '1px solid #ced4da',
                      borderRadius: '3px',
                      cursor: 'pointer'
                    }}
                    title="Named capture group"
                  >
                    (?&lt;name&gt;[^/]+)
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      const cursorPos = document.activeElement?.selectionStart || rule.pathPattern.length;
                      const newValue = rule.pathPattern.slice(0, cursorPos) + '[0-9]+' + rule.pathPattern.slice(cursorPos);
                      updateRule(index, 'pathPattern', newValue);
                    }}
                    style={{
                      padding: '0.25rem 0.5rem',
                      fontSize: '0.75rem',
                      backgroundColor: 'white',
                      border: '1px solid #ced4da',
                      borderRadius: '3px',
                      cursor: 'pointer'
                    }}
                    title="One or more digits"
                  >
                    [0-9]+
                  </button>
                  <button
                    type="button"
                    onClick={() => {
                      if (!rule.pathPattern.startsWith('^')) {
                        updateRule(index, 'pathPattern', '^' + rule.pathPattern);
                      }
                      if (!rule.pathPattern.endsWith('$')) {
                        updateRule(index, 'pathPattern', rule.pathPattern + '$');
                      }
                    }}
                    style={{
                      padding: '0.25rem 0.5rem',
                      fontSize: '0.75rem',
                      backgroundColor: 'white',
                      border: '1px solid #ced4da',
                      borderRadius: '3px',
                      cursor: 'pointer'
                    }}
                    title="Add anchors (^ and $) for exact match"
                  >
                    Add ^ $
                  </button>
                  <span style={{ fontSize: '0.75rem', color: '#6c757d', alignSelf: 'center', marginLeft: '0.5rem' }}>
                    Click to insert pattern at cursor
                  </span>
                </div>
              </div>
            )}

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 2fr', gap: '1rem', marginBottom: '1rem' }}>
              <div>
                <label style={{ display: 'block', marginBottom: '0.25rem', fontWeight: '600', fontSize: '0.875rem' }}>
                  Response Status Code
                </label>
                <div style={{ display: 'flex', gap: '0.5rem', alignItems: 'center' }}>
                  <input
                    type="number"
                    value={rule.statusCode}
                    onChange={(e) => updateRule(index, 'statusCode', parseInt(e.target.value))}
                    style={{ ...inputStyle, flex: 1 }}
                    min="100"
                    max="599"
                    placeholder="200"
                  />
                  <select
                    value=""
                    onChange={(e) => {
                      if (e.target.value) {
                        updateRule(index, 'statusCode', parseInt(e.target.value));
                      }
                    }}
                    style={{ ...selectStyle, flex: 1, fontSize: '0.8rem' }}
                  >
                    <option value="">Common codes...</option>
                    <option value="200">200 OK</option>
                    <option value="201">201 Created</option>
                    <option value="204">204 No Content</option>
                    <option value="400">400 Bad Request</option>
                    <option value="401">401 Unauthorized</option>
                    <option value="403">403 Forbidden</option>
                    <option value="404">404 Not Found</option>
                    <option value="500">500 Internal Server Error</option>
                    <option value="502">502 Bad Gateway</option>
                    <option value="503">503 Service Unavailable</option>
                  </select>
                </div>
              </div>

              <div>
                <label style={{ display: 'block', marginBottom: '0.25rem', fontWeight: '600', fontSize: '0.875rem' }}>
                  Response Content Type
                </label>
                <select
                  value={rule.contentType}
                  onChange={(e) => updateRule(index, 'contentType', e.target.value)}
                  style={inputStyle}
                >
                  <option value="application/json">application/json</option>
                  <option value="text/plain">text/plain</option>
                  <option value="text/html">text/html</option>
                  <option value="text/xml">text/xml</option>
                  <option value="application/xml">application/xml</option>
                </select>
              </div>
            </div>

            <div>
              <label style={{ display: 'block', marginBottom: '0.25rem', fontWeight: '600', fontSize: '0.875rem' }}>
                Response Body
                <span style={{ fontWeight: 'normal', color: '#6c757d', marginLeft: '0.5rem' }}>
                  (Variables: $&#123;path&#125;, $&#123;method&#125;, $&#123;requestId&#125;, $&#123;1&#125;, $&#123;groupName&#125;)
                </span>
              </label>
              <textarea
                value={rule.responseBody}
                onChange={(e) => updateRule(index, 'responseBody', e.target.value)}
                style={textareaStyle}
                placeholder='{"message": "Hello World"}'
              />
            </div>
          </div>
        ))
      )}

      {/* Bottom Action Buttons */}
      {rules.length > 0 && (
        <div style={{
          ...cardStyle,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          padding: '1rem 1.5rem'
        }}>
          <div style={{ color: '#6c757d', fontSize: '0.875rem' }}>
            {hasUnsavedChanges && (
              <span style={{ color: '#856404', fontWeight: '600' }}>
                ⚠️ You have unsaved changes
              </span>
            )}
          </div>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <button
              onClick={() => saveAllMutation.mutate()}
              disabled={saveAllMutation.isPending || !hasUnsavedChanges}
              style={{
                padding: '0.5rem 1rem',
                backgroundColor: hasUnsavedChanges ? '#007bff' : '#6c757d',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: hasUnsavedChanges ? 'pointer' : 'not-allowed',
                fontSize: '0.875rem',
                fontWeight: '600',
                opacity: hasUnsavedChanges ? 1 : 0.6
              }}
            >
              {saveAllMutation.isPending ? 'Saving...' : 'Save All Rules'}
            </button>
            <button onClick={addNewRule} style={addButtonStyle}>
              Add New Rule
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
