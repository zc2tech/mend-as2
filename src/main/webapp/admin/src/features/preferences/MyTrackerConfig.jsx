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

import { useState } from 'react';
import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import { LoadingPage } from '../../components/Loading';
import { useToast } from '../../components/Toast';
import { useAuth } from '../auth/useAuth';
import api from '../../api/client';

export default function MyTrackerConfig() {
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
    queryKey: ['userTrackerAuth'],
    queryFn: async () => {
      // Load config and certificates in parallel
      const [configRes, certsRes] = await Promise.all([
        api.get('/user/tracker-auth/config'),
        api.get('/certificates', { params: { keystoreType: 'sign' } })
      ]);

      const config = configRes.data;
      setBasicAuthEnabled(config.basicAuthEnabled || false);
      setCertAuthEnabled(config.certAuthEnabled || false);

      // Separate credentials by type
      const credentials = config.credentialsList || [];
      setBasicAuthList(credentials.filter(c => c.authType === 1));
      setCertAuthList(credentials.filter(c => c.authType === 2));

      // Set certificates for dropdown - only public certificates (not key pairs)
      const publicCerts = (certsRes.data || []).filter(cert => cert.isKeyPair === false);
      setCertificates(publicCerts);

      return config;
    }
  });

  // Save mutation
  const saveMutation = useMutation({
    mutationFn: async () => {
      const config = {
        basicAuthEnabled,
        certAuthEnabled,
        credentialsList: [...basicAuthList, ...certAuthList]
      };
      const response = await api.post('/user/tracker-auth/config', config);
      return response.data;
    },
    onSuccess: () => {
      toast.success('Tracker authentication settings saved successfully');
      queryClient.invalidateQueries(['userTrackerAuth']);
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
    return <LoadingPage message="Loading tracker authentication settings..." />;
  }

  // Styles
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

  // Build tracker URL
  const hostname = window.location.hostname;
  const httpsPort = window.location.protocol === 'https:' ? (window.location.port || '8443') : '8443';

  const httpsTrackerUrl = `https://${hostname}:${httpsPort}/as2/tracker/${user.username}`;

  // Copy URL to clipboard
  const copyToClipboard = (url, protocol) => {
    navigator.clipboard.writeText(url);
    alert(`${protocol.toUpperCase()} Tracker URL copied to clipboard!`);
  };

  // Check if Add Certificate button should be enabled
  const canAddCert = certificates.length > 0;

  return (
    <div>
      {/* Header Card */}
      <div style={cardStyle}>
        <h2 style={{ marginTop: 0 }}>My Tracker Authentication</h2>
        <p style={{ color: '#6c757d', fontSize: '0.875rem' }}>
          Configure authentication for your personal tracker endpoint.
          Messages posted to your tracker URL can be authenticated using Basic Auth or Certificate Auth.
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
              Endpoint:
            </strong>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
              <span style={{ fontSize: '0.75rem', color: '#6c757d', fontWeight: '600' }}>
                HTTPS ({httpsPort}):
              </span>
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
                onClick={() => copyToClipboard(httpsTrackerUrl, 'https')}
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

        <button
          onClick={() => saveMutation.mutate()}
          disabled={saveMutation.isPending}
          style={saveButtonStyle}
        >
          {saveMutation.isPending ? 'Saving...' : 'Save Configuration'}
        </button>
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
