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
import { useAuth } from '../auth/useAuth';
import { Navigate } from 'react-router-dom';
import SettingsTab from './SettingsTab';
import GlobalWhitelistTab from './GlobalWhitelistTab';
import PartnerWhitelistTab from './PartnerWhitelistTab';
import UserWhitelistTab from './UserWhitelistTab';
import BlockLogTab from './BlockLogTab';

export default function IPWhitelistManagement() {
  const { user } = useAuth();
  const [activeTab, setActiveTab] = useState('settings');

  // Only 'admin' super user can access this page
  if (user?.username !== 'admin') {
    return <Navigate to="/" replace />;
  }

  const containerStyle = {
    padding: '2rem',
    maxWidth: '1400px',
    margin: '0 auto'
  };

  const headerStyle = {
    marginBottom: '2rem'
  };

  const titleStyle = {
    fontSize: '1.875rem',
    fontWeight: 'bold',
    marginBottom: '0.5rem'
  };

  const subtitleStyle = {
    color: '#666',
    fontSize: '0.95rem'
  };

  const tabsContainerStyle = {
    borderBottom: '2px solid #dee2e6',
    marginBottom: '2rem',
    display: 'flex',
    gap: '1rem'
  };

  const getTabStyle = (tabName) => ({
    padding: '0.75rem 1.5rem',
    cursor: 'pointer',
    border: 'none',
    background: 'none',
    fontSize: '0.95rem',
    fontWeight: '500',
    color: activeTab === tabName ? '#007bff' : '#666',
    borderBottom: activeTab === tabName ? '3px solid #007bff' : '3px solid transparent',
    transition: 'all 0.2s',
    marginBottom: '-2px'
  });

  return (
    <div style={containerStyle}>
      <div style={headerStyle}>
        <h1 style={titleStyle}>IP Whitelist Management</h1>
        <p style={subtitleStyle}>
          Control which IP addresses can access AS2 endpoints, Tracker, WebUI, and API.
          Changes take effect within 60 seconds (cache refresh interval).
        </p>
      </div>

      <div style={tabsContainerStyle}>
        <button style={getTabStyle('settings')} onClick={() => setActiveTab('settings')}>
          Settings
        </button>
        <button style={getTabStyle('global')} onClick={() => setActiveTab('global')}>
          Global Whitelist
        </button>
        <button style={getTabStyle('partner')} onClick={() => setActiveTab('partner')}>
          Partner-Specific
        </button>
        <button style={getTabStyle('user')} onClick={() => setActiveTab('user')}>
          User-Specific
        </button>
        <button style={getTabStyle('blocklog')} onClick={() => setActiveTab('blocklog')}>
          Block Log
        </button>
      </div>

      {activeTab === 'settings' && <SettingsTab />}
      {activeTab === 'global' && <GlobalWhitelistTab />}
      {activeTab === 'partner' && <PartnerWhitelistTab />}
      {activeTab === 'user' && <UserWhitelistTab />}
      {activeTab === 'blocklog' && <BlockLogTab />}
    </div>
  );
}
