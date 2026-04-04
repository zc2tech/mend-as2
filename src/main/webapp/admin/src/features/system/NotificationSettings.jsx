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
import { LoadingPage } from '../../components/Loading';

export default function NotificationSettings() {
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [settings, setSettings] = useState({
    mailServer: '',
    mailServerPort: 25,
    connectionSecurity: 0,
    notificationMail: '',
    replyTo: '',
    useSMTPAuthCredentials: false,
    smtpUser: '',
    smtpPass: '',
    maxNotificationsPerMin: 2,
    notifyCertExpire: false,
    notifyTransactionError: false,
    notifySystemFailure: false,
    notifyCEM: false,
    notifyConnectionProblem: false,
    notifyPostprocessingProblem: false,
    notifyClientServerProblem: false
  });

  const securityOptions = [
    { value: 0, label: 'Plain (no encryption)' },
    { value: 1, label: 'STARTTLS' },
    { value: 2, label: 'TLS/SSL' }
  ];

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
    marginBottom: '0.5rem',
    fontSize: '0.875rem',
    display: 'block'
  };

  const inputStyle = {
    padding: '0.5rem',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    fontSize: '0.875rem',
    width: '100%'
  };

  const selectStyle = {
    ...inputStyle
  };

  const checkboxContainerStyle = {
    display: 'flex',
    alignItems: 'flex-start',
    marginBottom: '0.75rem'
  };

  const checkboxStyle = {
    marginRight: '0.5rem',
    marginTop: '0.15rem',
    cursor: 'pointer'
  };

  const buttonStyle = {
    backgroundColor: '#007bff',
    color: 'white',
    padding: '0.5rem 1.5rem',
    border: 'none',
    borderRadius: '4px',
    fontSize: '0.875rem',
    cursor: 'pointer',
    fontWeight: '600'
  };

  const buttonDisabledStyle = {
    ...buttonStyle,
    backgroundColor: '#6c757d',
    cursor: 'not-allowed'
  };

  const helpTextStyle = {
    color: '#6c757d',
    fontSize: '0.75rem',
    marginTop: '0.25rem',
    marginBottom: '1rem'
  };

  const infoBoxStyle = {
    backgroundColor: '#d1ecf1',
    border: '1px solid #bee5eb',
    borderRadius: '4px',
    padding: '1rem',
    color: '#0c5460',
    fontSize: '0.875rem',
    marginTop: '1.5rem'
  };

  const rowStyle = {
    display: 'grid',
    gridTemplateColumns: '3fr 1fr',
    gap: '1rem',
    marginBottom: '1rem'
  };

  const rowStyle2 = {
    display: 'grid',
    gridTemplateColumns: '1fr 1fr',
    gap: '1rem',
    marginBottom: '1rem'
  };

  useEffect(() => {
    loadSettings();
  }, []);

  const loadSettings = async () => {
    setLoading(true);
    try {
      const response = await fetch('/as2/api/v1/notifications', {
        credentials: 'include',
      });

      if (response.ok) {
        const data = await response.json();
        setSettings({
          ...data,
          smtpPass: ''
        });
      } else {
        alert('Failed to load notification settings');
      }
    } catch (error) {
      console.error('Error loading notification settings:', error);
      alert('Failed to load notification settings');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      const response = await fetch('/as2/api/v1/notifications', {
        method: 'PUT',
        headers: { 'Content-Type': 'application/json' },
        credentials: 'include',
        body: JSON.stringify(settings)
      });

      if (response.ok) {
        alert('Notification settings saved successfully');
      } else {
        alert('Failed to save notification settings');
      }
    } catch (error) {
      console.error('Error saving notification settings:', error);
      alert('Failed to save notification settings');
    } finally {
      setSaving(false);
    }
  };

  if (loading) {
    return <LoadingPage message="Loading notification settings..." />;
  }

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'flex-end', marginBottom: '1rem' }}>
        <button
          style={saving ? buttonDisabledStyle : buttonStyle}
          onClick={handleSave}
          disabled={saving}
        >
          {saving ? 'Saving...' : 'Save Settings'}
        </button>
      </div>

      <div style={cardStyle}>
        <h2 style={{ marginTop: 0, marginBottom: '1.5rem' }}>SMTP Server Configuration</h2>

        <div style={rowStyle}>
          <div>
            <label style={labelStyle}>Mail Server (SMTP)</label>
            <input
              type="text"
              style={inputStyle}
              value={settings.mailServer || ''}
              onChange={(e) => setSettings({ ...settings, mailServer: e.target.value })}
              placeholder="smtp.example.com"
            />
          </div>
          <div>
            <label style={labelStyle}>Port</label>
            <input
              type="number"
              style={inputStyle}
              value={settings.mailServerPort}
              onChange={(e) => setSettings({ ...settings, mailServerPort: parseInt(e.target.value) || 25 })}
            />
          </div>
        </div>

        <div style={{ marginBottom: '1rem' }}>
          <label style={labelStyle}>Connection Security</label>
          <select
            style={selectStyle}
            value={settings.connectionSecurity}
            onChange={(e) => setSettings({ ...settings, connectionSecurity: parseInt(e.target.value) })}
          >
            {securityOptions.map(opt => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
          <div style={helpTextStyle}>
            STARTTLS: Port 587, TLS/SSL: Port 465, Plain: Port 25
          </div>
        </div>

        <div style={checkboxContainerStyle}>
          <input
            type="checkbox"
            id="useSMTPAuth"
            checked={settings.useSMTPAuthCredentials}
            onChange={(e) => setSettings({ ...settings, useSMTPAuthCredentials: e.target.checked })}
            style={checkboxStyle}
          />
          <label htmlFor="useSMTPAuth" style={{ cursor: 'pointer', fontSize: '0.875rem' }}>
            Use SMTP authentication
          </label>
        </div>

        {settings.useSMTPAuthCredentials && (
          <div style={rowStyle2}>
            <div>
              <label style={labelStyle}>SMTP Username</label>
              <input
                type="text"
                style={inputStyle}
                value={settings.smtpUser || ''}
                onChange={(e) => setSettings({ ...settings, smtpUser: e.target.value })}
              />
            </div>
            <div>
              <label style={labelStyle}>SMTP Password</label>
              <input
                type="password"
                style={inputStyle}
                value={settings.smtpPass || ''}
                onChange={(e) => setSettings({ ...settings, smtpPass: e.target.value })}
                placeholder="Leave blank to keep existing"
              />
              <div style={helpTextStyle}>
                Leave blank to keep existing password
              </div>
            </div>
          </div>
        )}
      </div>

      <div style={cardStyle}>
        <h2 style={{ marginTop: 0, marginBottom: '1.5rem' }}>Email Addresses</h2>

        <div style={{ marginBottom: '1rem' }}>
          <label style={labelStyle}>Notification Recipients</label>
          <input
            type="text"
            style={inputStyle}
            value={settings.notificationMail || ''}
            onChange={(e) => setSettings({ ...settings, notificationMail: e.target.value })}
            placeholder="admin@example.com, operator@example.com"
          />
          <div style={helpTextStyle}>
            Multiple email addresses can be comma-separated
          </div>
        </div>

        <div style={{ marginBottom: '1rem' }}>
          <label style={labelStyle}>Reply-To Address</label>
          <input
            type="email"
            style={inputStyle}
            value={settings.replyTo || ''}
            onChange={(e) => setSettings({ ...settings, replyTo: e.target.value })}
            placeholder="noreply@example.com"
          />
          <div style={helpTextStyle}>
            Email address shown as sender/reply-to in notifications
          </div>
        </div>

        <div style={{ marginBottom: '1rem' }}>
          <label style={labelStyle}>Max Notifications per Minute</label>
          <input
            type="number"
            style={{ ...inputStyle, maxWidth: '200px' }}
            value={settings.maxNotificationsPerMin}
            onChange={(e) => setSettings({ ...settings, maxNotificationsPerMin: parseInt(e.target.value) || 2 })}
            min="1"
            max="60"
          />
          <div style={helpTextStyle}>
            Prevents email flooding by bundling multiple notifications
          </div>
        </div>
      </div>

      <div style={cardStyle}>
        <h2 style={{ marginTop: 0, marginBottom: '1rem' }}>Notification Triggers</h2>
        <p style={{ color: '#6c757d', fontSize: '0.875rem', marginBottom: '1rem' }}>
          Select which events should trigger email notifications:
        </p>

        <div style={checkboxContainerStyle}>
          <input
            type="checkbox"
            id="notifyCertExpire"
            checked={settings.notifyCertExpire}
            onChange={(e) => setSettings({ ...settings, notifyCertExpire: e.target.checked })}
            style={checkboxStyle}
          />
          <label htmlFor="notifyCertExpire" style={{ cursor: 'pointer', fontSize: '0.875rem' }}>
            <strong>Certificate Expiration</strong> - Notify when certificates are about to expire
          </label>
        </div>

        <div style={checkboxContainerStyle}>
          <input
            type="checkbox"
            id="notifyTransactionError"
            checked={settings.notifyTransactionError}
            onChange={(e) => setSettings({ ...settings, notifyTransactionError: e.target.checked })}
            style={checkboxStyle}
          />
          <label htmlFor="notifyTransactionError" style={{ cursor: 'pointer', fontSize: '0.875rem' }}>
            <strong>Transaction Errors</strong> - Notify when AS2 message transactions fail
          </label>
        </div>

        <div style={checkboxContainerStyle}>
          <input
            type="checkbox"
            id="notifySystemFailure"
            checked={settings.notifySystemFailure}
            onChange={(e) => setSettings({ ...settings, notifySystemFailure: e.target.checked })}
            style={checkboxStyle}
          />
          <label htmlFor="notifySystemFailure" style={{ cursor: 'pointer', fontSize: '0.875rem' }}>
            <strong>System Failures</strong> - Notify when critical system errors occur
          </label>
        </div>

        <div style={checkboxContainerStyle}>
          <input
            type="checkbox"
            id="notifyConnectionProblem"
            checked={settings.notifyConnectionProblem}
            onChange={(e) => setSettings({ ...settings, notifyConnectionProblem: e.target.checked })}
            style={checkboxStyle}
          />
          <label htmlFor="notifyConnectionProblem" style={{ cursor: 'pointer', fontSize: '0.875rem' }}>
            <strong>Connection Problems</strong> - Notify when partner connection issues occur
          </label>
        </div>

        <div style={checkboxContainerStyle}>
          <input
            type="checkbox"
            id="notifyPostprocessingProblem"
            checked={settings.notifyPostprocessingProblem}
            onChange={(e) => setSettings({ ...settings, notifyPostprocessingProblem: e.target.checked })}
            style={checkboxStyle}
          />
          <label htmlFor="notifyPostprocessingProblem" style={{ cursor: 'pointer', fontSize: '0.875rem' }}>
            <strong>Postprocessing Problems</strong> - Notify when message postprocessing fails
          </label>
        </div>

        <div style={checkboxContainerStyle}>
          <input
            type="checkbox"
            id="notifyClientServerProblem"
            checked={settings.notifyClientServerProblem}
            onChange={(e) => setSettings({ ...settings, notifyClientServerProblem: e.target.checked })}
            style={checkboxStyle}
          />
          <label htmlFor="notifyClientServerProblem" style={{ cursor: 'pointer', fontSize: '0.875rem' }}>
            <strong>Client-Server Problems</strong> - Notify when client-server communication issues occur
          </label>
        </div>

        <div style={checkboxContainerStyle}>
          <input
            type="checkbox"
            id="notifyCEM"
            checked={settings.notifyCEM}
            onChange={(e) => setSettings({ ...settings, notifyCEM: e.target.checked })}
            style={checkboxStyle}
          />
          <label htmlFor="notifyCEM" style={{ cursor: 'pointer', fontSize: '0.875rem' }}>
            <strong>CEM (Certificate Exchange)</strong> - Notify about certificate exchange messages
          </label>
        </div>
      </div>

      <div style={infoBoxStyle}>
        <strong>Note:</strong> Make sure your SMTP server settings are correct to receive notifications. Test the configuration after saving.
      </div>
    </div>
  );
}
