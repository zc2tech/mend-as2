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
import { useBlockLog } from './useIPWhitelist';
import { LoadingPage } from '../../components/Loading';

export default function BlockLogTab() {
  const [targetTypeFilter, setTargetTypeFilter] = useState(null);
  const [days, setDays] = useState(7);
  const { data: logs, isLoading, error } = useBlockLog(targetTypeFilter, days);

  if (isLoading) {
    return <LoadingPage message="Loading block log..." />;
  }

  if (error) {
    return <div style={{ color: 'red' }}>Error loading block log: {error.message}</div>;
  }

  const headerStyle = {
    marginBottom: '1.5rem'
  };

  const filtersStyle = {
    display: 'flex',
    gap: '1rem',
    marginBottom: '1.5rem',
    alignItems: 'center'
  };

  const selectStyle = {
    padding: '0.5rem',
    borderRadius: '4px',
    border: '1px solid #ccc',
    fontSize: '0.9rem'
  };

  const tableStyle = {
    width: '100%',
    borderCollapse: 'collapse',
    backgroundColor: 'white',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    borderRadius: '8px',
    overflow: 'hidden'
  };

  const thStyle = {
    textAlign: 'left',
    padding: '1rem',
    backgroundColor: '#f8f9fa',
    borderBottom: '2px solid #dee2e6',
    fontWeight: '600',
    fontSize: '0.9rem'
  };

  const tdStyle = {
    padding: '0.75rem 1rem',
    borderBottom: '1px solid #dee2e6',
    fontSize: '0.9rem'
  };

  return (
    <div>
      <div style={headerStyle}>
        <h2 style={{ fontSize: '1.25rem', marginBottom: '0.5rem' }}>Block Log</h2>
        <p style={{ color: '#666', fontSize: '0.9rem' }}>
          View audit log of blocked IP attempts. Shows most recent 1000 entries within the selected time range.
        </p>
      </div>

      <div style={filtersStyle}>
        <div>
          <label style={{ marginRight: '0.5rem', fontSize: '0.9rem' }}>Target Type:</label>
          <select
            value={targetTypeFilter || ''}
            onChange={(e) => setTargetTypeFilter(e.target.value || null)}
            style={selectStyle}
          >
            <option value="">All</option>
            <option value="AS2">AS2</option>
            <option value="TRACKER">TRACKER</option>
            <option value="WEBUI">WEBUI</option>
            <option value="API">API</option>
          </select>
        </div>
        <div>
          <label style={{ marginRight: '0.5rem', fontSize: '0.9rem' }}>Last:</label>
          <select
            value={days}
            onChange={(e) => setDays(parseInt(e.target.value))}
            style={selectStyle}
          >
            <option value="1">1 day</option>
            <option value="7">7 days</option>
            <option value="14">14 days</option>
            <option value="30">30 days</option>
            <option value="90">90 days</option>
          </select>
        </div>
        <div style={{ marginLeft: 'auto', color: '#666', fontSize: '0.9rem' }}>
          {logs?.length || 0} entries found
        </div>
      </div>

      {logs && logs.length === 0 ? (
        <div style={{ textAlign: 'center', padding: '2rem', color: '#666' }}>
          No blocked attempts found in the selected time range.
        </div>
      ) : (
        <table style={tableStyle}>
          <thead>
            <tr>
              <th style={thStyle}>Timestamp</th>
              <th style={thStyle}>Blocked IP</th>
              <th style={thStyle}>Target Type</th>
              <th style={thStyle}>Attempted User</th>
              <th style={thStyle}>Attempted Partner</th>
              <th style={thStyle}>Request Path</th>
              <th style={thStyle}>User Agent</th>
            </tr>
          </thead>
          <tbody>
            {logs?.map((log) => (
              <tr key={log.id}>
                <td style={tdStyle}>
                  {new Date(log.blockTime).toLocaleString()}
                </td>
                <td style={tdStyle}>
                  <code>{log.blockedIp}</code>
                </td>
                <td style={tdStyle}>
                  <span style={{
                    padding: '0.25rem 0.5rem',
                    backgroundColor: '#fff3cd',
                    borderRadius: '4px',
                    fontSize: '0.85rem'
                  }}>
                    {log.targetType}
                  </span>
                </td>
                <td style={tdStyle}>{log.attemptedUser || '-'}</td>
                <td style={tdStyle}>{log.attemptedPartner || '-'}</td>
                <td style={tdStyle}>
                  <code style={{ fontSize: '0.85rem' }}>{log.requestPath || '-'}</code>
                </td>
                <td style={tdStyle}>
                  <div style={{ maxWidth: '200px', overflow: 'hidden', textOverflow: 'ellipsis', fontSize: '0.85rem' }}>
                    {log.userAgent || '-'}
                  </div>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}
