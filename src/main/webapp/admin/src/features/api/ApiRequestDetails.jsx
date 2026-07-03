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
import { LoadingPage } from '../../components/Loading';
import { format } from 'date-fns';
import api from '../../api/client';
import { useState, useEffect } from 'react';

export default function ApiRequestDetails({ requestId, onClose }) {
  const [downloading, setDownloading] = useState(false);

  // Handle ESC key to close modal
  useEffect(() => {
    const handleEscKey = (event) => {
      if (event.key === 'Escape') {
        onClose();
      }
    };

    document.addEventListener('keydown', handleEscKey);
    return () => {
      document.removeEventListener('keydown', handleEscKey);
    };
  }, [onClose]);
  const overlayStyle = {
    position: 'fixed',
    top: 0,
    left: 0,
    right: 0,
    bottom: 0,
    backgroundColor: 'rgba(0, 0, 0, 0.5)',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    zIndex: 1000
  };

  const modalStyle = {
    backgroundColor: 'white',
    borderRadius: '8px',
    padding: '2rem',
    maxWidth: '1125px',
    maxHeight: '90vh',
    overflow: 'auto',
    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.3)'
  };

  const closeButtonStyle = {
    marginTop: '1.5rem',
    padding: '0.5rem 1rem',
    backgroundColor: '#6c757d',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.875rem'
  };

  const downloadButtonStyle = {
    padding: '0.5rem 1rem',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.875rem',
    marginRight: '0.5rem'
  };

  const disabledButtonStyle = {
    ...downloadButtonStyle,
    backgroundColor: '#6c757d',
    cursor: 'not-allowed',
    opacity: 0.6
  };

  const sectionStyle = {
    marginBottom: '1.5rem',
    paddingBottom: '1rem',
    borderBottom: '1px solid #dee2e6'
  };

  const labelStyle = {
    fontWeight: '600',
    marginRight: '0.5rem',
    color: '#495057'
  };

  const { data: message, isLoading, error } = useQuery({
    queryKey: ['trackerMessage', requestId],
    queryFn: async () => {
      const response = await api.get(`/user/api-requests/${requestId}`);
      return response.data;
    }
  });

  const handleDownloadContent = async () => {
    setDownloading(true);
    try {
      const response = await api.get(`/user/api-requests/${requestId}/body`, {
        responseType: 'blob'
      });

      // Extract filename from Content-Disposition header
      const contentDisposition = response.headers['content-disposition'];
      let filename = 'request_body.bin';
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="(.+)"/);
        if (filenameMatch) {
          filename = filenameMatch[1];
        }
      }

      // Create download link
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
      setDownloading(false);
    }
  };

  const handleDownloadPayloads = async () => {
    setDownloading(true);
    try {
      const response = await api.get(`/api-requests/${requestId}/download-payloads`, {
        responseType: 'blob'
      });

      // Extract filename from Content-Disposition header
      const contentDisposition = response.headers['content-disposition'];
      let filename = 'payloads.zip';
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="(.+)"/);
        if (filenameMatch) {
          filename = filenameMatch[1];
        }
      }

      // Create download link
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
      setDownloading(false);
    }
  };

  const handleDownloadBruno = async () => {
    setDownloading(true);
    try {
      const response = await api.get(`/api-requests/${requestId}/download-bruno`, {
        responseType: 'blob'
      });

      // Extract filename from Content-Disposition header
      const contentDisposition = response.headers['content-disposition'];
      let filename = 'bruno_collection.zip';
      if (contentDisposition) {
        const filenameMatch = contentDisposition.match(/filename="(.+)"/);
        if (filenameMatch) {
          filename = filenameMatch[1];
        }
      }

      // Create download link
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
      setDownloading(false);
    }
  };

  if (isLoading) {
    return (
      <div style={overlayStyle}>
        <div style={modalStyle}>
          <LoadingPage message="Loading message details..." />
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div style={overlayStyle}>
        <div style={modalStyle}>
          <div style={{ color: 'red' }}>Error loading message details: {error.message}</div>
          <button onClick={onClose} style={closeButtonStyle}>Close</button>
        </div>
      </div>
    );
  }

  return (
    <div style={overlayStyle} onClick={onClose}>
      <div style={modalStyle} onClick={(e) => e.stopPropagation()}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <h2 style={{ margin: 0 }}>API Request Details</h2>
          <div>
            <button
              onClick={handleDownloadContent}
              disabled={downloading}
              style={downloading ? disabledButtonStyle : downloadButtonStyle}
              title="Download request body"
            >
              {downloading ? 'Downloading...' : '⬇ Download Body'}
            </button>
          </div>
        </div>

        <div style={sectionStyle}>
          <h3>Basic Information</h3>
          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '0.75rem' }}>
            <div>
              <span style={labelStyle}>Timestamp:</span>
              {message.requestTime ? format(new Date(message.requestTime), 'yyyy-MM-dd HH:mm:ss') : '-'}
            </div>
            <div>
              <span style={labelStyle}>HTTP Method:</span>
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
            </div>
            <div>
              <span style={labelStyle}>Request Path:</span>
              <code>{message.requestPath || '/'}</code>
            </div>
            <div>
              <span style={labelStyle}>Response Status:</span>
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
            </div>
            <div>
              <span style={labelStyle}>Remote IP:</span>
              {message.remoteAddr || '-'}
            </div>
            <div>
              <span style={labelStyle}>Content Size:</span>
              {message.contentSize ? `${(message.contentSize / 1024).toFixed(2)} KB` : '0 KB'}
            </div>
          </div>
        </div>

        <div style={sectionStyle}>
          <h3>User Agent</h3>
          <pre style={{
            backgroundColor: '#f8f9fa',
            padding: '0.75rem',
            borderRadius: '4px',
            overflow: 'auto',
            fontSize: '0.875rem'
          }}>
            {message.userAgent || '-'}
          </pre>
        </div>

        <div style={sectionStyle}>
          <h3>Content Type</h3>
          <pre style={{
            backgroundColor: '#f8f9fa',
            padding: '0.75rem',
            borderRadius: '4px',
            overflow: 'auto',
            fontSize: '0.875rem'
          }}>
            {message.contentType || '-'}
          </pre>
        </div>

        {message.requestHeaders && (
          <div style={sectionStyle}>
            <h3>Request Headers</h3>
            <div style={{
              backgroundColor: '#f8f9fa',
              padding: '0.75rem',
              borderRadius: '4px',
              overflow: 'auto',
              fontSize: '0.875rem'
            }}>
              {(() => {
                try {
                  // Try to parse as JSON first
                  const headers = JSON.parse(message.requestHeaders);
                  return Object.entries(headers).map(([key, value], index) => (
                    <div key={index} style={{ marginBottom: '0.25rem' }}>
                      {key}: {String(value)}
                    </div>
                  ));
                } catch (e) {
                  // Fallback to plain text if not JSON
                  return message.requestHeaders.split('\n').filter(line => line.trim()).map((line, index) => (
                    <div key={index} style={{ marginBottom: '0.25rem' }}>
                      {line.trim()}
                    </div>
                  ));
                }
              })()}
            </div>
          </div>
        )}

        {message.contentPreview && (
          <div style={sectionStyle}>
            <h3>Content Preview (first 2000 bytes)</h3>
            <pre style={{
              backgroundColor: '#f8f9fa',
              padding: '0.75rem',
              borderRadius: '4px',
              overflow: 'auto',
              fontSize: '0.875rem',
              whiteSpace: 'pre-wrap'
            }}>
              {message.contentPreview}
            </pre>
          </div>
        )}

        <button onClick={onClose} style={closeButtonStyle}>Close</button>
      </div>
    </div>
  );
}
