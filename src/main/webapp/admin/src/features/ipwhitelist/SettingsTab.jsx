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
import { useWhitelistSettings, useUpdateWhitelistSettings } from './useIPWhitelist';
import { useToast } from '../../components/Toast';
import { LoadingPage } from '../../components/Loading';

export default function SettingsTab() {
  const { data: settings, isLoading, error } = useWhitelistSettings();
  const updateMutation = useUpdateWhitelistSettings();
  const toast = useToast();

  const [enabledAS2, setEnabledAS2] = useState(false);
  const [enabledTracker, setEnabledTracker] = useState(false);
  const [enabledWebUI, setEnabledWebUI] = useState(true);
  const [enabledAPI, setEnabledAPI] = useState(true);
  const [mode, setMode] = useState('GLOBAL_AND_SPECIFIC');
  const [logRetentionDays, setLogRetentionDays] = useState(30);
  const [hasChanges, setHasChanges] = useState(false);

  // Load settings into state
  useEffect(() => {
    if (settings) {
      setEnabledAS2(settings.enabledAS2 || false);
      setEnabledTracker(settings.enabledTracker || false);
      setEnabledWebUI(settings.enabledWebUI || false);
      setEnabledAPI(settings.enabledAPI || false);
      setMode(settings.mode || 'GLOBAL_AND_SPECIFIC');
      setLogRetentionDays(settings.logRetentionDays || 30);
      setHasChanges(false);
    }
  }, [settings]);

  // Track changes
  useEffect(() => {
    if (settings) {
      const changed =
        enabledAS2 !== (settings.enabledAS2 || false) ||
        enabledTracker !== (settings.enabledTracker || false) ||
        enabledWebUI !== (settings.enabledWebUI || false) ||
        enabledAPI !== (settings.enabledAPI || false) ||
        mode !== (settings.mode || 'GLOBAL_AND_SPECIFIC') ||
        logRetentionDays !== (settings.logRetentionDays || 30);
      setHasChanges(changed);
    }
  }, [settings, enabledAS2, enabledTracker, enabledWebUI, enabledAPI, mode, logRetentionDays]);

  const handleSave = async () => {
    try {
      await updateMutation.mutateAsync({
        enabledAS2,
        enabledTracker,
        enabledWebUI,
        enabledAPI,
        mode,
        logRetentionDays
      });
      toast.success('Settings saved successfully. Changes will take effect within 60 seconds.');
      setHasChanges(false);
    } catch (error) {
      toast.error('Failed to save settings: ' + (error.response?.data?.error || error.message));
    }
  };

  if (isLoading) {
    return <LoadingPage message="Loading settings..." />;
  }

  if (error) {
    return <div style={{ color: 'red' }}>Error loading settings: {error.message}</div>;
  }

  const containerStyle = {
    maxWidth: '800px'
  };

  const sectionStyle = {
    backgroundColor: 'white',
    padding: '2rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '1.5rem'
  };

  const sectionTitleStyle = {
    fontSize: '1.125rem',
    fontWeight: '600',
    marginBottom: '1rem',
    borderBottom: '2px solid #e0e0e0',
    paddingBottom: '0.5rem'
  };

  const checkboxContainerStyle = {
    marginBottom: '1rem',
    display: 'flex',
    alignItems: 'flex-start',
    gap: '0.75rem'
  };

  const checkboxStyle = {
    marginTop: '0.25rem',
    cursor: 'pointer'
  };

  const labelStyle = {
    fontSize: '0.95rem',
    cursor: 'pointer'
  };

  const helpTextStyle = {
    fontSize: '0.85rem',
    color: '#666',
    marginTop: '0.25rem'
  };

  const radioContainerStyle = {
    marginBottom: '1rem'
  };

  const radioLabelStyle = {
    display: 'flex',
    alignItems: 'flex-start',
    gap: '0.75rem',
    marginBottom: '0.75rem',
    cursor: 'pointer'
  };

  const inputContainerStyle = {
    marginTop: '1rem'
  };

  const inputStyle = {
    padding: '0.5rem',
    borderRadius: '4px',
    border: '1px solid #ccc',
    fontSize: '0.95rem',
    width: '100px'
  };

  const buttonContainerStyle = {
    marginTop: '2rem',
    paddingTop: '1.5rem',
    borderTop: '2px solid #e0e0e0',
    display: 'flex',
    gap: '1rem',
    alignItems: 'center'
  };

  const saveButtonStyle = {
    padding: '0.75rem 2rem',
    backgroundColor: hasChanges ? '#007bff' : '#ccc',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: hasChanges ? 'pointer' : 'not-allowed',
    fontSize: '0.95rem',
    fontWeight: '600'
  };

  const infoBoxStyle = {
    backgroundColor: '#e7f3ff',
    border: '1px solid #b3d9ff',
    borderRadius: '4px',
    padding: '1rem',
    fontSize: '0.9rem',
    color: '#004085',
    marginBottom: '1.5rem'
  };

  return (
    <div style={containerStyle}>
      <div style={infoBoxStyle}>
        ℹ️ <strong>Note:</strong> Changes to these settings take effect within 60 seconds (cache refresh interval).
        When whitelist is enabled for an endpoint, only whitelisted IPs will be allowed (default-deny security model).
      </div>

      {/* Enable Flags Section */}
      <div style={sectionStyle}>
        <h3 style={sectionTitleStyle}>Enable Whitelist per Endpoint Type</h3>

        <div style={checkboxContainerStyle}>
          <input
            type="checkbox"
            id="enabledAS2"
            checked={enabledAS2}
            onChange={(e) => setEnabledAS2(e.target.checked)}
            style={checkboxStyle}
          />
          <div>
            <label htmlFor="enabledAS2" style={labelStyle}>
              <strong>AS2 Endpoint</strong>
            </label>
            <div style={helpTextStyle}>
              Control which IPs can send AS2 messages to this server
            </div>
          </div>
        </div>

        <div style={checkboxContainerStyle}>
          <input
            type="checkbox"
            id="enabledTracker"
            checked={enabledTracker}
            onChange={(e) => setEnabledTracker(e.target.checked)}
            style={checkboxStyle}
          />
          <div>
            <label htmlFor="enabledTracker" style={labelStyle}>
              <strong>Tracker Endpoint</strong>
            </label>
            <div style={helpTextStyle}>
              Control which IPs can send tracker messages to this server
            </div>
          </div>
        </div>

        <div style={checkboxContainerStyle}>
          <input
            type="checkbox"
            id="enabledWebUI"
            checked={enabledWebUI}
            onChange={(e) => setEnabledWebUI(e.target.checked)}
            style={checkboxStyle}
          />
          <div>
            <label htmlFor="enabledWebUI" style={labelStyle}>
              <strong>WebUI Access</strong>
            </label>
            <div style={helpTextStyle}>
              Control which IPs can access the web interface
            </div>
          </div>
        </div>

        <div style={checkboxContainerStyle}>
          <input
            type="checkbox"
            id="enabledAPI"
            checked={enabledAPI}
            onChange={(e) => setEnabledAPI(e.target.checked)}
            style={checkboxStyle}
          />
          <div>
            <label htmlFor="enabledAPI" style={labelStyle}>
              <strong>REST API Access</strong>
            </label>
            <div style={helpTextStyle}>
              Control which IPs can make REST API calls
            </div>
          </div>
        </div>
      </div>

      {/* Mode Selection Section */}
      <div style={sectionStyle}>
        <h3 style={sectionTitleStyle}>Whitelist Mode</h3>
        <div style={{ fontSize: '0.9rem', color: '#666', marginBottom: '1rem' }}>
          This mode applies to all enabled endpoints above.
        </div>

        <div style={radioContainerStyle}>
          <label style={radioLabelStyle}>
            <input
              type="radio"
              name="mode"
              value="GLOBAL_AND_SPECIFIC"
              checked={mode === 'GLOBAL_AND_SPECIFIC'}
              onChange={(e) => setMode(e.target.value)}
            />
            <div>
              <strong>Global + Specific (Recommended)</strong>
              <div style={helpTextStyle}>
                Check both global whitelist AND partner/user-specific whitelists. Most flexible option.
              </div>
            </div>
          </label>

          <label style={radioLabelStyle}>
            <input
              type="radio"
              name="mode"
              value="GLOBAL_ONLY"
              checked={mode === 'GLOBAL_ONLY'}
              onChange={(e) => setMode(e.target.value)}
            />
            <div>
              <strong>Global Only</strong>
              <div style={helpTextStyle}>
                Only check global whitelist. Ignore partner/user-specific entries. Same rules for everyone.
              </div>
            </div>
          </label>

          <label style={radioLabelStyle}>
            <input
              type="radio"
              name="mode"
              value="PARTNER_ONLY"
              checked={mode === 'PARTNER_ONLY'}
              onChange={(e) => setMode(e.target.value)}
            />
            <div>
              <strong>Partner/User Specific Only</strong>
              <div style={helpTextStyle}>
                Only check partner/user-specific whitelists. Ignore global entries. Each entity has isolated rules.
              </div>
            </div>
          </label>
        </div>
      </div>

      {/* Log Retention Section */}
      <div style={sectionStyle}>
        <h3 style={sectionTitleStyle}>Block Log Settings</h3>

        <div style={inputContainerStyle}>
          <label htmlFor="logRetentionDays" style={{ fontSize: '0.95rem', fontWeight: '600', display: 'block', marginBottom: '0.5rem' }}>
            Block Log Retention Days
          </label>
          <input
            type="number"
            id="logRetentionDays"
            min="1"
            max="365"
            value={logRetentionDays}
            onChange={(e) => setLogRetentionDays(parseInt(e.target.value) || 30)}
            style={inputStyle}
          />
          <div style={helpTextStyle}>
            How many days to keep blocked IP attempt logs (1-365 days)
          </div>
        </div>
      </div>

      {/* Save Button */}
      <div style={buttonContainerStyle}>
        <button
          onClick={handleSave}
          disabled={!hasChanges || updateMutation.isPending}
          style={saveButtonStyle}
        >
          {updateMutation.isPending ? 'Saving...' : 'Save Settings'}
        </button>
        {hasChanges && (
          <span style={{ fontSize: '0.9rem', color: '#ff6b6b' }}>
            ● Unsaved changes
          </span>
        )}
      </div>
    </div>
  );
}
