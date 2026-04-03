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
import { useQueryClient } from '@tanstack/react-query';
import { useToast } from '../../components/Toast';
import api from '../../api/client';

export default function CertificateImport({ keystoreType, onClose }) {
  const [file, setFile] = useState(null);
  const [password, setPassword] = useState('');
  const [alias, setAlias] = useState('');
  const [loading, setLoading] = useState(false);
  const [dragActive, setDragActive] = useState(false);
  const queryClient = useQueryClient();
  const toast = useToast();

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);

    if (e.dataTransfer.files && e.dataTransfer.files[0]) {
      setFile(e.dataTransfer.files[0]);
    }
  };

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files[0]) {
      setFile(e.target.files[0]);
    }
  };

  const handleImport = async () => {
    if (!file) {
      toast.warning('Please select a certificate file');
      return;
    }

    if (!password) {
      toast.warning('Please enter the keystore password');
      return;
    }

    setLoading(true);
    try {
      const formData = new FormData();
      formData.append('file', file);
      formData.append('password', password);
      formData.append('keystoreType', keystoreType);
      if (alias) {
        formData.append('alias', alias);
      }

      await api.post('/certificates/import', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });

      toast.success('Certificate imported successfully');
      queryClient.invalidateQueries(['certificates', keystoreType]);
      onClose();
    } catch (error) {
      toast.error('Failed to import certificate: ' + (error.response?.data?.error || error.message));
    } finally {
      setLoading(false);
    }
  };

  const dropZoneStyle = {
    border: dragActive ? '2px dashed #007bff' : '2px dashed #ddd',
    borderRadius: '8px',
    padding: '2rem',
    textAlign: 'center',
    backgroundColor: dragActive ? '#f0f8ff' : '#fafafa',
    cursor: 'pointer',
    transition: 'all 0.3s'
  };

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0,0,0,0.5)',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      zIndex: 1000,
      padding: '1rem'
    }} onClick={onClose}>
      <div style={{
        backgroundColor: 'white',
        padding: '2rem',
        borderRadius: '8px',
        maxWidth: '500px',
        width: '100%'
      }} onClick={(e) => e.stopPropagation()}>
        <h2 style={{ marginTop: 0 }}>Import Certificate</h2>
        <p style={{ color: '#666', fontSize: '0.875rem', marginBottom: '1.5rem' }}>
          Importing to: {keystoreType === 'sign' ? 'Sign/Encrypt Keystore' : 'TLS Keystore'}
        </p>

        <div
          style={dropZoneStyle}
          onDragEnter={handleDrag}
          onDragLeave={handleDrag}
          onDragOver={handleDrag}
          onDrop={handleDrop}
          onClick={() => document.getElementById('fileInput').click()}
        >
          <input
            id="fileInput"
            type="file"
            accept=".p12,.pfx,.jks,.cer,.crt,.pem"
            onChange={handleFileChange}
            style={{ display: 'none' }}
          />
          {file ? (
            <div>
              <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>📄</div>
              <div style={{ fontWeight: '600' }}>{file.name}</div>
              <div style={{ color: '#666', fontSize: '0.875rem' }}>
                {(file.size / 1024).toFixed(2)} KB
              </div>
            </div>
          ) : (
            <div>
              <div style={{ fontSize: '2rem', marginBottom: '0.5rem' }}>📁</div>
              <div style={{ fontWeight: '600', marginBottom: '0.5rem' }}>
                Drop certificate file here
              </div>
              <div style={{ color: '#666', fontSize: '0.875rem' }}>
                or click to browse
              </div>
              <div style={{ color: '#999', fontSize: '0.75rem', marginTop: '0.5rem' }}>
                Supports .p12, .pfx, .jks, .cer, .crt, .pem
              </div>
            </div>
          )}
        </div>

        <div style={{ marginTop: '1.5rem' }}>
          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600' }}>
            Keystore Password *
          </label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter keystore password"
            disabled={loading}
            style={{
              width: '100%',
              padding: '0.5rem',
              border: '1px solid #ddd',
              borderRadius: '4px',
              marginBottom: '1rem'
            }}
          />

          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '600' }}>
            Alias (optional)
          </label>
          <input
            type="text"
            value={alias}
            onChange={(e) => setAlias(e.target.value)}
            placeholder="Certificate alias"
            disabled={loading}
            style={{
              width: '100%',
              padding: '0.5rem',
              border: '1px solid #ddd',
              borderRadius: '4px'
            }}
          />
        </div>

        <div style={{ display: 'flex', gap: '0.5rem', marginTop: '1.5rem' }}>
          <button
            onClick={handleImport}
            disabled={loading || !file}
            style={{
              padding: '0.75rem 1.5rem',
              backgroundColor: loading || !file ? '#6c757d' : '#28a745',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: loading || !file ? 'not-allowed' : 'pointer',
              fontSize: '1rem'
            }}
          >
            {loading ? 'Importing...' : 'Import Certificate'}
          </button>
          <button
            onClick={onClose}
            disabled={loading}
            style={{
              padding: '0.75rem 1.5rem',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: loading ? 'not-allowed' : 'pointer',
              fontSize: '1rem'
            }}
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}
