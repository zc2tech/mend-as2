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
    return <LoadingPage />;
  }

  return (
    <div className="space-y-6">
      <div>
        <h2 className="text-2xl font-bold mb-4">Tracker Configuration</h2>
        <p className="text-gray-600 mb-6">
          Configure the tracker endpoint settings for receiving messages via HTTP POST.
        </p>
      </div>

      <div className="bg-white rounded-lg shadow p-6 space-y-6">
        {/* Enable Tracker */}
        <div className="flex items-center justify-between">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Enable Tracker Endpoint
            </label>
            <p className="text-sm text-gray-500">
              Allow messages to be posted to /as2/tracker endpoint
            </p>
          </div>
          <label className="relative inline-flex items-center cursor-pointer">
            <input
              type="checkbox"
              className="sr-only peer"
              checked={enabled}
              onChange={(e) => setEnabled(e.target.checked)}
              disabled={!canWrite}
            />
            <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
          </label>
        </div>

        {/* Require Authentication */}
        <div className="flex items-center justify-between">
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-1">
              Require Authentication
            </label>
            <p className="text-sm text-gray-500">
              Require HTTP Basic Authentication for tracker endpoint access
            </p>
          </div>
          <label className="relative inline-flex items-center cursor-pointer">
            <input
              type="checkbox"
              className="sr-only peer"
              checked={authRequired}
              onChange={(e) => setAuthRequired(e.target.checked)}
              disabled={!canWrite}
            />
            <div className="w-11 h-6 bg-gray-200 peer-focus:outline-none peer-focus:ring-4 peer-focus:ring-blue-300 rounded-full peer peer-checked:after:translate-x-full peer-checked:after:border-white after:content-[''] after:absolute after:top-[2px] after:left-[2px] after:bg-white after:border-gray-300 after:border after:rounded-full after:h-5 after:w-5 after:transition-all peer-checked:bg-blue-600"></div>
          </label>
        </div>

        {/* Max Size */}
        <div>
          <label className="block text-sm font-medium text-gray-700 mb-1">
            Maximum Message Size (MB)
          </label>
          <p className="text-sm text-gray-500 mb-2">
            Maximum allowed size for tracker messages
          </p>
          <input
            type="number"
            min="1"
            max="100"
            className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
            value={maxSizeMB}
            onChange={(e) => setMaxSizeMB(parseInt(e.target.value) || 1)}
            disabled={!canWrite}
          />
        </div>

        {/* Rate Limiting Section */}
        <div className="pt-4 border-t">
          <h3 className="text-lg font-semibold mb-4">Rate Limiting</h3>
          <p className="text-sm text-gray-500 mb-4">
            Configure rate limiting to prevent abuse. When a client exceeds the failure threshold within the time window, they will be temporarily blocked.
          </p>

          <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Max Failed Attempts
              </label>
              <p className="text-sm text-gray-500 mb-2">
                Number of failed auth attempts before blocking
              </p>
              <input
                type="number"
                min="1"
                max="100"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={rateLimitFailures}
                onChange={(e) => setRateLimitFailures(parseInt(e.target.value) || 1)}
                disabled={!canWrite}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Time Window (Hours)
              </label>
              <p className="text-sm text-gray-500 mb-2">
                Time period to track failed attempts
              </p>
              <input
                type="number"
                min="1"
                max="24"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={rateLimitWindowHours}
                onChange={(e) => setRateLimitWindowHours(parseInt(e.target.value) || 1)}
                disabled={!canWrite}
              />
            </div>

            <div>
              <label className="block text-sm font-medium text-gray-700 mb-1">
                Block Duration (Minutes)
              </label>
              <p className="text-sm text-gray-500 mb-2">
                How long to block after exceeding limit
              </p>
              <input
                type="number"
                min="1"
                max="1440"
                className="w-full px-3 py-2 border border-gray-300 rounded-md focus:outline-none focus:ring-2 focus:ring-blue-500"
                value={rateLimitBlockMinutes}
                onChange={(e) => setRateLimitBlockMinutes(parseInt(e.target.value) || 1)}
                disabled={!canWrite}
              />
            </div>
          </div>
        </div>

        {/* Save Button */}
        {canWrite && (
          <div className="flex justify-end pt-4 border-t">
            <button
              onClick={handleSave}
              disabled={saveMutation.isPending}
              className="px-6 py-2 bg-blue-600 text-white rounded-md hover:bg-blue-700 disabled:bg-gray-400 disabled:cursor-not-allowed transition-colors"
            >
              {saveMutation.isPending ? 'Saving...' : 'Save Configuration'}
            </button>
          </div>
        )}

        {!canWrite && (
          <div className="text-sm text-gray-500 italic pt-4 border-t">
            You have read-only access to this configuration.
          </div>
        )}
      </div>
    </div>
  );
}
