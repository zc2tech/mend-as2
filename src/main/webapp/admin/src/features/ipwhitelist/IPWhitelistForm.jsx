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
import { useValidatePattern } from './useIPWhitelist';

export default function IPWhitelistForm({ initialData, mode, onSubmit, onCancel }) {
  const [ipPattern, setIpPattern] = useState(initialData?.ipPattern || '');
  const [targetType, setTargetType] = useState(initialData?.targetType || 'AS2');
  const [description, setDescription] = useState(initialData?.description || '');
  const [enabled, setEnabled] = useState(initialData?.enabled !== undefined ? initialData.enabled : true);
  const [validationResult, setValidationResult] = useState(null);
  const validateMutation = useValidatePattern();

  useEffect(() => {
    if (initialData) {
      setIpPattern(initialData.ipPattern || '');
      setTargetType(initialData.targetType || 'AS2');
      setDescription(initialData.description || '');
      setEnabled(initialData.enabled !== undefined ? initialData.enabled : true);
    }
  }, [initialData]);

  const handleValidate = async () => {
    if (!ipPattern.trim()) {
      setValidationResult({ valid: false, description: 'IP pattern is required' });
      return;
    }
    try {
      const result = await validateMutation.mutateAsync(ipPattern);
      setValidationResult(result);
    } catch (error) {
      setValidationResult({ valid: false, description: error.response?.data?.error || error.message });
    }
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    const formData = {
      ipPattern,
      targetType,
      description,
      enabled
    };
    onSubmit(formData);
  };

  const formContainerStyle = {
    backgroundColor: 'white',
    padding: '2rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    maxWidth: '600px'
  };

  const formGroupStyle = {
    marginBottom: '1.5rem'
  };

  const labelStyle = {
    display: 'block',
    marginBottom: '0.5rem',
    fontWeight: '600',
    fontSize: '0.9rem'
  };

  const inputStyle = {
    width: '100%',
    padding: '0.5rem',
    borderRadius: '4px',
    border: '1px solid #ccc',
    fontSize: '0.9rem',
    boxSizing: 'border-box'
  };

  const selectStyle = {
    ...inputStyle
  };

  const textareaStyle = {
    ...inputStyle,
    minHeight: '80px',
    resize: 'vertical'
  };

  const checkboxContainerStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem'
  };

  const buttonContainerStyle = {
    display: 'flex',
    gap: '1rem',
    marginTop: '2rem'
  };

  const buttonStyle = {
    padding: '0.75rem 1.5rem',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.9rem',
    fontWeight: '500'
  };

  const primaryButtonStyle = {
    ...buttonStyle,
    backgroundColor: '#007bff',
    color: 'white'
  };

  const secondaryButtonStyle = {
    ...buttonStyle,
    backgroundColor: '#6c757d',
    color: 'white'
  };

  const validateButtonStyle = {
    ...buttonStyle,
    backgroundColor: '#28a745',
    color: 'white',
    marginTop: '0.5rem'
  };

  const helpTextStyle = {
    fontSize: '0.85rem',
    color: '#666',
    marginTop: '0.25rem'
  };

  const validationBoxStyle = (isValid) => ({
    marginTop: '0.5rem',
    padding: '0.75rem',
    borderRadius: '4px',
    backgroundColor: isValid ? '#d4edda' : '#f8d7da',
    color: isValid ? '#155724' : '#721c24',
    border: `1px solid ${isValid ? '#c3e6cb' : '#f5c6cb'}`,
    fontSize: '0.85rem'
  });

  return (
    <div style={formContainerStyle}>
      <h2 style={{ marginBottom: '1.5rem', fontSize: '1.5rem' }}>
        {initialData ? 'Edit' : 'Add'} {mode === 'global' ? 'Global' : mode === 'partner' ? 'Partner' : 'User'} Whitelist Entry
      </h2>

      <form onSubmit={handleSubmit}>
        <div style={formGroupStyle}>
          <label style={labelStyle}>
            IP Pattern <span style={{ color: 'red' }}>*</span>
          </label>
          <input
            type="text"
            value={ipPattern}
            onChange={(e) => {
              setIpPattern(e.target.value);
              setValidationResult(null); // Clear validation on change
            }}
            style={inputStyle}
            placeholder="e.g., 192.168.1.100, 10.0.0.0/24, 172.16.*"
            required
          />
          <div style={helpTextStyle}>
            Supported formats: Exact IP (192.168.1.100), CIDR (10.0.0.0/24), Wildcard (172.16.*)
          </div>
          <button
            type="button"
            onClick={handleValidate}
            style={validateButtonStyle}
            disabled={!ipPattern.trim() || validateMutation.isPending}
          >
            {validateMutation.isPending ? 'Validating...' : 'Validate Pattern'}
          </button>
          {validationResult && (
            <div style={validationBoxStyle(validationResult.valid)}>
              {validationResult.valid ? '✓ ' : '✗ '}
              {validationResult.description}
            </div>
          )}
        </div>

        {mode === 'global' && (
          <div style={formGroupStyle}>
            <label style={labelStyle}>
              Target Type <span style={{ color: 'red' }}>*</span>
            </label>
            <select
              value={targetType}
              onChange={(e) => setTargetType(e.target.value)}
              style={selectStyle}
              required
            >
              <option value="AS2">AS2 - AS2 message endpoint</option>
              <option value="TRACKER">TRACKER - Tracker message endpoint</option>
              <option value="WEBUI">WEBUI - WebUI access</option>
              <option value="API">API - REST API access</option>
              <option value="ALL">ALL - All endpoints</option>
            </select>
          </div>
        )}

        <div style={formGroupStyle}>
          <label style={labelStyle}>Description</label>
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            style={textareaStyle}
            placeholder="Optional description (e.g., 'Partner X datacenter', 'Office network')"
          />
        </div>

        <div style={formGroupStyle}>
          <div style={checkboxContainerStyle}>
            <input
              type="checkbox"
              id="enabled"
              checked={enabled}
              onChange={(e) => setEnabled(e.target.checked)}
            />
            <label htmlFor="enabled" style={{ margin: 0, fontWeight: 'normal' }}>
              Enabled (allows IPs matching this pattern)
            </label>
          </div>
        </div>

        <div style={buttonContainerStyle}>
          <button type="submit" style={primaryButtonStyle}>
            {initialData ? 'Update' : 'Add'} Entry
          </button>
          <button type="button" onClick={onCancel} style={secondaryButtonStyle}>
            Cancel
          </button>
        </div>
      </form>
    </div>
  );
}
