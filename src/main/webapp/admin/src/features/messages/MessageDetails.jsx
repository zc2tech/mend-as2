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
import { useToast } from '../../components/Toast';
import api from '../../api/client';

export default function MessageDetails({ message, onClose }) {
  const [details, setDetails] = useState([]);
  const [logs, setLogs] = useState([]);
  const [payloads, setPayloads] = useState([]);
  const [rawData, setRawData] = useState('');
  const [rawDataTruncated, setRawDataTruncated] = useState(false);
  const [rawDataFileSize, setRawDataFileSize] = useState(0);
  const [headerData, setHeaderData] = useState('');
  const [activeTab, setActiveTab] = useState('log');
  const [loading, setLoading] = useState(true);
  const [selectedDetailIndex, setSelectedDetailIndex] = useState(0);
  const toast = useToast();

  // Handle ESC key to close dialog
  useEffect(() => {
    const handleEscKey = (event) => {
      if (event.key === 'Escape') {
        onClose();
      }
    };

    document.addEventListener('keydown', handleEscKey);
    return () => document.removeEventListener('keydown', handleEscKey);
  }, [onClose]);

  useEffect(() => {
    const fetchDetails = async () => {
      try {
        setLoading(true);

        // Fetch message details list (message + MDN entries)
        const detailsResponse = await api.get(`/messages/${message.messageId}/details`).catch(e => {
          console.error('Details error:', e);
          return { data: [] };
        });

        console.log('Message details response:', detailsResponse.data);
        setDetails(detailsResponse.data || []);

        // Auto-select first entry if available
        if (detailsResponse.data && detailsResponse.data.length > 0) {
          setSelectedDetailIndex(0);
        }
      } catch (error) {
        console.error('Failed to fetch message details:', error);
        toast.error('Failed to load message details: ' + (error.response?.data?.error || error.message));
      } finally {
        setLoading(false);
      }
    };

    fetchDetails();
  }, [message.messageId, toast]);

  // Fetch data for the selected detail entry (message or MDN)
  useEffect(() => {
    if (details.length === 0 || selectedDetailIndex < 0) return;

    const fetchDetailData = async () => {
      try {
        const selectedDetail = details[selectedDetailIndex];
        if (!selectedDetail) return;

        const selectedMessageId = selectedDetail.messageId;
        const overviewMessageId = message.messageId; // Use the original overview message ID

        console.log('Selected detail:', selectedDetail);
        console.log('Selected message ID:', selectedMessageId);
        console.log('Overview message ID:', overviewMessageId);
        console.log('Is MDN:', selectedDetail.mdn);

        if (!selectedMessageId) {
          console.error('No messageId found in selected detail');
          return;
        }

        // For raw data and header, we need to pass the overview messageId to get all details,
        // then the backend will find the specific entry by selectedMessageId
        // For logs, always use overview messageId since logs are for the entire transaction
        // For payloads, use overview messageId with query param (MDNs don't have payloads)
        const [logsResponse, payloadsResponse, rawDataResponse, headerResponse] = await Promise.all([
          api.get(`/messages/${overviewMessageId}/log`).catch(e => {
            console.error('Log error:', e);
            return { data: [] };
          }),
          // MDNs don't have payloads, only messages do
          selectedDetail.mdn ? Promise.resolve({ data: [] }) : api.get(`/messages/${overviewMessageId}/payloads`, {
            params: { entryMessageId: selectedMessageId }
          }).catch(e => {
            console.error('Payloads error:', e);
            return { data: [] };
          }),
          // Use overview messageId for raw data, with query param for specific entry
          api.get(`/messages/${overviewMessageId}/raw`, {
            params: { entryMessageId: selectedMessageId }
          }).catch(e => {
            console.error('Raw data error:', e);
            return { data: { rawData: 'Raw data not available', isBase64: false } };
          }),
          // Use overview messageId for header, with query param for specific entry
          api.get(`/messages/${overviewMessageId}/header`, {
            params: { entryMessageId: selectedMessageId }
          }).catch(e => {
            console.error('Header error:', e);
            return { data: { rawData: 'Header not available', isBase64: false } };
          })
        ]);

        console.log('Message logs response:', logsResponse.data);
        if (logsResponse.data && logsResponse.data.length > 0) {
          console.log('First log entry structure:', logsResponse.data[0]);
          console.log('First log keys:', Object.keys(logsResponse.data[0]));
          console.log('First log level object:', logsResponse.data[0].level);
          if (logsResponse.data[0].level && typeof logsResponse.data[0].level === 'object') {
            console.log('Level object keys:', Object.keys(logsResponse.data[0].level));
          }
        }
        console.log('Message payloads response:', payloadsResponse.data);
        console.log('Message raw data response:', rawDataResponse.data);
        console.log('Message header response:', headerResponse.data);

        setLogs(logsResponse.data || []);
        setPayloads(payloadsResponse.data || []);

        // Handle raw data response
        if (rawDataResponse.data) {
          setRawDataTruncated(rawDataResponse.data.truncated || false);
          setRawDataFileSize(rawDataResponse.data.fileSize || 0);

          if (rawDataResponse.data.isBase64) {
            // Decode base64
            try {
              const decoded = atob(rawDataResponse.data.rawData);
              setRawData(decoded);
            } catch (e) {
              console.error('Failed to decode base64:', e);
              setRawData('[Binary data - ' + rawDataResponse.data.rawData.length + ' base64 characters]\n\nBase64 encoded data:\n' + rawDataResponse.data.rawData);
            }
          } else {
            setRawData(rawDataResponse.data.rawData || 'No raw data available');
          }
        } else {
          setRawData('No raw data available');
        }

        // Handle header response
        if (headerResponse.data) {
          setHeaderData(headerResponse.data.rawData || 'No header data available');
        } else {
          setHeaderData('No header data available');
        }
      } catch (error) {
        console.error('Failed to fetch detail data:', error);
        toast.error('Failed to load detail data: ' + (error.response?.data?.error || error.message));
      }
    };

    fetchDetailData();
  }, [selectedDetailIndex, details, toast]);

  const handleDownloadPayload = async (index = 0) => {
    try {
      const selectedDetail = details[selectedDetailIndex];
      const selectedMessageId = selectedDetail?.messageId;
      const overviewMessageId = message.messageId;

      const response = await api.get(`/messages/${overviewMessageId}/payload`, {
        params: {
          entryMessageId: selectedMessageId,
          index: index
        },
        responseType: 'blob'
      });

      const payload = payloads[index];
      const filename = payload?.originalFilename || `payload_${index}.dat`;

      const url = window.URL.createObjectURL(response.data);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success('Payload downloaded successfully');
    } catch (error) {
      toast.error('Failed to download payload: ' + (error.response?.data?.error || error.message));
    }
  };

  const getTimezoneOffset = () => {
    const offset = -new Date().getTimezoneOffset();
    const hours = Math.floor(Math.abs(offset) / 60);
    const minutes = Math.abs(offset) % 60;
    const sign = offset >= 0 ? '+' : '-';
    return `UTC${sign}${hours}${minutes > 0 ? ':' + minutes.toString().padStart(2, '0') : ''}`;
  };

  const formatTimestamp = (timestamp) => {
    if (!timestamp) return '-';
    return new Date(timestamp).toLocaleString('en-US', {
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit',
      hour12: false  // Use 24-hour format (military time)
    });
  };

  const getSignatureText = (signType) => {
    const signatures = {
      0: 'none',
      1: 'MD5',
      2: 'SHA-1',
      3: 'SHA-224',
      4: 'SHA-256',
      5: 'SHA-384',
      6: 'SHA-512',
      7: 'SHA3-224',
      8: 'SHA3-256',
      9: 'SHA3-384',
      10: 'SHA3-512'
    };
    return signatures[signType] || `Type ${signType}`;
  };

  const getEncryptionText = (encType) => {
    const encryptions = {
      0: 'none',
      1: '3DES',
      2: 'DES',
      3: 'RC2-40',
      4: 'RC2-64',
      5: 'RC2-128',
      6: 'RC2-196',
      7: 'AES-128',
      8: 'AES-192',
      9: 'AES-256',
      10: 'AES128-GCM',
      11: 'AES192-GCM',
      12: 'AES256-GCM'
    };
    return encryptions[encType] || `Type ${encType}`;
  };

  const getStatusColor = (state) => {
    if (state === 1) return '#28a745'; // FINISHED
    if (state === 2) return '#ffc107'; // PENDING
    if (state === 3) return '#dc3545'; // STOPPED
    return '#6c757d';
  };

  const getStatusText = (state) => {
    if (state === 1) return 'FINISHED';
    if (state === 2) return 'PENDING';
    if (state === 3) return 'STOPPED';
    return 'UNKNOWN';
  };

  const getLogLevelColor = (level) => {
    if (level === 'SEVERE' || level === 'ERROR') return '#dc3545';
    if (level === 'WARNING') return '#ffc107';
    if (level === 'INFO') return '#17a2b8';
    return '#6c757d';
  };

  const extractLogMessage = (log) => {
    if (typeof log === 'string') return log;

    // Check each field and make sure it's not an object before converting to string
    if (log.message) {
      if (typeof log.message === 'string') return log.message;
      if (typeof log.message === 'number') return String(log.message);
    }
    if (log.text) {
      if (typeof log.text === 'string') return log.text;
      if (typeof log.text === 'number') return String(log.text);
    }
    if (log.localizedMessage) {
      if (typeof log.localizedMessage === 'string') return log.localizedMessage;
      if (typeof log.localizedMessage === 'number') return String(log.localizedMessage);
    }
    if (log.name && typeof log.name === 'string') {
      let msg = log.name;
      if (log.localizedName && typeof log.localizedName === 'string') {
        msg += ': ' + log.localizedName;
      }
      return msg;
    }

    // Try to stringify the entire object as a last resort
    try {
      const str = JSON.stringify(log);
      if (str && str !== '{}') return str;
    } catch (e) {
      // ignore
    }
    return null; // Return null for entries we can't parse
  };

  const tabStyle = (active) => ({
    padding: '0.75rem 1rem',
    backgroundColor: active ? '#007bff' : '#e9ecef',
    color: active ? 'white' : '#495057',
    border: 'none',
    cursor: 'pointer',
    fontSize: '0.875rem',
    fontWeight: active ? '600' : '400',
    borderRadius: '4px 4px 0 0',
    marginRight: '0.25rem',
    transition: 'all 0.2s'
  });

  const sectionStyle = {
    marginBottom: '1rem',
    backgroundColor: '#fff',
    padding: '1rem',
    borderRadius: '4px',
    border: '1px solid #dee2e6'
  };

  const formatBytes = (bytes) => {
    if (bytes === 0) return '0 Bytes';
    const k = 1024;
    const sizes = ['Bytes', 'KB', 'MB', 'GB'];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return Math.round(bytes / Math.pow(k, i) * 100) / 100 + ' ' + sizes[i];
  };

  // Render transaction log tab
  const renderLog = () => {
    // Filter out logs that don't have meaningful messages
    const validLogs = logs.filter(log => {
      const message = extractLogMessage(log);
      return message && message !== '[object Object]' && message !== 'Log entry';
    });

    return (
      <div style={{ padding: '1rem' }}>
        <h3 style={{ margin: '0 0 1rem 0', fontSize: '1rem', color: '#495057' }}>
          Log of this message instance
        </h3>
        {validLogs.length === 0 ? (
          <div style={{ textAlign: 'center', padding: '2rem', color: '#6c757d' }}>
            No log entries found
          </div>
        ) : (
          <div style={{
            maxHeight: '500px',
            overflowY: 'auto',
            backgroundColor: '#fff',
            border: '1px solid #dee2e6',
            borderRadius: '4px'
          }}>
            {validLogs.map((log, index) => {
              // Extract level - it might be an object with a 'name' field or a string
              let logLevel = 'INFO';
              if (log.level) {
                if (typeof log.level === 'string') {
                  logLevel = log.level;
                } else if (typeof log.level === 'object') {
                  logLevel = log.level.name || log.level.value || log.level.level || 'INFO';
                }
              } else if (log.severity) {
                logLevel = typeof log.severity === 'string' ? log.severity : String(log.severity);
              }

              const logMessage = extractLogMessage(log);
              const logTimestamp = log.millis || log.timestamp || log.initDate || log.time;

              // Format timestamp properly - handle if it's already a Date object or a number/string
              let formattedTimestamp = '';
              if (logTimestamp) {
                try {
                  const date = logTimestamp instanceof Date ? logTimestamp : new Date(logTimestamp);
                  if (!isNaN(date.getTime())) {
                    formattedTimestamp = date.toLocaleString('en-US', {
                      month: '2-digit',
                      day: '2-digit',
                      hour: '2-digit',
                      minute: '2-digit',
                      second: '2-digit',
                      hour12: false
                    });
                  }
                } catch (e) {
                  // If timestamp parsing fails, leave it empty
                }
              }

              return (
                <div
                  key={index}
                  style={{
                    padding: '0.5rem 0.75rem',
                    borderBottom: index < validLogs.length - 1 ? '1px solid #dee2e6' : 'none',
                    fontSize: '0.875rem'
                  }}
                >
                  <div style={{ color: '#212529', whiteSpace: 'pre-wrap' }}>
                    {formattedTimestamp && (
                      <span style={{ color: '#6c757d', fontSize: '0.75rem', marginRight: '0.5rem' }}>
                        [{formattedTimestamp}]
                      </span>
                    )}
                    {logMessage}
                  </div>
                </div>
              );
            })}
          </div>
        )}
      </div>
    );
  };

  const handleDownloadEncrypted = async () => {
    try {
      const selectedDetail = details[selectedDetailIndex];
      const selectedMessageId = selectedDetail?.messageId;
      const overviewMessageId = message.messageId;

      const response = await api.get(`/messages/${overviewMessageId}/download/encrypted`, {
        params: { entryMessageId: selectedMessageId },
        responseType: 'blob'
      });

      const filename = `${selectedMessageId || overviewMessageId}_encrypted_raw.dat`;
      const url = window.URL.createObjectURL(response.data);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success('Encrypted raw message downloaded successfully');
    } catch (error) {
      toast.error('Failed to download encrypted message: ' + (error.response?.data?.error || error.message));
    }
  };

  const handleDownloadDecrypted = async () => {
    try {
      const selectedDetail = details[selectedDetailIndex];
      const selectedMessageId = selectedDetail?.messageId;
      const overviewMessageId = message.messageId;

      const response = await api.get(`/messages/${overviewMessageId}/download/decrypted`, {
        params: { entryMessageId: selectedMessageId },
        responseType: 'blob'
      });

      const filename = `${selectedMessageId || overviewMessageId}_decrypted_raw.dat`;
      const url = window.URL.createObjectURL(response.data);
      const a = document.createElement('a');
      a.href = url;
      a.download = filename;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success('Decrypted raw message downloaded successfully');
    } catch (error) {
      toast.error('Failed to download decrypted message: ' + (error.response?.data?.error || error.message));
    }
  };

  // Render raw data tab
  const renderRawData = () => (
    <div style={{ padding: '1rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
        <h3 style={{ margin: 0, fontSize: '1rem', color: '#495057' }}>
          Raw data
        </h3>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button
            onClick={handleDownloadEncrypted}
            style={{
              padding: '0.375rem 0.75rem',
              backgroundColor: '#ffc107',
              color: '#212529',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '0.875rem'
            }}
          >
            🔒 Download Encrypted
          </button>
          <button
            onClick={handleDownloadDecrypted}
            style={{
              padding: '0.375rem 0.75rem',
              backgroundColor: '#28a745',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '0.875rem'
            }}
          >
            🔓 Download Decrypted
          </button>
        </div>
      </div>
      {rawDataTruncated && (
        <div style={{
          backgroundColor: '#fff3cd',
          border: '1px solid #ffc107',
          borderRadius: '4px',
          padding: '0.75rem 1rem',
          marginBottom: '1rem',
          fontSize: '0.875rem',
          color: '#856404'
        }}>
          ⚠️ <strong>Large file truncated:</strong> Showing first 100 KB of {(rawDataFileSize / 1024).toFixed(2)} KB total.
          Use "Save to File" button above to download the complete raw data.
        </div>
      )}
      <pre style={{
        backgroundColor: '#f8f9fa',
        padding: '1rem',
        borderRadius: '4px',
        border: '1px solid #dee2e6',
        fontSize: '0.75rem',
        overflow: 'auto',
        maxHeight: '500px',
        fontFamily: 'monospace',
        margin: 0,
        whiteSpace: 'pre-wrap',
        wordBreak: 'break-word'
      }}>
        {rawData}
      </pre>
    </div>
  );

  // Render message header tab - just display raw header content like Swing UI
  const renderHeader = () => (
    <div style={{ padding: '1rem' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
        <h3 style={{ margin: 0, fontSize: '1rem', color: '#495057' }}>
          Message Header
        </h3>
        <button
          onClick={() => {
            const blob = new Blob([headerData], { type: 'text/plain' });
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `header_${message.messageId}.txt`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
          }}
          style={{
            padding: '0.375rem 0.75rem',
            backgroundColor: '#17a2b8',
            color: 'white',
            border: 'none',
            borderRadius: '4px',
            cursor: 'pointer',
            fontSize: '0.875rem'
          }}
        >
          💾 Save to File
        </button>
      </div>
      <pre style={{
        backgroundColor: '#f8f9fa',
        padding: '1rem',
        borderRadius: '4px',
        border: '1px solid #dee2e6',
        fontSize: '0.75rem',
        overflow: 'auto',
        maxHeight: '500px',
        fontFamily: 'monospace',
        margin: 0,
        whiteSpace: 'pre-wrap',
        wordBreak: 'break-word'
      }}>
        {headerData}
      </pre>
    </div>
  );

  // Render payload tab - display raw MIME content
  const renderPayload = (payloadIndex) => {
    const payload = payloads[payloadIndex];
    if (!payload) {
      return (
        <div style={{ padding: '2rem', textAlign: 'center', color: '#6c757d' }}>
          Payload not found
        </div>
      );
    }

    const isBinary = payload.isText === false ||
                     (payload.contentType &&
                      (payload.contentType.includes('pdf') ||
                       payload.contentType.includes('image/') ||
                       payload.contentType.includes('application/octet-stream') ||
                       payload.contentType.includes('application/zip')));

    return (
      <div style={{ padding: '1rem' }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <div>
            <h3 style={{ margin: 0, fontSize: '1rem', color: '#495057' }}>
              Payload {payloadIndex + 1}
              {payload.originalFilename && (
                <span style={{ fontWeight: 'normal', color: '#6c757d', marginLeft: '0.5rem' }}>
                  ({payload.originalFilename})
                </span>
              )}
            </h3>
            {payload.contentType && (
              <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.25rem' }}>
                Content-Type: {payload.contentType}
              </div>
            )}
            {payload.size !== undefined && (
              <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.25rem' }}>
                Size: {payload.size.toLocaleString()} bytes
              </div>
            )}
          </div>
          <button
            onClick={() => handleDownloadPayload(payloadIndex)}
            style={{
              padding: '0.375rem 0.75rem',
              backgroundColor: '#28a745',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '0.875rem'
            }}
          >
            📥 Download
          </button>
        </div>
        {isBinary ? (
          <div style={{
            backgroundColor: '#f8f9fa',
            padding: '2rem',
            borderRadius: '4px',
            border: '1px solid #dee2e6',
            textAlign: 'center',
            color: '#6c757d'
          }}>
            <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>📄</div>
            <div style={{ fontSize: '1rem', fontWeight: '600', marginBottom: '0.5rem' }}>
              Binary File
            </div>
            <div style={{ fontSize: '0.875rem' }}>
              This is a binary file and cannot be displayed as text.
              {payload.size && ` (${payload.size.toLocaleString()} bytes)`}
            </div>
            <div style={{ fontSize: '0.875rem', marginTop: '0.5rem' }}>
              Click the Download button above to save the file.
            </div>
          </div>
        ) : (
          <pre style={{
            backgroundColor: '#f8f9fa',
            padding: '1rem',
            borderRadius: '4px',
            border: '1px solid #dee2e6',
            fontSize: '0.75rem',
            overflow: 'auto',
            maxHeight: '500px',
            fontFamily: 'monospace',
            margin: 0,
            whiteSpace: 'pre-wrap',
            wordBreak: 'break-all'
          }}>
            {payload.preview || 'No payload data available'}
          </pre>
        )}
      </div>
    );
  };

  return (
    <div style={{
      position: 'fixed',
      top: 0,
      left: 0,
      right: 0,
      bottom: 0,
      backgroundColor: 'rgba(0,0,0,0.5)',
      display: 'flex',
      justifyContent: 'center',
      alignItems: 'center',
      zIndex: 1000,
      padding: '1rem'
    }} onClick={onClose}>
      <div style={{
        backgroundColor: 'white',
        borderRadius: '8px',
        maxWidth: '1200px',
        width: '100%',
        height: '95vh',
        display: 'flex',
        flexDirection: 'column',
        overflow: 'hidden',
        boxShadow: '0 4px 20px rgba(0,0,0,0.15)'
      }} onClick={(e) => e.stopPropagation()}>

        {/* Header */}
        <div style={{
          padding: '1.5rem',
          borderBottom: '1px solid #dee2e6',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          backgroundColor: '#f8f9fa'
        }}>
          <div>
            <h2 style={{ margin: 0, fontSize: '1.25rem' }}>Message Details</h2>
            <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.25rem' }}>
              {message.senderAS2Id} → {message.receiverAS2Id}
            </div>
          </div>
          <button
            onClick={onClose}
            style={{
              background: 'none',
              border: 'none',
              fontSize: '1.5rem',
              cursor: 'pointer',
              padding: 0,
              color: '#6c757d',
              width: '32px',
              height: '32px',
              display: 'flex',
              alignItems: 'center',
              justifyContent: 'center'
            }}
          >
            ×
          </button>
        </div>

        {/* Details Selection Table */}
        {loading ? (
          <div style={{ textAlign: 'center', padding: '2rem', color: '#6c757d' }}>
            <div style={{ fontSize: '2rem', marginBottom: '1rem' }}>⏳</div>
            Loading message details...
          </div>
        ) : details.length > 0 ? (
          <div style={{
            padding: '0.75rem 1rem',
            backgroundColor: '#f8f9fa',
            borderBottom: '2px solid #dee2e6',
            maxHeight: '180px',
            overflowY: 'auto'
          }}>
            <div style={{ fontSize: '0.75rem', fontWeight: '600', color: '#495057', marginBottom: '0.5rem' }}>
              Transaction Details ({details.length} {details.length === 1 ? 'entry' : 'entries'})
            </div>
            <table style={{
              width: '100%',
              fontSize: '0.75rem',
              borderCollapse: 'collapse',
              backgroundColor: 'white',
              border: '1px solid #dee2e6'
            }}>
              <thead>
                <tr style={{ backgroundColor: '#e9ecef', borderBottom: '2px solid #dee2e6' }}>
                  <th style={{ padding: '0.4rem', textAlign: 'center', fontWeight: '600', width: '55px' }}>Dir</th>
                  <th style={{ padding: '0.4rem', textAlign: 'left', fontWeight: '600', width: '140px' }}>Timestamp ({getTimezoneOffset()})</th>
                  <th style={{ padding: '0.4rem', textAlign: 'center', fontWeight: '600', width: '50px' }}>Type</th>
                  <th style={{ padding: '0.4rem', textAlign: 'left', fontWeight: '600' }}>Message ID</th>
                  <th style={{ padding: '0.4rem', textAlign: 'left', fontWeight: '600', width: '80px' }}>Signature</th>
                  <th style={{ padding: '0.4rem', textAlign: 'left', fontWeight: '600', width: '80px' }}>Encryption</th>
                  <th style={{ padding: '0.4rem', textAlign: 'left', fontWeight: '600', width: '100px' }}>Sender</th>
                  <th style={{ padding: '0.4rem', textAlign: 'left', fontWeight: '600', width: '100px' }}>AS2 Server</th>
                </tr>
              </thead>
              <tbody>
                {details.map((detail, index) => (
                  <tr
                    key={index}
                    onClick={() => setSelectedDetailIndex(index)}
                    style={{
                      borderBottom: '1px solid #e9ecef',
                      cursor: 'pointer',
                      backgroundColor: selectedDetailIndex === index ? '#cfe2ff' : 'white'
                    }}
                    onMouseEnter={(e) => {
                      if (selectedDetailIndex !== index) {
                        e.currentTarget.style.backgroundColor = '#f1f3f5';
                      }
                    }}
                    onMouseLeave={(e) => {
                      if (selectedDetailIndex !== index) {
                        e.currentTarget.style.backgroundColor = 'white';
                      }
                    }}
                  >
                    <td style={{ padding: '0.4rem', textAlign: 'center' }}>
                      {detail.direction === 1 ? (
                        <span style={{ color: '#28a745', fontSize: '1.5rem', fontWeight: 'bold' }} title="Inbound">←</span>
                      ) : (
                        <span style={{ color: '#dc3545', fontSize: '1.5rem', fontWeight: 'bold' }} title="Outbound">→</span>
                      )}
                    </td>
                    <td style={{ padding: '0.4rem', whiteSpace: 'nowrap', fontSize: '0.7rem' }}>
                      {formatTimestamp(detail.initDate)}
                    </td>
                    <td style={{ padding: '0.4rem', textAlign: 'center' }}>
                      {detail.mdn ? (
                        <span style={{
                          padding: '0.2rem 0.4rem',
                          borderRadius: '3px',
                          fontSize: '0.65rem',
                          fontWeight: '600',
                          backgroundColor: detail.state === 1 ? '#28a74520' : '#dc354520',
                          color: detail.state === 1 ? '#28a745' : '#dc3545'
                        }}>
                          MDN
                        </span>
                      ) : (
                        <span style={{
                          padding: '0.2rem 0.4rem',
                          borderRadius: '3px',
                          fontSize: '0.65rem',
                          fontWeight: '600',
                          backgroundColor: '#007bff20',
                          color: '#007bff'
                        }}>
                          MSG
                        </span>
                      )}
                    </td>
                    <td style={{ padding: '0.4rem' }}>
                      <code style={{
                        fontSize: '0.65rem',
                        backgroundColor: '#f8f9fa',
                        padding: '0.1rem 0.2rem',
                        borderRadius: '2px',
                        wordBreak: 'break-all',
                        display: 'block',
                        maxWidth: '100%',
                        overflow: 'hidden',
                        textOverflow: 'ellipsis',
                        whiteSpace: 'nowrap'
                      }}>
                        {detail.messageId || '-'}
                      </code>
                    </td>
                    <td style={{ padding: '0.4rem', fontSize: '0.65rem' }}>
                      {getSignatureText(detail.signType || 0)}
                    </td>
                    <td style={{ padding: '0.4rem', fontSize: '0.65rem' }}>
                      {detail.mdn ? '--' : getEncryptionText(detail.encryptionType || 0)}
                    </td>
                    <td style={{ padding: '0.4rem', fontSize: '0.65rem' }} title={detail.senderHost || ''}>
                      {detail.senderHost || '-'}
                    </td>
                    <td style={{ padding: '0.4rem', fontSize: '0.65rem' }} title={detail.userAgent || ''}>
                      {detail.userAgent || '-'}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : null}

        {/* Tabs */}
        <div style={{
          padding: '0.5rem 1rem',
          backgroundColor: '#f8f9fa',
          borderBottom: '1px solid #dee2e6',
          overflowX: 'auto',
          whiteSpace: 'nowrap'
        }}>
          <button
            style={tabStyle(activeTab === 'log')}
            onClick={() => setActiveTab('log')}
          >
            📋 Log of this message instance
          </button>
          <button
            style={tabStyle(activeTab === 'raw')}
            onClick={() => setActiveTab('raw')}
          >
            📄 Raw data
          </button>
          <button
            style={tabStyle(activeTab === 'header')}
            onClick={() => setActiveTab('header')}
          >
            📨 Message header
          </button>
          {payloads.map((payload, index) => (
            <button
              key={index}
              style={tabStyle(activeTab === `payload-${index}`)}
              onClick={() => setActiveTab(`payload-${index}`)}
            >
              📦 Payload ({index + 1})
            </button>
          ))}
        </div>

        {/* Content */}
        <div style={{ flex: 1, overflowY: 'auto', backgroundColor: '#f8f9fa' }}>
          {activeTab === 'log' && renderLog()}
          {activeTab === 'raw' && renderRawData()}
          {activeTab === 'header' && renderHeader()}
          {activeTab.startsWith('payload-') && renderPayload(parseInt(activeTab.split('-')[1]))}
        </div>

        {/* Footer */}
        <div style={{
          padding: '1rem 1.5rem',
          borderTop: '1px solid #dee2e6',
          display: 'flex',
          justifyContent: 'flex-end',
          backgroundColor: '#f8f9fa'
        }}>
          <button
            onClick={onClose}
            style={{
              padding: '0.5rem 1.5rem',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '0.875rem'
            }}
          >
            Close
          </button>
        </div>
      </div>
    </div>
  );
}
