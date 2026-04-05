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
import { useGenerateKey } from './useCertificates';
import { useToast } from '../../components/Toast';

export default function GenerateKeyDialog({ keystoreType, onClose, onSuccess }) {
  const [formData, setFormData] = useState({
    alias: '',
    keyAlgorithm: 'RSA',
    keySize: 2048,
    commonName: '',
    organisationUnit: '',
    organisationName: '',
    localityName: '',
    stateName: '',
    countryCode: 'US',
    emailAddress: '',
    keyValidInDays: 365,
    signatureAlgorithm: 'SHA256WithRSA'
  });

  const generateKey = useGenerateKey();
  const toast = useToast();

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: name === 'keySize' || name === 'keyValidInDays' ? parseInt(value) : value
    }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (!formData.alias || !formData.commonName) {
      toast.error('Alias and Common Name are required');
      return;
    }

    try {
      await generateKey.mutateAsync({
        ...formData,
        keystoreType
      });
      toast.success(`Key "${formData.alias}" generated successfully`);
      onSuccess();
      onClose();
    } catch (error) {
      toast.error('Failed to generate key: ' + (error.response?.data?.error || error.message));
    }
  };

  const overlayStyle = {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    zIndex: 2000
  };

  const dialogStyle = {
    backgroundColor: 'white',
    borderRadius: '8px',
    boxShadow: '0 4px 20px rgba(0,0,0,0.3)',
    maxWidth: '600px',
    width: '90%',
    maxHeight: '90vh',
    overflow: 'auto'
  };

  const headerStyle = {
    padding: '1.5rem',
    borderBottom: '1px solid #dee2e6',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  };

  const bodyStyle = {
    padding: '1.5rem'
  };

  const formGroupStyle = {
    marginBottom: '1rem'
  };

  const labelStyle = {
    display: 'block',
    marginBottom: '0.5rem',
    fontWeight: '500',
    fontSize: '0.875rem'
  };

  const inputStyle = {
    width: '100%',
    padding: '0.5rem',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    fontSize: '0.875rem',
    boxSizing: 'border-box'
  };

  const selectStyle = {
    ...inputStyle
  };

  const buttonStyle = {
    padding: '0.5rem 1rem',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.875rem',
    marginRight: '0.5rem'
  };

  return (
    <div style={overlayStyle} onClick={onClose}>
      <div style={dialogStyle} onClick={(e) => e.stopPropagation()}>
        <div style={headerStyle}>
          <h2 style={{ margin: 0, fontSize: '1.25rem' }}>Generate Key Pair</h2>
          <button
            onClick={onClose}
            style={{
              background: 'none',
              border: 'none',
              fontSize: '1.5rem',
              cursor: 'pointer',
              padding: 0,
              color: '#6c757d'
            }}
          >
            ×
          </button>
        </div>

        <div style={bodyStyle}>
          <form onSubmit={handleSubmit}>
            <div style={formGroupStyle}>
              <label style={labelStyle}>Alias (Key Name) *</label>
              <input
                type="text"
                name="alias"
                value={formData.alias}
                onChange={handleChange}
                style={inputStyle}
                placeholder="e.g., mycompany_key"
                required
              />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div style={formGroupStyle}>
                <label style={labelStyle}>Key Algorithm</label>
                <select
                  name="keyAlgorithm"
                  value={formData.keyAlgorithm}
                  onChange={handleChange}
                  style={selectStyle}
                >
                  <option value="RSA">RSA</option>
                  <option value="DSA">DSA</option>
                  <option value="ECDSA">ECDSA</option>
                </select>
              </div>

              <div style={formGroupStyle}>
                <label style={labelStyle}>Key Size (bits)</label>
                <select
                  name="keySize"
                  value={formData.keySize}
                  onChange={handleChange}
                  style={selectStyle}
                >
                  <option value="1024">1024</option>
                  <option value="2048">2048</option>
                  <option value="4096">4096</option>
                </select>
              </div>
            </div>

            <div style={formGroupStyle}>
              <label style={labelStyle}>Common Name (CN) *</label>
              <input
                type="text"
                name="commonName"
                value={formData.commonName}
                onChange={handleChange}
                style={inputStyle}
                placeholder="e.g., subdomain.yourdomain.com"
                required
              />
            </div>

            <div style={formGroupStyle}>
              <label style={labelStyle}>Organization Unit (OU)</label>
              <input
                type="text"
                name="organisationUnit"
                value={formData.organisationUnit}
                onChange={handleChange}
                style={inputStyle}
                placeholder="e.g., IT Department"
              />
            </div>

            <div style={formGroupStyle}>
              <label style={labelStyle}>Organization (O)</label>
              <input
                type="text"
                name="organisationName"
                value={formData.organisationName}
                onChange={handleChange}
                style={inputStyle}
                placeholder="e.g., Your Company Name"
              />
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div style={formGroupStyle}>
                <label style={labelStyle}>City/Locality (L)</label>
                <input
                  type="text"
                  name="localityName"
                  value={formData.localityName}
                  onChange={handleChange}
                  style={inputStyle}
                  placeholder="e.g., San Francisco"
                />
              </div>

              <div style={formGroupStyle}>
                <label style={labelStyle}>State/Province (ST)</label>
                <input
                  type="text"
                  name="stateName"
                  value={formData.stateName}
                  onChange={handleChange}
                  style={inputStyle}
                  placeholder="e.g., California"
                />
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div style={formGroupStyle}>
                <label style={labelStyle}>Country Code (C)</label>
                <input
                  type="text"
                  name="countryCode"
                  value={formData.countryCode}
                  onChange={handleChange}
                  style={inputStyle}
                  maxLength="2"
                  placeholder="e.g., US"
                />
              </div>

              <div style={formGroupStyle}>
                <label style={labelStyle}>Email Address</label>
                <input
                  type="email"
                  name="emailAddress"
                  value={formData.emailAddress}
                  onChange={handleChange}
                  style={inputStyle}
                  placeholder="e.g., admin@example.com"
                />
              </div>
            </div>

            <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1rem' }}>
              <div style={formGroupStyle}>
                <label style={labelStyle}>Valid for (days)</label>
                <input
                  type="number"
                  name="keyValidInDays"
                  value={formData.keyValidInDays}
                  onChange={handleChange}
                  style={inputStyle}
                  min="1"
                />
              </div>

              <div style={formGroupStyle}>
                <label style={labelStyle}>Signature Algorithm</label>
                <select
                  name="signatureAlgorithm"
                  value={formData.signatureAlgorithm}
                  onChange={handleChange}
                  style={selectStyle}
                >
                  <option value="SHA256WithRSA">SHA256 with RSA</option>
                  <option value="SHA512WithRSA">SHA512 with RSA</option>
                  <option value="SHA1WithRSA">SHA1 with RSA</option>
                </select>
              </div>
            </div>

            <div style={{ marginTop: '1.5rem', display: 'flex', justifyContent: 'flex-end' }}>
              <button
                type="button"
                onClick={onClose}
                style={{
                  ...buttonStyle,
                  backgroundColor: '#6c757d',
                  color: 'white'
                }}
              >
                Cancel
              </button>
              <button
                type="submit"
                disabled={generateKey.isPending}
                style={{
                  ...buttonStyle,
                  backgroundColor: '#28a745',
                  color: 'white',
                  marginRight: 0
                }}
              >
                {generateKey.isPending ? 'Generating...' : 'Generate Key'}
              </button>
            </div>
          </form>
        </div>
      </div>
    </div>
  );
}
