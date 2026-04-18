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
import { useSearchParams } from 'react-router-dom';
import HTTPServerConfig from './HTTPServerConfig';
import TrackerConfig from './TrackerConfig';
import SystemEvents from './SystemEvents';
import ServerLogSearch from './ServerLogSearch';
import MaintenanceSettings from './MaintenanceSettings';
import NotificationSettings from './NotificationSettings';
import SystemTLS from './SystemTLS';
import QueueConfig from './QueueConfig';
import { useAuth } from '../auth/useAuth';
import { PERMISSIONS } from '../../constants/permissions';

export default function SystemInfo() {
  const [searchParams] = useSearchParams();
  const [activeTab, setActiveTab] = useState('httpConfig');
  const { hasPermission } = useAuth();

  // Check for tab parameter in URL on mount
  useEffect(() => {
    const tabParam = searchParams.get('tab');
    if (tabParam) {
      setActiveTab(tabParam);
    }
  }, [searchParams]);

  // Check permissions for each tab
  const hasHTTPConfigPermission = hasPermission(PERMISSIONS.SYSTEM_CONFIG_CONNECTIVITY);
  const hasTLSReadPermission = hasPermission(PERMISSIONS.CERT_TLS_READ);
  const hasTrackerReadPermission = hasPermission(PERMISSIONS.TRACKER_CONFIG_READ);
  const hasEventsReadPermission = hasPermission(PERMISSIONS.SYSTEM_EVENTS_READ);
  const hasLogsReadPermission = hasPermission(PERMISSIONS.SYSTEM_LOGS_READ);
  const hasMaintenancePermission = hasPermission(PERMISSIONS.SYSTEM_CONFIG_MAINTENANCE);
  const hasNotificationPermission = hasPermission(PERMISSIONS.SYSTEM_CONFIG_NOTIFICATIONS);
  const hasInfoReadPermission = hasPermission(PERMISSIONS.SYSTEM_INFO_READ);

  const tabStyle = {
    display: 'flex',
    borderBottom: '2px solid #dee2e6',
    marginBottom: '1.5rem',
    gap: '0.5rem'
  };

  const tabButtonStyle = (isActive) => ({
    padding: '0.75rem 1.5rem',
    backgroundColor: isActive ? '#007bff' : 'transparent',
    color: isActive ? 'white' : '#495057',
    border: 'none',
    borderBottom: isActive ? '2px solid #007bff' : '2px solid transparent',
    cursor: 'pointer',
    fontWeight: isActive ? '600' : 'normal',
    fontSize: '0.9rem',
    transition: 'all 0.2s',
    borderRadius: '4px 4px 0 0'
  });

  return (
    <div>
      <h1>System</h1>

      <div style={tabStyle}>
        {hasHTTPConfigPermission && (
          <button
            style={tabButtonStyle(activeTab === 'httpConfig')}
            onClick={() => setActiveTab('httpConfig')}
          >
            HTTP Server Configuration
          </button>
        )}
        {hasTLSReadPermission && (
          <button
            style={tabButtonStyle(activeTab === 'tls')}
            onClick={() => setActiveTab('tls')}
          >
            TLS
          </button>
        )}
        {hasTrackerReadPermission && (
          <button
            style={tabButtonStyle(activeTab === 'tracker')}
            onClick={() => setActiveTab('tracker')}
          >
            Tracker Conf
          </button>
        )}
        {hasEventsReadPermission && (
          <button
            style={tabButtonStyle(activeTab === 'events')}
            onClick={() => setActiveTab('events')}
          >
            System Events
          </button>
        )}
        {hasLogsReadPermission && (
          <button
            style={tabButtonStyle(activeTab === 'serverlog')}
            onClick={() => setActiveTab('serverlog')}
          >
            Search in Server Log
          </button>
        )}
        {hasMaintenancePermission && (
          <button
            style={tabButtonStyle(activeTab === 'maintenance')}
            onClick={() => setActiveTab('maintenance')}
          >
            Maintenance
          </button>
        )}
        {hasNotificationPermission && (
          <button
            style={tabButtonStyle(activeTab === 'notification')}
            onClick={() => setActiveTab('notification')}
          >
            Notification
          </button>
        )}
        {hasInfoReadPermission && (
          <button
            style={tabButtonStyle(activeTab === 'queue')}
            onClick={() => setActiveTab('queue')}
          >
            Queue
          </button>
        )}
      </div>

      {activeTab === 'httpConfig' && hasHTTPConfigPermission && <HTTPServerConfig />}
      {activeTab === 'tracker' && hasTrackerReadPermission && <TrackerConfig />}
      {activeTab === 'events' && hasEventsReadPermission && <SystemEvents />}
      {activeTab === 'serverlog' && hasLogsReadPermission && <ServerLogSearch />}
      {activeTab === 'maintenance' && hasMaintenancePermission && <MaintenanceSettings />}
      {activeTab === 'notification' && hasNotificationPermission && <NotificationSettings />}
      {activeTab === 'tls' && hasTLSReadPermission && <SystemTLS />}
      {activeTab === 'queue' && hasInfoReadPermission && <QueueConfig />}
    </div>
  );
}
