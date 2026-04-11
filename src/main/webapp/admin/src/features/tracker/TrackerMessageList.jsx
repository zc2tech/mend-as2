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

import { useState, useEffect, useRef } from 'react';
import { useQuery, useQueryClient } from '@tanstack/react-query';
import { LoadingPage } from '../../components/Loading';
import { format } from 'date-fns';
import TrackerMessageDetails from './TrackerMessageDetails';
import { useAuth } from '../auth/useAuth';
import api from '../../api/client';

export default function TrackerMessageList() {
  const { user, hasPermission } = useAuth();
  const [trackerAuthRequired, setTrackerAuthRequired] = useState(false);
  const [downloading, setDownloading] = useState({});
  const queryClient = useQueryClient();

  // Check if user has admin-like permissions (USER_MANAGE means they can see all users)
  const isAdmin = hasPermission('USER_MANAGE');

  // Get tracker auth setting - now all authenticated users can access this
  useEffect(() => {
    const getTrackerAuth = async () => {
      try {
        const trackerConfig = await api.get('/system/tracker/config');
        setTrackerAuthRequired(trackerConfig.data.authRequired);
      } catch (error) {
        // Silently fail - default to false (show user filter)
      }
    };
    getTrackerAuth();
  }, []);
  const defaultFilters = {
    startDate: format(new Date(Date.now() - 7 * 24 * 60 * 60 * 1000), 'yyyy-MM-dd'),
    endDate: format(new Date(), 'yyyy-MM-dd'),
    trackerId: '',
    user: '',
    format: '',
    authNone: true,
    authSuccess: true
  };

  const [filters, setFilters] = useState(defaultFilters);
  const [queryFilters, setQueryFilters] = useState(defaultFilters);
  const [selectedMessage, setSelectedMessage] = useState(null);
  const searchTimeoutRef = useRef(null);

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['trackerMessages', queryFilters],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (queryFilters.trackerId) {
        params.append('trackerId', queryFilters.trackerId);
      } else {
        params.append('startDate', queryFilters.startDate);
        params.append('endDate', queryFilters.endDate);
      }
      if (queryFilters.user) params.append('user', queryFilters.user);
      if (queryFilters.format) params.append('format', queryFilters.format);
      params.append('authNone', queryFilters.authNone);
      params.append('authSuccess', queryFilters.authSuccess);

      const response = await api.get('/tracker-messages?' + params.toString());
      return response.data;
    }
  });

  // Apply search immediately for non-text filters
  const applyFiltersImmediately = (newFilters) => {
    setFilters(newFilters);
    setQueryFilters(newFilters);
  };

  // Debounced search for text inputs (trackerId, user)
  const applyFiltersDebounced = (newFilters) => {
    setFilters(newFilters);

    // Clear existing timeout
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }

    // Set new timeout - apply search after 1 second of no input
    searchTimeoutRef.current = setTimeout(() => {
      setQueryFilters(newFilters);
    }, 1000);
  };

  // Cleanup timeout on unmount
  useEffect(() => {
    return () => {
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
    };
  }, []);

  const handleSearch = () => {
    setQueryFilters({ ...filters });
    // Invalidate the query cache to force a fresh fetch from the database
    queryClient.invalidateQueries(['tracker-messages']);
  };

  const handleResetFilters = () => {
    setFilters(defaultFilters);
    setQueryFilters(defaultFilters);
    // Clear any pending debounced search
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }
    // Invalidate the query cache to force a fresh fetch from the database
    queryClient.invalidateQueries(['tracker-messages']);
  };

  const handleKeyPress = (e) => {
    if (e.key === 'Enter') {
      // Clear timeout and apply immediately on Enter
      if (searchTimeoutRef.current) {
        clearTimeout(searchTimeoutRef.current);
      }
      handleSearch();
    }
  };

  // Determine if User filter/column should be shown
  // Hide if auth is required AND user is not admin
  const showUserFilter = !trackerAuthRequired || isAdmin;

  const getTimezoneOffset = () => {
    const offset = -new Date().getTimezoneOffset();
    const hours = Math.floor(Math.abs(offset) / 60);
    const minutes = Math.abs(offset) % 60;
    const sign = offset >= 0 ? '+' : '-';
    return `UTC${sign}${hours}${minutes > 0 ? ':' + minutes.toString().padStart(2, '0') : ''}`;
  };

  const abbreviateDocType = (docType) => {
    if (!docType) return '-';

    // Extract code from parentheses if present
    const match = docType.match(/\(([^)]+)\)/);
    if (match) return match[1];

    // Abbreviate cXML types
    if (docType === 'Purchase Order') return 'PO';
    if (docType === 'Invoice') return 'INV';
    if (docType === 'Advanced Ship Notice') return 'ASN';
    if (docType === 'Order Confirmation') return 'OC';
    if (docType === 'Payment Remittance') return 'PR';

    // Truncate if too long
    if (docType.length > 15) return docType.substring(0, 15);

    return docType;
  };

  const handleDownloadContent = async (trackerId) => {
    setDownloading({ ...downloading, [trackerId]: 'content' });
    try {
      const response = await api.get(`/tracker-messages/${trackerId}/download`, {
        responseType: 'blob'
      });

      const contentDisposition = response.headers['content-disposition'];
      let filename = 'tracker_message.msg';
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="(.+)"/);
        if (filenameMatch) {
          filename = filenameMatch[1];
        }
      }

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      alert('Failed to download message content: ' + (error.response?.data?.error || error.message));
    } finally {
      setDownloading({ ...downloading, [trackerId]: null });
    }
  };

  const handleDownloadPayloads = async (trackerId) => {
    setDownloading({ ...downloading, [trackerId]: 'payloads' });
    try {
      const response = await api.get(`/tracker-messages/${trackerId}/download-payloads`, {
        responseType: 'blob'
      });

      const contentDisposition = response.headers['content-disposition'];
      let filename = 'payloads.zip';
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="(.+)"/);
        if (filenameMatch) {
          filename = filenameMatch[1];
        }
      }

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      alert('Failed to download payloads: ' + (error.response?.data?.error || error.message));
    } finally {
      setDownloading({ ...downloading, [trackerId]: null });
    }
  };

  const handleDownloadBruno = async (trackerId) => {
    setDownloading({ ...downloading, [trackerId]: 'bruno' });
    try {
      const response = await api.get(`/tracker-messages/${trackerId}/download-bruno`, {
        responseType: 'blob'
      });

      const contentDisposition = response.headers['content-disposition'];
      let filename = 'bruno_collection.zip';
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="(.+)"/);
        if (filenameMatch) {
          filename = filenameMatch[1];
        }
      }

      const url = window.URL.createObjectURL(new Blob([response.data]));
      const link = document.createElement('a');
      link.href = url;
      link.setAttribute('download', filename);
      document.body.appendChild(link);
      link.click();
      link.remove();
      window.URL.revokeObjectURL(url);
    } catch (error) {
      alert('Failed to download Bruno collection: ' + (error.response?.data?.error || error.message));
    } finally {
      setDownloading({ ...downloading, [trackerId]: null });
    }
  };

  if (isLoading) {
    return <LoadingPage message="Loading tracker messages..." />;
  }

  if (error) {
    return <div style={{ color: 'red' }}>Error loading tracker messages: {error.message}</div>;
  }

  const messages = data || [];

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
    padding: '0.75rem 1rem',
    backgroundColor: '#f8f9fa',
    borderBottom: '2px solid #dee2e6',
    fontWeight: '600',
    fontSize: '0.875rem'
  };

  const tdStyle = {
    padding: '0.5rem 1rem',
    borderBottom: '1px solid #dee2e6',
    fontSize: '0.875rem'
  };

  return (
    <div>
      <div style={{ marginBottom: '1.5rem' }}>
        <h1 style={{ margin: 0 }}>Tracker Messages</h1>
        <p style={{ color: '#666', margin: '0.5rem 0 0 0' }}>
          Showing {messages.length} messages
        </p>
      </div>

      <div style={{ marginBottom: '1rem', padding: '1rem', backgroundColor: 'white', borderRadius: '8px' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h3 style={{ margin: 0, fontSize: '1rem', fontWeight: '600' }}>Filters</h3>
          <div style={{ display: 'flex', gap: '0.5rem' }}>
            <button
              style={{
                padding: '0.375rem 0.75rem',
                backgroundColor: '#007bff',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '0.875rem'
              }}
              onClick={handleSearch}
            >
              Search
            </button>
            <button
              style={{
                padding: '0.375rem 0.75rem',
                backgroundColor: '#6c757d',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer',
                fontSize: '0.875rem'
              }}
              onClick={handleResetFilters}
            >
              Reset Filters
            </button>
          </div>
        </div>

        {/* Row 1: Date range, Tracker ID, User, Format */}
        <div style={{ display: 'grid', gridTemplateColumns: showUserFilter ? '140px 140px 200px 80px 135px' : '140px 140px 200px 135px', gap: '1rem', marginBottom: '1rem' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
              Start Date
            </label>
            <input
              type="date"
              value={filters.startDate}
              onChange={(e) => applyFiltersImmediately({ ...filters, startDate: e.target.value })}
              disabled={!!filters.trackerId}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            />
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
              End Date
            </label>
            <input
              type="date"
              value={filters.endDate}
              onChange={(e) => applyFiltersImmediately({ ...filters, endDate: e.target.value })}
              disabled={!!filters.trackerId}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            />
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
              Tracker ID
            </label>
            <input
              type="text"
              placeholder="Search by Tracker ID"
              value={filters.trackerId}
              onChange={(e) => applyFiltersDebounced({ ...filters, trackerId: e.target.value })}
              onKeyPress={handleKeyPress}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            />
          </div>

          {showUserFilter && (
            <div>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
                User
              </label>
              <input
                type="text"
                placeholder="User"
                value={filters.user}
                onChange={(e) => applyFiltersDebounced({ ...filters, user: e.target.value })}
                onKeyPress={handleKeyPress}
                style={{
                  width: '100%',
                  padding: '0.5rem',
                  border: '1px solid #ddd',
                  borderRadius: '4px'
                }}
              />
            </div>
          )}

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
              Format
            </label>
            <select
              value={filters.format}
              onChange={(e) => applyFiltersImmediately({ ...filters, format: e.target.value })}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            >
              <option value="">All</option>
              <option value="cXML">cXML</option>
              <option value="X12">X12</option>
              <option value="EDIFACT">EDIFACT</option>
            </select>
          </div>
        </div>

        {/* Row 2: Auth status toggles */}
        <div style={{ display: 'flex', gap: '2rem', padding: '0.5rem' }}>
          <label style={{ display: 'flex', alignItems: 'center', fontSize: '0.875rem', cursor: 'pointer' }}>
            <input
              type="checkbox"
              checked={filters.authNone}
              onChange={(e) => applyFiltersImmediately({ ...filters, authNone: e.target.checked })}
              style={{ marginRight: '0.5rem' }}
            />
            Show No Auth
          </label>
          <label style={{ display: 'flex', alignItems: 'center', fontSize: '0.875rem', cursor: 'pointer' }}>
            <input
              type="checkbox"
              checked={filters.authSuccess}
              onChange={(e) => applyFiltersImmediately({ ...filters, authSuccess: e.target.checked })}
              style={{ marginRight: '0.5rem' }}
            />
            Show Auth Success
          </label>
        </div>
      </div>

      <table style={tableStyle}>
        <thead>
          <tr>
            <th style={thStyle}>Tracker ID</th>
            <th style={thStyle}>Timestamp ({getTimezoneOffset()})</th>
            <th style={thStyle}>Remote IP</th>
            <th style={thStyle}>User Agent</th>
            <th style={thStyle}>Size</th>
            <th style={thStyle}>Auth Status</th>
            {showUserFilter && <th style={thStyle}>User</th>}
            <th style={thStyle}>Payloads</th>
            <th style={thStyle}>Format</th>
            <th style={thStyle}>Doc Type</th>
            <th style={thStyle}>DL</th>
            <th style={thStyle}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {messages.length === 0 ? (
            <tr>
              <td colSpan={showUserFilter ? "12" : "11"} style={{ ...tdStyle, textAlign: 'center', padding: '2rem' }}>
                No tracker messages found
              </td>
            </tr>
          ) : (
            messages.map(message => (
              <tr key={message.trackerId}>
                <td style={tdStyle}>
                  <code style={{ fontSize: '0.75rem' }}>{message.trackerId}</code>
                </td>
                <td style={tdStyle}>
                  {message.timestamp ? format(new Date(message.timestamp), 'yyyy-MM-dd HH:mm:ss') : '-'}
                </td>
                <td style={tdStyle}>{message.remoteAddr || '-'}</td>
                <td style={tdStyle} title={message.userAgent}>
                  {message.userAgent ?
                    (message.userAgent.length > 30 ? message.userAgent.substring(0, 30) + '...' : message.userAgent)
                    : '-'}
                </td>
                <td style={tdStyle}>{message.contentSize || 0} bytes</td>
                <td style={tdStyle}>
                  <span style={{
                    padding: '0.25rem 0.5rem',
                    borderRadius: '4px',
                    backgroundColor: message.authStatus === 'Success' ? '#28a74520' : '#6c757d20',
                    color: message.authStatus === 'Success' ? '#28a745' : '#6c757d',
                    fontWeight: '600',
                    fontSize: '0.75rem'
                  }}>
                    {message.authStatus || 'None'}
                  </span>
                </td>
                {showUserFilter && <td style={tdStyle}>{message.authUser || '-'}</td>}
                <td style={tdStyle}>{message.payloadCount || 0}</td>
                <td style={tdStyle}>{message.payloadFormat || '-'}</td>
                <td style={tdStyle}>{abbreviateDocType(message.payloadDocType)}</td>
                <td style={{ ...tdStyle, textAlign: 'center', whiteSpace: 'nowrap' }}>
                  <div style={{ display: 'flex', gap: '0.25rem', justifyContent: 'center', alignItems: 'center' }}>
                    <button
                      onClick={() => handleDownloadContent(message.trackerId)}
                      disabled={downloading[message.trackerId] === 'content'}
                      title="Download message content"
                      style={{
                        padding: '0.25rem 0.5rem',
                        backgroundColor: downloading[message.trackerId] === 'content' ? '#6c757d' : '#007bff',
                        color: 'white',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: downloading[message.trackerId] === 'content' ? 'not-allowed' : 'pointer',
                        fontSize: '0.875rem',
                        width: '32px',
                        height: '28px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                      }}
                    >
                      ⬇
                    </button>
                    <button
                      onClick={() => handleDownloadPayloads(message.trackerId)}
                      disabled={downloading[message.trackerId] === 'payloads' || !message.payloadCount || message.payloadCount === 0}
                      title="Download payloads as ZIP"
                      style={{
                        padding: '0.25rem 0.5rem',
                        backgroundColor: (downloading[message.trackerId] === 'payloads' || !message.payloadCount || message.payloadCount === 0) ? '#6c757d' : '#007bff',
                        color: 'white',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: (downloading[message.trackerId] === 'payloads' || !message.payloadCount || message.payloadCount === 0) ? 'not-allowed' : 'pointer',
                        fontSize: '0.875rem',
                        width: '32px',
                        height: '28px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center',
                        opacity: (!message.payloadCount || message.payloadCount === 0) ? 0.5 : 1
                      }}
                    >
                      📦
                    </button>
                    <button
                      onClick={() => handleDownloadBruno(message.trackerId)}
                      disabled={downloading[message.trackerId] === 'bruno'}
                      title="Download Bruno collection"
                      style={{
                        padding: '0.25rem 0.5rem',
                        backgroundColor: downloading[message.trackerId] === 'bruno' ? '#6c757d' : '#007bff',
                        color: 'white',
                        border: 'none',
                        borderRadius: '4px',
                        cursor: downloading[message.trackerId] === 'bruno' ? 'not-allowed' : 'pointer',
                        fontSize: '0.875rem',
                        width: '32px',
                        height: '28px',
                        display: 'flex',
                        alignItems: 'center',
                        justifyContent: 'center'
                      }}
                    >
                      <span style={{ fontSize: '1rem', verticalAlign: 'middle' }}>🐶</span>
                    </button>
                  </div>
                </td>
                <td style={tdStyle}>
                  <button
                    style={{
                      padding: '0.375rem 0.75rem',
                      backgroundColor: '#007bff',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: 'pointer',
                      fontSize: '0.875rem'
                    }}
                    onClick={() => setSelectedMessage(message)}
                  >
                    Details
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {selectedMessage && (
        <TrackerMessageDetails
          trackerId={selectedMessage.trackerId}
          onClose={() => setSelectedMessage(null)}
        />
      )}
    </div>
  );
}
