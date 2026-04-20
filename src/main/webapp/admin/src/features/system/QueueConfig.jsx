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

import { useQuery } from '@tanstack/react-query';
import api from '../../api/client';
import { LoadingPage } from '../../components/Loading';

export default function QueueConfig() {
  const { data: config, isLoading, error } = useQuery({
    queryKey: ['queue-config'],
    queryFn: async () => {
      const response = await api.get('/system/queue/config');
      return response.data;
    },
    refetchInterval: 30000 // Refresh every 30 seconds
  });

  const { data: stats } = useQuery({
    queryKey: ['queue-stats'],
    queryFn: async () => {
      const response = await api.get('/system/queue/stats');
      return response.data;
    },
    refetchInterval: 5000, // Refresh every 5 seconds for real-time stats
    enabled: config?.strategy === 'IN_MEMORY' // Only fetch stats for IN_MEMORY strategy
  });

  if (isLoading) {
    return <LoadingPage message="Loading queue configuration..." />;
  }

  if (error) {
    return (
      <div style={{ color: 'red', padding: '1rem' }}>
        Error loading queue configuration: {error.message}
      </div>
    );
  }

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
    fontSize: '0.875rem'
  };

  const valueStyle = {
    fontSize: '1rem',
    color: '#212529',
    padding: '0.5rem',
    backgroundColor: '#f8f9fa',
    borderRadius: '4px',
    border: '1px solid #dee2e6'
  };

  const infoBoxStyle = {
    backgroundColor: '#e7f3ff',
    border: '1px solid #b3d9ff',
    borderRadius: '4px',
    padding: '1rem',
    marginBottom: '1.5rem'
  };

  const badgeStyle = (isActive) => ({
    display: 'inline-block',
    padding: '0.25rem 0.75rem',
    borderRadius: '12px',
    fontSize: '0.875rem',
    fontWeight: '600',
    backgroundColor: isActive ? '#28a745' : '#6c757d',
    color: 'white'
  });

  return (
    <div>
      <h2 style={{ marginBottom: '1.5rem' }}>SendOrder Queue Configuration</h2>

      <div style={infoBoxStyle}>
        <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.5rem' }}>
          <span style={{ fontSize: '1.25rem' }}>ℹ️</span>
          <strong style={{ fontSize: '0.9rem' }}>Configuration Source</strong>
        </div>
        <p style={{ margin: 0, fontSize: '0.875rem', color: '#495057' }}>
          These settings are configured in the <code>as2.properties</code> file and cannot be changed via the Web UI.
          To modify queue settings, edit the configuration file and restart the server.
        </p>
      </div>

      <div style={cardStyle}>
        <h3 style={{ marginTop: 0, marginBottom: '1.5rem', color: '#495057' }}>Current Configuration</h3>

        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))', gap: '1.5rem' }}>
          <div>
            <div style={labelStyle}>Queue Strategy</div>
            <div style={valueStyle}>
              <span style={badgeStyle(config.strategy === 'IN_MEMORY')}>
                {config.strategy}
              </span>
              {config.strategy === 'PERSISTENT' && (
                <div style={{ fontSize: '0.75rem', color: '#6c757d', marginTop: '0.5rem' }}>
                  Messages stored in database
                </div>
              )}
              {config.strategy === 'IN_MEMORY' && (
                <div style={{ fontSize: '0.75rem', color: '#6c757d', marginTop: '0.5rem' }}>
                  Lightweight in-memory queue with checkpointing
                </div>
              )}
            </div>
          </div>

          {config.strategy === 'IN_MEMORY' && (
            <>
              <div>
                <div style={labelStyle}>Max Queue Depth</div>
                <div style={valueStyle}>
                  {config.maxDepth.toLocaleString()} messages
                  <div style={{ fontSize: '0.75rem', color: '#6c757d', marginTop: '0.5rem' }}>
                    Maximum pending messages before backpressure
                  </div>
                </div>
              </div>

              <div>
                <div style={labelStyle}>Checkpoint Interval</div>
                <div style={valueStyle}>
                  {config.checkpointInterval} seconds
                  <div style={{ fontSize: '0.75rem', color: '#6c757d', marginTop: '0.5rem' }}>
                    Crash recovery checkpoint frequency
                  </div>
                </div>
              </div>
            </>
          )}
        </div>
      </div>

      {config.strategy === 'IN_MEMORY' && stats && (
        <div style={cardStyle}>
          <h3 style={{ marginTop: 0, marginBottom: '1.5rem', color: '#495057' }}>
            Queue Status
            <span style={{ fontSize: '0.75rem', color: '#6c757d', fontWeight: 'normal', marginLeft: '0.5rem' }}>
              (refreshes every 5 seconds)
            </span>
          </h3>

          {stats.message && !stats.totalCount && (
            <p style={{ color: '#6c757d', fontSize: '0.875rem' }}>{stats.message}</p>
          )}

          {stats.totalCount !== undefined && (
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(200px, 1fr))', gap: '1.5rem' }}>
              <div>
                <div style={labelStyle}>Waiting</div>
                <div style={{ ...valueStyle, fontSize: '1.5rem', fontWeight: '600', color: '#007bff' }}>
                  {stats.waitingCount || 0}
                </div>
              </div>

              <div>
                <div style={labelStyle}>Processing</div>
                <div style={{ ...valueStyle, fontSize: '1.5rem', fontWeight: '600', color: '#ffc107' }}>
                  {stats.processingCount || 0}
                </div>
              </div>

              <div>
                <div style={labelStyle}>Total</div>
                <div style={{ ...valueStyle, fontSize: '1.5rem', fontWeight: '600', color: '#28a745' }}>
                  {stats.totalCount || 0}
                </div>
              </div>
            </div>
          )}
        </div>
      )}

      {config.strategy === 'PERSISTENT' && (
        <div style={cardStyle}>
          <h3 style={{ marginTop: 0, marginBottom: '1rem', color: '#495057' }}>Strategy Details</h3>
          <p style={{ fontSize: '0.875rem', color: '#495057', lineHeight: '1.6', marginBottom: '0.5rem' }}>
            <strong>PERSISTENT</strong> strategy stores complete AS2 messages in the database before sending.
            This provides maximum durability but uses more memory and storage.
          </p>
          <p style={{ fontSize: '0.875rem', color: '#495057', lineHeight: '1.6', margin: 0 }}>
            Consider switching to <strong>IN_MEMORY</strong> strategy for:
          </p>
          <ul style={{ fontSize: '0.875rem', color: '#495057', lineHeight: '1.6', marginTop: '0.5rem' }}>
            <li>99% memory reduction (1KB vs 100KB per message)</li>
            <li>Faster message processing</li>
            <li>Crash recovery via periodic checkpointing</li>
          </ul>
        </div>
      )}

      <div style={{ marginTop: '2rem', padding: '1rem', backgroundColor: '#fff3cd', border: '1px solid #ffc107', borderRadius: '4px' }}>
        <strong style={{ fontSize: '0.9rem' }}>Configuration File Location:</strong>
        <code style={{ display: 'block', marginTop: '0.5rem', padding: '0.5rem', backgroundColor: 'white', borderRadius: '4px', fontSize: '0.875rem' }}>
          config/as2.properties
        </code>
        <div style={{ marginTop: '0.75rem', fontSize: '0.875rem', color: '#856404' }}>
          <strong>Example configuration:</strong>
          <pre style={{ marginTop: '0.5rem', padding: '0.75rem', backgroundColor: 'white', borderRadius: '4px', fontSize: '0.8rem', overflow: 'auto' }}>
{`sendorder.queue.strategy=IN_MEMORY
sendorder.queue.max_depth=1000
sendorder.queue.checkpoint_interval=60`}
          </pre>
        </div>
      </div>
    </div>
  );
}
