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

export default function MaintenanceSettings() {
  const [loading, setLoading] = useState(true);
  const [saving, setSaving] = useState(false);
  const [isCommunityEdition, setIsCommunityEdition] = useState(false);
  const [settings, setSettings] = useState({
    autoMsgDelete: true,
    autoMsgDeleteOlderThan: '5',
    autoMsgDeleteOlderThanMultiplierS: '86400',
    autoStatsDelete: true,
    autoStatsDeleteOlderThan: '180',
    autoLogdirDelete: false,
    autoLogdirDeleteOlderThan: '180',
    autoTrackerDelete: false,
    autoTrackerDeleteOlderThan: '180'
  });

  const timeUnitOptions = [
    { label: 'Day', value: String(86400) },
    { label: 'Hour', value: String(3600) },
    { label: 'Minute', value: String(60) }
  ];

  const cardStyle = {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '1.5rem'
  };

  const rowStyle = {
    display: 'grid',
    gridTemplateColumns: 'auto 100px 120px auto 100px',
    gap: '0.75rem',
    alignItems: 'center',
    marginBottom: '1rem',
    paddingLeft: '1rem'
  };

  const labelStyle = {
    fontWeight: '500',
    color: '#495057',
    fontSize: '0.875rem'
  };

  const inputStyle = {
    padding: '0.4rem',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    fontSize: '0.875rem',
    width: '100%',
    textAlign: 'right'
  };

  const selectStyle = {
    padding: '0.4rem',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    fontSize: '0.875rem',
    width: '100%'
  };

  const switchStyle = {
    display: 'inline-flex',
    alignItems: 'center',
    cursor: 'pointer',
    userSelect: 'none'
  };

  const switchTrackStyle = (isOn) => ({
    width: '44px',
    height: '24px',
    backgroundColor: isOn ? '#28a745' : '#6c757d',
    borderRadius: '12px',
    position: 'relative',
    transition: 'background-color 0.2s',
    marginRight: '0.5rem'
  });

  const switchThumbStyle = (isOn) => ({
    width: '20px',
    height: '20px',
    backgroundColor: 'white',
    borderRadius: '50%',
    position: 'absolute',
    top: '2px',
    left: isOn ? '22px' : '2px',
    transition: 'left 0.2s',
    boxShadow: '0 1px 2px rgba(0,0,0,0.2)'
  });

  const switchTextStyle = {
    fontSize: '0.75rem',
    fontWeight: '600',
    color: '#495057'
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

  const sectionHeaderStyle = {
    fontWeight: '600',
    color: '#495057',
    fontSize: '0.875rem',
    marginBottom: '1rem',
    paddingBottom: '0.5rem',
    borderBottom: '2px solid #dee2e6'
  };

  useEffect(() => {
    loadSettings();
  }, []);

  const loadSettings = async () => {
    setLoading(true);
    try {
      const keys = [
        'commed',
        'automsgdelete',
        'automsgdeleteolderthan',
        'automsgdeleteolderthanmults',
        'autostatsdelete',
        'autostatsdeleteolderthan',
        'autologdirdelete',
        'autologdirdeleteolderthan',
        'autotrackerdelete',
        'autotrackerdeleteolderthan'
      ];

      const values = {};
      for (const key of keys) {
        const response = await fetch(`/as2/api/v1/preferences/${key}`, {
          credentials: 'include',
        });
        if (response.ok) {
          const data = await response.json();
          values[key] = data.value;
        }
      }

      setIsCommunityEdition(values.commed === 'TRUE');
      setSettings({
        autoMsgDelete: values.automsgdelete === 'TRUE',
        autoMsgDeleteOlderThan: values.automsgdeleteolderthan || '5',
        autoMsgDeleteOlderThanMultiplierS: values.automsgdeleteolderthanmults || '86400',
        autoStatsDelete: values.autostatsdelete === 'TRUE',
        autoStatsDeleteOlderThan: values.autostatsdeleteolderthan || '180',
        autoLogdirDelete: values.autologdirdelete === 'TRUE',
        autoLogdirDeleteOlderThan: values.autologdirdeleteolderthan || '180',
        autoTrackerDelete: values.autotrackerdelete === 'TRUE',
        autoTrackerDeleteOlderThan: values.autotrackerdeleteolderthan || '180'
      });
    } catch (error) {
      console.error('Error loading maintenance settings:', error);
      alert('Failed to load maintenance settings');
    } finally {
      setLoading(false);
    }
  };

  const handleSave = async () => {
    setSaving(true);
    try {
      const updates = [
        { key: 'automsgdelete', value: settings.autoMsgDelete ? 'TRUE' : 'FALSE' },
        { key: 'automsgdeleteolderthan', value: settings.autoMsgDeleteOlderThan },
        { key: 'automsgdeleteolderthanmults', value: settings.autoMsgDeleteOlderThanMultiplierS },
        { key: 'autostatsdelete', value: settings.autoStatsDelete ? 'TRUE' : 'FALSE' },
        { key: 'autostatsdeleteolderthan', value: settings.autoStatsDeleteOlderThan },
        { key: 'autologdirdelete', value: settings.autoLogdirDelete ? 'TRUE' : 'FALSE' },
        { key: 'autologdirdeleteolderthan', value: settings.autoLogdirDeleteOlderThan },
        { key: 'autotrackerdelete', value: settings.autoTrackerDelete ? 'TRUE' : 'FALSE' },
        { key: 'autotrackerdeleteolderthan', value: settings.autoTrackerDeleteOlderThan }
      ];

      for (const update of updates) {
        await fetch(`/as2/api/v1/preferences/${update.key}`, {
          method: 'PUT',
          headers: { 'Content-Type': 'application/json' },
          credentials: 'include',
          body: JSON.stringify({ value: update.value })
        });
      }

      alert('Maintenance settings saved successfully');
    } catch (error) {
      console.error('Error saving maintenance settings:', error);
      alert('Failed to save maintenance settings');
    } finally {
      setSaving(false);
    }
  };

  const ToggleSwitch = ({ checked, onChange }) => (
    <div style={switchStyle} onClick={() => onChange(!checked)}>
      <div style={switchTrackStyle(checked)}>
        <div style={switchThumbStyle(checked)} />
      </div>
      <span style={switchTextStyle}>{checked ? 'ON' : 'OFF'}</span>
    </div>
  );

  if (loading) {
    return <LoadingPage message="Loading maintenance settings..." />;
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
        <h2 style={{ marginTop: 0, marginBottom: '1.5rem' }}>System Maintenance</h2>

        <div style={sectionHeaderStyle}>Auto delete</div>

        <div style={rowStyle}>
          <div style={labelStyle}>Transaction entries older than</div>
          <input
            type="number"
            style={inputStyle}
            value={settings.autoMsgDeleteOlderThan}
            onChange={(e) => setSettings({ ...settings, autoMsgDeleteOlderThan: e.target.value })}
            min="1"
          />
          <select
            style={selectStyle}
            value={settings.autoMsgDeleteOlderThanMultiplierS}
            onChange={(e) => setSettings({ ...settings, autoMsgDeleteOlderThanMultiplierS: e.target.value })}
          >
            {timeUnitOptions.map(opt => (
              <option key={opt.value} value={opt.value}>{opt.label}</option>
            ))}
          </select>
          <div></div>
          <ToggleSwitch
            checked={settings.autoMsgDelete}
            onChange={(val) => setSettings({ ...settings, autoMsgDelete: val })}
          />
        </div>

        {!isCommunityEdition && (
          <div style={rowStyle}>
            <div style={labelStyle}>Statistics data older than</div>
            <input
              type="number"
              style={inputStyle}
              value={settings.autoStatsDeleteOlderThan}
              onChange={(e) => setSettings({ ...settings, autoStatsDeleteOlderThan: e.target.value })}
              min="1"
            />
            <div style={{ ...labelStyle, textAlign: 'left' }}>days</div>
            <div></div>
            <ToggleSwitch
              checked={settings.autoStatsDelete}
              onChange={(val) => setSettings({ ...settings, autoStatsDelete: val })}
            />
          </div>
        )}

        <div style={rowStyle}>
          <div style={labelStyle}>Log data older than</div>
          <input
            type="number"
            style={inputStyle}
            value={settings.autoLogdirDeleteOlderThan}
            onChange={(e) => setSettings({ ...settings, autoLogdirDeleteOlderThan: e.target.value })}
            min="1"
          />
          <div style={{ ...labelStyle, textAlign: 'left' }}>days</div>
          <div></div>
          <ToggleSwitch
            checked={settings.autoLogdirDelete}
            onChange={(val) => setSettings({ ...settings, autoLogdirDelete: val })}
          />
        </div>

        <div style={rowStyle}>
          <div style={labelStyle}>Tracker messages older than</div>
          <input
            type="number"
            style={inputStyle}
            value={settings.autoTrackerDeleteOlderThan}
            onChange={(e) => setSettings({ ...settings, autoTrackerDeleteOlderThan: e.target.value })}
            min="1"
          />
          <div style={{ ...labelStyle, textAlign: 'left' }}>days</div>
          <div></div>
          <ToggleSwitch
            checked={settings.autoTrackerDelete}
            onChange={(val) => setSettings({ ...settings, autoTrackerDelete: val })}
          />
        </div>
      </div>
    </div>
  );
}
