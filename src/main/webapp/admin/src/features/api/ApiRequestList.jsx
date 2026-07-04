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
import ApiRequestDetails from './ApiRequestDetails';
import { useAuth } from '../auth/useAuth';
import api from '../../api/client';

export default function ApiRequestList() {
  const { user, hasPermission } = useAuth();
  const [downloading, setDownloading] = useState({});
  const [apiEndpointUrls, setApiEndpointUrls] = useState([]);
  const queryClient = useQueryClient();

  // Check if user has admin-like permissions (USER_MANAGE means they can see all users)
  const isAdmin = hasPermission('USER_MANAGE');

  // Get API config and build endpoint URLs
  useEffect(() => {
    const getApiConfig = async () => {
      try {
        const apiConfig = await api.get('/system/api/config');

        // Build API endpoint URLs using server configuration
        const host = window.location.hostname;
        const username = user?.username || 'your-username';
        const urls = [];

        // Add HTTPS URL if configured
        if (apiConfig.data.httpsPort) {
          urls.push({
            protocol: 'https',
            port: apiConfig.data.httpsPort,
            url: `https://${host}:${apiConfig.data.httpsPort}/as2/userapi/${username}`
          });
        }

        // Add HTTP URL if configured
        // if (apiConfig.data.httpPort) {
        //   urls.push({
        //     protocol: 'http',
        //     port: apiConfig.data.httpPort,
        //     url: `http://${host}:${apiConfig.data.httpPort}/as2/userapi/${username}`
        //   });
        // }

        setApiEndpointUrls(urls);
      } catch (error) {
        // Fallback: use current window location port
        console.warn('Failed to load API config, using fallback port from window.location', error);
        const host = window.location.hostname;
        const port = window.location.port || (window.location.protocol === 'https:' ? '443' : '80');
        const username = user?.username || 'your-username';

        setApiEndpointUrls([{
          protocol: window.location.protocol.replace(':', ''),
          port: parseInt(port),
          url: `${window.location.protocol}//${host}:${port}/as2/userapi/${username}`
        }]);
      }
    };
    getApiConfig();
  }, [user]);

  const defaultFilters = {
    startDate: format(new Date(Date.now() - 7 * 24 * 60 * 60 * 1000), 'yyyy-MM-dd'),
    endDate: format(new Date(), 'yyyy-MM-dd'),
    method: 'ALL',
    path: '',
    limit: 20
  };

  const [filters, setFilters] = useState(defaultFilters);
  const [queryFilters, setQueryFilters] = useState(defaultFilters);
  const [selectedMessage, setSelectedMessage] = useState(null);
  const searchTimeoutRef = useRef(null);

  const { data, isLoading, error, refetch } = useQuery({
    queryKey: ['apiRequests', queryFilters],
    queryFn: async () => {
      const params = new URLSearchParams();
      params.append('startDate', queryFilters.startDate);
      params.append('endDate', queryFilters.endDate);
      if (queryFilters.method && queryFilters.method !== 'ALL') {
        params.append('method', queryFilters.method);
      }
      if (queryFilters.path) {
        params.append('path', queryFilters.path);
      }
      if (queryFilters.limit) {
        params.append('limit', queryFilters.limit);
      }

      const response = await api.get('/user/api-requests?' + params.toString());
      return response.data;
    }
  });

  // Apply search immediately for non-text filters
  const applyFiltersImmediately = (newFilters) => {
    setFilters(newFilters);
    setQueryFilters(newFilters);
  };

  // Debounced search for text inputs (requestId, user)
  const applyFiltersDebounced = (newFilters) => {
    setFilters(newFilters);

    // Clear existing timeout
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }

    // Set new timeout - apply search after 2 seconds of no input
    searchTimeoutRef.current = setTimeout(() => {
      setQueryFilters(newFilters);
    }, 2000);
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
    queryClient.invalidateQueries(['apiRequests']);
  };

  const handleResetFilters = () => {
    setFilters(defaultFilters);
    setQueryFilters(defaultFilters);
    // Clear any pending debounced search
    if (searchTimeoutRef.current) {
      clearTimeout(searchTimeoutRef.current);
    }
    // Invalidate the query cache to force a fresh fetch from the database
    queryClient.invalidateQueries(['apiRequests']);
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
  // Only show to ADMIN users who can see all messages
  const showUserFilter = isAdmin;

  const getTimezoneOffset = () => {
    const offset = -new Date().getTimezoneOffset();
    const hours = Math.floor(Math.abs(offset) / 60);
    const minutes = Math.abs(offset) % 60;
    const sign = offset >= 0 ? '+' : '-';
    return `UTC${sign}${hours}${minutes > 0 ? ':' + minutes.toString().padStart(2, '0') : ''}`;
  };

  const abbreviateDocType = (docType) => {
    if (!docType) return '-';
    return docType;
  };

  const formatSize = (bytes) => {
    if (!bytes) return '0 K';
    const kb = bytes / 1024;
    // Format with comma as thousands separator
    return kb.toLocaleString('en-US', { maximumFractionDigits: 1 }) + ' K';
  };

  const handleDownloadContent = async (requestId) => {
    setDownloading({ ...downloading, [requestId]: 'content' });
    try {
      // First get the message details to build filename
      const message = messages.find(m => m.requestId === requestId);

      const response = await api.get(`/user/api-requests/${requestId}/body`, {
        responseType: 'blob'
      });

      // Build filename from method, path, timestamp
      let filename = `${requestId}.dat`;
      if (message) {
        const timestamp = message.requestTime ? format(new Date(message.requestTime), 'yyyyMMdd_HHmmss') : 'unknown';
        const timezone = getTimezoneOffset();
        const method = message.httpMethod || 'unknown';
        const path = message.requestPath ? message.requestPath.replace(/\//g, '_').replace(/[^a-zA-Z0-9_-]/g, '') : 'unknown';

        // Determine extension based on content type
        let ext = 'dat';
        const contentType = message.contentType || '';
        if (contentType.includes('json')) {
          ext = 'json';
        } else if (contentType.includes('xml')) {
          ext = 'xml';
        } else if (contentType.includes('text')) {
          ext = 'txt';
        } else if (contentType.includes('form')) {
          ext = 'form';
        }

        filename = `${method}_${path}_${timestamp}_${timezone}.${ext}`;
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
      alert('Failed to download request body: ' + (error.response?.data?.error || error.message));
    } finally {
      setDownloading({ ...downloading, [requestId]: null });
    }
  };

  const handleDownloadPayloads = async (requestId) => {
    setDownloading({ ...downloading, [requestId]: 'payloads' });
    try {
      const response = await api.get(`/api-requests/${requestId}/download-payloads`, {
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
      setDownloading({ ...downloading, [requestId]: null });
    }
  };

  const handleDownloadBruno = async (requestId) => {
    setDownloading({ ...downloading, [requestId]: 'bruno' });
    try {
      const response = await api.get(`/api-requests/${requestId}/download-bruno`, {
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
      setDownloading({ ...downloading, [requestId]: null });
    }
  };

  if (isLoading) {
    return <LoadingPage message="Loading API requests..." />;
  }

  if (error) {
    return <div style={{ color: 'red' }}>Error loading API requests: {error.message}</div>;
  }

  const messages = data?.messages || data || [];
  const totalCount = data?.totalCount || messages.length;
  const returnedCount = data?.returnedCount || messages.length;

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
        <h1 style={{ margin: 0 }}>REST API Requests</h1>
        <p style={{ color: '#666', margin: '0.5rem 0 0 0' }}>
          Showing {returnedCount} of {totalCount} requests
        </p>
      </div>

      {/* Base URL Info Box */}
      {apiEndpointUrls.length > 0 && (
        <div style={{
          marginBottom: '1rem',
          padding: '0.75rem 1rem',
          backgroundColor: '#e7f3ff',
          border: '1px solid #b3d9ff',
          borderRadius: '8px'
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem', flexWrap: 'wrap' }}>
            <strong style={{ fontSize: '0.875rem', color: '#0056b3', marginRight: '0.5rem' }}>
              Base URL:
            </strong>
            {apiEndpointUrls.map((endpoint, index) => (
              <div key={index} style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                <code style={{
                  fontSize: '0.875rem',
                  padding: '0.375rem 0.5rem',
                  backgroundColor: 'white',
                  border: '1px solid #b3d9ff',
                  borderRadius: '4px',
                  display: 'inline-block',
                  maxWidth: 'fit-content'
                }}>
                  {endpoint.url}
                </code>
                <button
                  onClick={() => {
                    navigator.clipboard.writeText(endpoint.url);
                    alert('Base URL copied to clipboard!');
                  }}
                  style={{
                    padding: '0.375rem 0.75rem',
                    backgroundColor: '#007bff',
                    color: 'white',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer',
                    fontSize: '0.8rem',
                    fontWeight: '600',
                    whiteSpace: 'nowrap'
                  }}
                >
                  📋 Copy
                </button>
              </div>
            ))}
          </div>
        </div>
      )}

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

        {/* Row 1: Date range, Method, Path, Limit */}
        <div style={{ display: 'grid', gridTemplateColumns: '140px 140px 120px 200px 80px', gap: '1rem', marginBottom: '1rem' }}>
          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
              Start Date
            </label>
            <input
              type="date"
              value={filters.startDate}
              onChange={(e) => applyFiltersImmediately({ ...filters, startDate: e.target.value })}
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
              Method
            </label>
            <select
              value={filters.method}
              onChange={(e) => applyFiltersImmediately({ ...filters, method: e.target.value })}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            >
              <option value="ALL">All</option>
              <option value="GET">GET</option>
              <option value="POST">POST</option>
              <option value="PUT">PUT</option>
              <option value="DELETE">DELETE</option>
            </select>
          </div>

          <div>
            <label style={{ display: 'block', marginBottom: '0.5rem', fontSize: '0.875rem', fontWeight: '600' }}>
              Path
            </label>
            <input
              type="text"
              placeholder="Search by path"
              value={filters.path}
              onChange={(e) => applyFiltersDebounced({ ...filters, path: e.target.value })}
              onKeyPress={handleKeyPress}
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
              Limit
            </label>
            <input
              type="number"
              step="5"
              min="1"
              value={filters.limit || 20}
              onChange={(e) => applyFiltersDebounced({ ...filters, limit: parseInt(e.target.value) })}
              onKeyPress={handleKeyPress}
              style={{
                width: '100%',
                padding: '0.5rem',
                border: '1px solid #ddd',
                borderRadius: '4px'
              }}
            />
          </div>
        </div>
      </div>

      <table style={tableStyle}>
        <thead>
          <tr>
            <th style={thStyle}>Method</th>
            <th style={thStyle}>Path</th>
            <th style={thStyle}>Timestamp ({getTimezoneOffset()})</th>
            <th style={thStyle}>Remote IP</th>
            <th style={thStyle}>User Agent</th>
            <th style={thStyle}>Size</th>
            <th style={thStyle}>Status</th>
            <th style={thStyle}>DL</th>
            <th style={thStyle}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {messages.length === 0 ? (
            <tr>
              <td colSpan="9" style={{ ...tdStyle, textAlign: 'center', padding: '2rem' }}>
                No API requests found
              </td>
            </tr>
          ) : (
            messages.map(message => (
              <tr key={message.requestId}>
                <td style={tdStyle}>
                  <span style={{
                    padding: '0.25rem 0.5rem',
                    backgroundColor:
                      message.httpMethod === 'GET' ? '#28a745' :
                      message.httpMethod === 'POST' ? '#007bff' :
                      message.httpMethod === 'PUT' ? '#ffc107' :
                      message.httpMethod === 'DELETE' ? '#dc3545' : '#6c757d',
                    color: 'white',
                    borderRadius: '4px',
                    fontSize: '0.75rem',
                    fontWeight: '600'
                  }}>
                    {message.httpMethod}
                  </span>
                </td>
                <td style={tdStyle}>
                  <code style={{ fontSize: '0.75rem' }}>{message.requestPath || '/'}</code>
                </td>
                <td style={tdStyle}>
                  {message.requestTime ? format(new Date(message.requestTime), 'yyyy-MM-dd HH:mm:ss') : '-'}
                </td>
                <td style={tdStyle}>{message.remoteAddr || '-'}</td>
                <td style={tdStyle} title={message.userAgent}>
                  {message.userAgent ?
                    (message.userAgent.length > 30 ? message.userAgent.substring(0, 30) + '...' : message.userAgent)
                    : '-'}
                </td>
                <td style={tdStyle}>{formatSize(message.contentSize)}</td>
                <td style={tdStyle}>
                  <span style={{
                    padding: '0.25rem 0.5rem',
                    borderRadius: '4px',
                    backgroundColor: message.responseStatus >= 200 && message.responseStatus < 300 ? '#28a74520' : '#dc354520',
                    color: message.responseStatus >= 200 && message.responseStatus < 300 ? '#28a745' : '#dc3545',
                    fontWeight: '600',
                    fontSize: '0.75rem'
                  }}>
                    {message.responseStatus}
                  </span>
                </td>
                <td style={{ ...tdStyle, textAlign: 'center' }}>
                  <button
                    onClick={() => handleDownloadContent(message.requestId)}
                    disabled={downloading[message.requestId] === 'content'}
                    title="Download request body"
                    style={{
                      padding: '0.25rem 0.5rem',
                      backgroundColor: downloading[message.requestId] === 'content' ? '#6c757d' : '#007bff',
                      color: 'white',
                      border: 'none',
                      borderRadius: '4px',
                      cursor: downloading[message.requestId] === 'content' ? 'not-allowed' : 'pointer',
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
                    View Details
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {selectedMessage && (
        <ApiRequestDetails
          requestId={selectedMessage.requestId}
          onClose={() => setSelectedMessage(null)}
        />
      )}
    </div>
  );
}
