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
import { useAuth } from '../auth/useAuth';
import api from '../../api/client';

export default function TrackerConfig() {
  const { showToast } = useToast();
  const queryClient = useQueryClient();
  const { hasPermission } = useAuth();

  const canWrite = hasPermission('SYSTEM_WRITE');

  const [enabled, setEnabled] = useState(true);
  const [authRequired, setAuthRequired] = useState(true);
  const [maxSizeMB, setMaxSizeMB] = useState(2);
  const [rateLimitFailures, setRateLimitFailures] = useState(3);
  const [rateLimitWindowHours, setRateLimitWindowHours] = useState(1);
  const [rateLimitBlockMinutes, setRateLimitBlockMinutes] = useState(60);

  // Load configuration
  const { isLoading } = useQuery({
    queryKey: ['trackerConfig'],
    queryFn: async () => {
      const res = await api.get('/system/tracker/config');
      const config = res.data;

      setEnabled(config.enabled);
      setAuthRequired(config.authRequired);
      setMaxSizeMB(config.maxSizeMB);
      setRateLimitFailures(config.rateLimitFailures);
      setRateLimitWindowHours(config.rateLimitWindowHours);
      setRateLimitBlockMinutes(config.rateLimitBlockMinutes);

      return config;
    }
  });

  // Save mutation
  const saveMutation = useMutation({
    mutationFn: async () => {
      await api.post('/system/tracker/config', {
        enabled,
        authRequired,
        maxSizeMB,
        rateLimitFailures,
        rateLimitWindowHours,
        rateLimitBlockMinutes
      });
    },
    onSuccess: () => {
      showToast('Tracker configuration saved successfully', 'success');
      queryClient.invalidateQueries(['trackerConfig']);
    },
    onError: (error) => {
      showToast('Failed to save tracker configuration: ' + error.message, 'error');
    }
  });

  const handleSave = () => {
    if (!canWrite) {
      showToast('You do not have permission to modify system settings', 'error');
      return;
    }
    saveMutation.mutate();
  };

  if (isLoading) {
    return <LoadingPage message="Loading tracker configuration..." />;
  }

  const cardStyle = {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '1.5rem'
  };

  const sectionStyle = {
    marginBottom: '1.5rem',
    paddingBottom: '1.5rem',
    borderBottom: '1px solid #dee2e6'
  };

  const labelStyle = {
    display: 'block',
    marginBottom: '0.5rem',
    fontWeight: '600',
    fontSize: '0.875rem',
    color: '#495057'
  };

  const descriptionStyle = {
    fontSize: '0.875rem',
    color: '#6c757d',
    marginBottom: '0.75rem'
  };

  const inputStyle = {
    padding: '0.5rem',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    fontSize: '0.875rem',
    width: '200px'
  };

  const checkboxContainerStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    marginBottom: '0.5rem'
  };

  const saveButtonStyle = {
    padding: '0.5rem 1.5rem',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: saveMutation.isPending ? 'not-allowed' : 'pointer',
    fontSize: '0.875rem',
    fontWeight: '600',
    opacity: saveMutation.isPending ? 0.6 : 1
  };

  const gridStyle = {
    display: 'grid',
    gridTemplateColumns: 'repeat(3, 1fr)',
    gap: '1.5rem',
    marginTop: '1rem'
  };

  return (
    <div>
      <div style={cardStyle}>
        <p style={descriptionStyle}>
          Configure the tracker endpoint settings for receiving messages via HTTP POST to /as2/tracker.
        </p>

        {/* Enable Tracker */}
        <div style={sectionStyle}>
          <div style={checkboxContainerStyle}>
            <input
              type="checkbox"
              id="enableTracker"
              checked={enabled}
              onChange={(e) => setEnabled(e.target.checked)}
              disabled={!canWrite}
              style={{ cursor: canWrite ? 'pointer' : 'not-allowed' }}
            />
            <label htmlFor="enableTracker" style={{ ...labelStyle, marginBottom: 0, cursor: canWrite ? 'pointer' : 'not-allowed' }}>
              Enable Tracker Endpoint
            </label>
          </div>
          <p style={descriptionStyle}>
            Allow messages to be posted to /as2/tracker endpoint
          </p>
        </div>

        {/* Require Authentication */}
        <div style={sectionStyle}>
          <div style={checkboxContainerStyle}>
            <input
              type="checkbox"
              id="requireAuth"
              checked={authRequired}
              onChange={(e) => setAuthRequired(e.target.checked)}
              disabled={!canWrite}
              style={{ cursor: canWrite ? 'pointer' : 'not-allowed' }}
            />
            <label htmlFor="requireAuth" style={{ ...labelStyle, marginBottom: 0, cursor: canWrite ? 'pointer' : 'not-allowed' }}>
              Require Authentication
            </label>
          </div>
          <p style={descriptionStyle}>
            Require HTTP Basic Authentication for tracker endpoint access
          </p>
        </div>

        {/* Max Size */}
        <div style={sectionStyle}>
          <label style={labelStyle}>
            Maximum Message Size (MB)
          </label>
          <p style={descriptionStyle}>
            Maximum allowed size for tracker messages
          </p>
          <input
            type="number"
            min="1"
            max="100"
            style={inputStyle}
            value={maxSizeMB}
            onChange={(e) => setMaxSizeMB(parseInt(e.target.value) || 1)}
            disabled={!canWrite}
          />
        </div>

        {/* Rate Limiting Section */}
        <div style={{ marginBottom: '1.5rem' }}>
          <h3 style={{ fontSize: '1rem', fontWeight: '600', marginBottom: '0.75rem' }}>
            Rate Limiting
          </h3>
          <p style={descriptionStyle}>
            Configure rate limiting to prevent abuse. When a client exceeds the failure threshold within the time window, they will be temporarily blocked.
          </p>

          <div style={gridStyle}>
            <div>
              <label style={labelStyle}>
                Max Failed Attempts
              </label>
              <p style={descriptionStyle}>
                Number of failed auth attempts before blocking
              </p>
              <input
                type="number"
                min="1"
                max="100"
                style={inputStyle}
                value={rateLimitFailures}
                onChange={(e) => setRateLimitFailures(parseInt(e.target.value) || 1)}
                disabled={!canWrite}
              />
            </div>

            <div>
              <label style={labelStyle}>
                Time Window (Hours)
              </label>
              <p style={descriptionStyle}>
                Time period to track failed attempts
              </p>
              <input
                type="number"
                min="1"
                max="24"
                style={inputStyle}
                value={rateLimitWindowHours}
                onChange={(e) => setRateLimitWindowHours(parseInt(e.target.value) || 1)}
                disabled={!canWrite}
              />
            </div>

            <div>
              <label style={labelStyle}>
                Block Duration (Minutes)
              </label>
              <p style={descriptionStyle}>
                How long to block after exceeding limit
              </p>
              <input
                type="number"
                min="1"
                max="1440"
                style={inputStyle}
                value={rateLimitBlockMinutes}
                onChange={(e) => setRateLimitBlockMinutes(parseInt(e.target.value) || 1)}
                disabled={!canWrite}
              />
            </div>
          </div>
        </div>

        {/* Save Button */}
        {canWrite ? (
          <div style={{ display: 'flex', justifyContent: 'flex-end', paddingTop: '1rem', borderTop: '1px solid #dee2e6' }}>
            <button
              onClick={handleSave}
              disabled={saveMutation.isPending}
              style={saveButtonStyle}
            >
              {saveMutation.isPending ? 'Saving...' : 'Save Configuration'}
            </button>
          </div>
        ) : (
          <div style={{ fontSize: '0.875rem', color: '#6c757d', fontStyle: 'italic', paddingTop: '1rem', borderTop: '1px solid #dee2e6' }}>
            You have read-only access to this configuration.
          </div>
        )}
      </div>
    </div>
  );
}
