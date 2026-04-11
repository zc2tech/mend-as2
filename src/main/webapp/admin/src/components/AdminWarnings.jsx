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
import { useQuery } from '@tanstack/react-query';
import { useAuth } from '../features/auth/useAuth';
import api from '../api/client';

export default function AdminWarnings() {
  const { user } = useAuth();
  const [warnings, setWarnings] = useState([]);
  const [dismissed, setDismissed] = useState([]);

  // Fetch current user details
  const { data: currentUser } = useQuery({
    queryKey: ['currentUser'],
    queryFn: async () => {
      const response = await api.get('/users/current');
      return response.data;
    },
    enabled: !!user && user.username === 'admin',
    staleTime: 5 * 60 * 1000 // 5 minutes
  });

  // Fetch notification settings
  const { data: notificationSettings } = useQuery({
    queryKey: ['notificationSettings'],
    queryFn: async () => {
      const response = await api.get('/notifications');
      return response.data;
    },
    enabled: !!user && user.username === 'admin',
    staleTime: 5 * 60 * 1000 // 5 minutes
  });

  useEffect(() => {
    if (!user || user.username !== 'admin') {
      setWarnings([]);
      return;
    }

    const newWarnings = [];

    // Check if admin user's email is blank
    if (currentUser && (!currentUser.email || currentUser.email.trim() === '')) {
      newWarnings.push({
        id: 'admin-email-blank',
        message: 'Admin user email is not configured. Email notifications will not work.',
        link: '/as2/webui/users'
      });
    }

    // Check notification settings
    if (notificationSettings) {
      const { mailServer, useSMTPAuthCredentials, smtpUser } = notificationSettings;

      // Only check if SMTP auth is enabled (not "None")
      if (useSMTPAuthCredentials) {
        if (!mailServer || mailServer.trim() === '') {
          newWarnings.push({
            id: 'mail-server-blank',
            message: 'Mail server host is not configured in notification settings.',
            link: '/as2/webui/system?tab=notification'
          });
        }

        if (!smtpUser || smtpUser.trim() === '') {
          newWarnings.push({
            id: 'smtp-user-blank',
            message: 'SMTP authorization user is not configured in notification settings.',
            link: '/as2/webui/system?tab=notification'
          });
        }
      } else {
        // If SMTP auth is disabled but mail server is blank
        if (!mailServer || mailServer.trim() === '') {
          newWarnings.push({
            id: 'mail-server-blank',
            message: 'Mail server host is not configured in notification settings.',
            link: '/as2/webui/system?tab=notification'
          });
        }
      }
    }

    // Filter out dismissed warnings
    const activeWarnings = newWarnings.filter(w => !dismissed.includes(w.id));
    setWarnings(activeWarnings);
  }, [currentUser, notificationSettings, user, dismissed]);

  const handleDismiss = (warningId) => {
    setDismissed([...dismissed, warningId]);
  };

  if (warnings.length === 0) {
    return null;
  }

  const warningContainerStyle = {
    margin: '1rem 0',
    padding: 0
  };

  const warningStyle = {
    backgroundColor: '#fff3cd',
    border: '1px solid #ffc107',
    borderRadius: '4px',
    padding: '0.75rem 1rem',
    marginBottom: '0.5rem',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'space-between'
  };

  const warningTextStyle = {
    color: '#856404',
    fontSize: '0.9rem',
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem'
  };

  const linkStyle = {
    color: '#0056b3',
    textDecoration: 'underline',
    marginLeft: '0.5rem',
    cursor: 'pointer'
  };

  const dismissButtonStyle = {
    backgroundColor: 'transparent',
    border: 'none',
    color: '#856404',
    cursor: 'pointer',
    fontSize: '1.2rem',
    padding: '0 0.5rem',
    lineHeight: 1
  };

  return (
    <div style={warningContainerStyle}>
      {warnings.map((warning) => (
        <div key={warning.id} style={warningStyle}>
          <div style={warningTextStyle}>
            <span>⚠️</span>
            <span>
              {warning.message}
              {warning.link && (
                <a href={warning.link} style={linkStyle}>
                  Configure now
                </a>
              )}
            </span>
          </div>
          <button
            onClick={() => handleDismiss(warning.id)}
            style={dismissButtonStyle}
            title="Dismiss"
          >
            ×
          </button>
        </div>
      ))}
    </div>
  );
}
