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
import api from '../../api/client';

export default function InboundAuth() {
  const { showToast } = useToast();
  const queryClient = useQueryClient();

  const [enableBasicAuth, setEnableBasicAuth] = useState(false);
  const [enableCertAuth, setEnableCertAuth] = useState(false);
  const [basicAuthCreds, setBasicAuthCreds] = useState([]);
  const [certAuthCreds, setCertAuthCreds] = useState([]);
  const [certificates, setCertificates] = useState([]);
  const [passwordVisibility, setPasswordVisibility] = useState({});

  // Load configuration
  const { isLoading } = useQuery({
    queryKey: ['inboundAuth'],
    queryFn: async () => {
      const [configRes, basicRes, certRes, certsRes] = await Promise.all([
        api.get('/system/inbound-auth/config'),
        api.get('/system/inbound-auth/credentials/basic'),
        api.get('/system/inbound-auth/credentials/cert'),
        api.get('/certificates')
      ]);

      const authMode = configRes.data.authMode || 0;
      setEnableBasicAuth((authMode & 1) !== 0);
      setEnableCertAuth((authMode & 2) !== 0);
      setBasicAuthCreds(basicRes.data || []);
      setCertAuthCreds(certRes.data || []);
      setCertificates(certsRes.data || []);

      return { authMode };
    }
  });

  // Save mutation
  const saveMutation = useMutation({
    mutationFn: async () => {
      const authMode = (enableBasicAuth ? 1 : 0) | (enableCertAuth ? 2 : 0);

      await Promise.all([
        api.post('/system/inbound-auth/config', { authMode }),
        api.post('/system/inbound-auth/credentials/basic', basicAuthCreds),
        api.post('/system/inbound-auth/credentials/cert', certAuthCreds)
      ]);
    },
    onSuccess: () => {
      showToast('Inbound authentication settings saved successfully', 'success');
      queryClient.invalidateQueries(['inboundAuth']);
    },
    onError: (error) => {
      showToast('Failed to save inbound authentication settings: ' + error.message, 'error');
    }
  });

  if (isLoading) {
    return <LoadingPage message="Loading inbound authentication settings..." />;
  }

  const cardStyle = {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '1.5rem'
  };

  const saveButtonStyle = {
    padding: '0.5rem 1rem',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    marginRight: '0.5rem',
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
    marginRight: '0.5rem',
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

  const addBasicAuthRow = () => {
    setBasicAuthCreds([...basicAuthCreds, { username: '', password: '' }]);
  };

  const removeBasicAuthRow = (index) => {
    setBasicAuthCreds(basicAuthCreds.filter((_, i) => i !== index));
    // Remove visibility state for this row
    const newVisibility = { ...passwordVisibility };
    delete newVisibility[index];
    setPasswordVisibility(newVisibility);
  };

  const updateBasicAuthRow = (index, field, value) => {
    const updated = [...basicAuthCreds];
    updated[index][field] = value;
    setBasicAuthCreds(updated);
  };

  const togglePasswordVisibility = (index) => {
    setPasswordVisibility({
      ...passwordVisibility,
      [index]: !passwordVisibility[index]
    });
  };

  const addCertAuthRow = () => {
    if (certificates.length > 0) {
      setCertAuthCreds([...certAuthCreds, { certAlias: certificates[0].alias }]);
    }
  };

  const removeCertAuthRow = (index) => {
    setCertAuthCreds(certAuthCreds.filter((_, i) => i !== index));
  };

  const updateCertAuthRow = (index, value) => {
    const updated = [...certAuthCreds];
    updated[index].certAlias = value;
    setCertAuthCreds(updated);
  };

  return (
    <div>
      <div style={cardStyle}>
        <h2 style={{ marginTop: 0 }}>Inbound Authentication</h2>
        <p style={{ color: '#6c757d', fontSize: '0.875rem' }}>
          Configure authentication required for incoming AS2 messages. Messages matching ANY configured credential will be accepted.
        </p>

        <div style={{ marginBottom: '1.5rem' }}>
          <label style={{ display: 'block', marginBottom: '0.5rem' }}>
            <input
              type="checkbox"
              checked={enableBasicAuth}
              onChange={(e) => setEnableBasicAuth(e.target.checked)}
              style={{ marginRight: '0.5rem' }}
            />
            Enable Basic Authentication
          </label>

          <label style={{ display: 'block' }}>
            <input
              type="checkbox"
              checked={enableCertAuth}
              onChange={(e) => setEnableCertAuth(e.target.checked)}
              style={{ marginRight: '0.5rem' }}
            />
            Enable Certificate Authentication
          </label>
        </div>

        <button
          onClick={() => saveMutation.mutate()}
          disabled={saveMutation.isPending}
          style={saveButtonStyle}
        >
          {saveMutation.isPending ? 'Saving...' : 'Save Settings'}
        </button>
      </div>

      {/* Basic Authentication Table */}
      <div style={cardStyle}>
        <h3>Basic Authentication Credentials</h3>
        <table style={tableStyle}>
          <thead>
            <tr>
              <th style={thStyle}>#</th>
              <th style={thStyle}>Username</th>
              <th style={thStyle}>Password</th>
              <th style={thStyle}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {basicAuthCreds.map((cred, index) => (
              <tr key={index}>
                <td style={tdStyle}>{index + 1}</td>
                <td style={tdStyle}>
                  <input
                    type="text"
                    value={cred.username}
                    onChange={(e) => updateBasicAuthRow(index, 'username', e.target.value)}
                    style={inputStyle}
                    disabled={!enableBasicAuth}
                  />
                </td>
                <td style={tdStyle}>
                  <div style={passwordContainerStyle}>
                    <input
                      type={passwordVisibility[index] ? 'text' : 'password'}
                      value={cred.password}
                      onChange={(e) => updateBasicAuthRow(index, 'password', e.target.value)}
                      style={inputStyle}
                      disabled={!enableBasicAuth}
                    />
                    <span
                      style={eyeIconStyle}
                      onClick={() => togglePasswordVisibility(index)}
                    >
                      {passwordVisibility[index] ? '👁️' : '👁️‍🗨️'}
                    </span>
                  </div>
                </td>
                <td style={tdStyle}>
                  <button
                    onClick={() => removeBasicAuthRow(index)}
                    style={deleteButtonStyle}
                    disabled={!enableBasicAuth}
                  >
                    Remove
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <button
          onClick={addBasicAuthRow}
          style={{ ...addButtonStyle, marginTop: '1rem' }}
          disabled={!enableBasicAuth}
        >
          Add Credential
        </button>
      </div>

      {/* Certificate Authentication Table */}
      <div style={cardStyle}>
        <h3>Certificate Authentication</h3>
        <table style={tableStyle}>
          <thead>
            <tr>
              <th style={thStyle}>#</th>
              <th style={thStyle}>Certificate Alias</th>
              <th style={thStyle}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {certAuthCreds.map((cred, index) => (
              <tr key={index}>
                <td style={tdStyle}>{index + 1}</td>
                <td style={tdStyle}>
                  <select
                    value={cred.certAlias}
                    onChange={(e) => updateCertAuthRow(index, e.target.value)}
                    style={inputStyle}
                    disabled={!enableCertAuth}
                  >
                    {certificates.map(cert => (
                      <option key={cert.alias} value={cert.alias}>
                        {cert.alias}
                      </option>
                    ))}
                  </select>
                </td>
                <td style={tdStyle}>
                  <button
                    onClick={() => removeCertAuthRow(index)}
                    style={deleteButtonStyle}
                    disabled={!enableCertAuth}
                  >
                    Remove
                  </button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
        <button
          onClick={addCertAuthRow}
          style={{ ...addButtonStyle, marginTop: '1rem' }}
          disabled={!enableCertAuth}
        >
          Add Certificate
        </button>
      </div>

      {/* Bottom Save Button */}
      <div style={cardStyle}>
        <button
          onClick={() => saveMutation.mutate()}
          disabled={saveMutation.isPending}
          style={saveButtonStyle}
        >
          {saveMutation.isPending ? 'Saving...' : 'Save Settings'}
        </button>
      </div>
    </div>
  );
}
