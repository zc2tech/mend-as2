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
import { useQueryClient } from '@tanstack/react-query';
import { useToast } from '../../components/Toast';
import { useAuth } from '../auth/useAuth';
import api from '../../api/client';

export default function ManualSend({ onClose }) {
  const [files, setFiles] = useState([]);
  const [senderPartnerId, setSenderPartnerId] = useState('');
  const [receiverPartnerId, setReceiverPartnerId] = useState('');
  const [subject, setSubject] = useState('AS2 message');
  const [contentType, setContentType] = useState('application/EDI-Consent');
  const [loading, setLoading] = useState(false);
  const [dragActive, setDragActive] = useState(false);
  const [partners, setPartners] = useState([]);
  const [partnersLoading, setPartnersLoading] = useState(true);
  const queryClient = useQueryClient();
  const toast = useToast();
  const { user } = useAuth();

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

  // Fetch partners list filtered by current user visibility
  useEffect(() => {
    const fetchPartners = async () => {
      try {
        setPartnersLoading(true);
        // Fetch only partners visible to current user
        const response = await api.get('/partners', {
          params: { visibleToUser: user?.id }
        });
        setPartners(response.data || []);
      } catch (error) {
        console.error('Failed to fetch partners:', error);
        toast.error('Failed to load partners list');
        setPartners([]);
      } finally {
        setPartnersLoading(false);
      }
    };
    fetchPartners();
  }, [toast, user]);

  // Separate local stations and remote partners
  const localStations = partners.filter(p => p.localStation === true) || [];
  const remotePartners = partners.filter(p => p.localStation !== true) || [];

  // Auto-select if only one local station
  useEffect(() => {
    if (localStations.length === 1 && !senderPartnerId && localStations[0]?.dbid) {
      const id = String(localStations[0].dbid);
      setSenderPartnerId(id);
    }
  }, [localStations, senderPartnerId]);

  const handleDrag = (e) => {
    e.preventDefault();
    e.stopPropagation();
    if (e.type === 'dragenter' || e.type === 'dragover') {
      setDragActive(true);
    } else if (e.type === 'dragleave') {
      setDragActive(false);
    }
  };

  const handleDrop = (e) => {
    e.preventDefault();
    e.stopPropagation();
    setDragActive(false);

    if (e.dataTransfer.files && e.dataTransfer.files.length > 0) {
      const newFiles = Array.from(e.dataTransfer.files);
      setFiles(prev => [...prev, ...newFiles]);
    }
  };

  const handleFileChange = (e) => {
    if (e.target.files && e.target.files.length > 0) {
      const newFiles = Array.from(e.target.files);
      setFiles(prev => [...prev, ...newFiles]);
    }
  };

  const removeFile = (index) => {
    setFiles(prev => prev.filter((_, i) => i !== index));
  };

  const handleSend = async () => {
    if (files.length === 0) {
      toast.warning('Please select at least one file to send');
      return;
    }

    if (!senderPartnerId || !receiverPartnerId) {
      toast.warning('Please select both sender and receiver');
      return;
    }

    // Debug: log file details before sending
    console.log('=== Sending files ===');
    files.forEach((file, index) => {
      console.log(`File ${index}: name="${file.name}", size=${file.size}, type="${file.type}"`);
    });

    setLoading(true);
    try {
      const formData = new FormData();

      // Add all files
      files.forEach(file => {
        formData.append('files', file);
        console.log(`Appending to FormData: ${file.name} (${file.size} bytes)`);
      });

      formData.append('senderId', senderPartnerId);
      formData.append('receiverId', receiverPartnerId);
      formData.append('subject', subject);
      formData.append('contentType', contentType);

      await api.post('/messages/send', formData, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      });

      const fileCount = files.length;
      const fileNames = files.map(f => f.name).join(', ');
      toast.success(fileCount === 1
        ? `File "${files[0].name}" sent successfully`
        : `${fileCount} files sent successfully: ${fileNames}`);
      queryClient.invalidateQueries(['messages']);
      onClose();
    } catch (error) {
      const errorMsg = error.response?.data?.error || error.message || 'Unknown error';
      toast.error('Failed to send message: ' + errorMsg);
    } finally {
      setLoading(false);
    }
  };

  const dropZoneStyle = {
    border: dragActive ? '2px dashed #007bff' : '2px dashed #dee2e6',
    borderRadius: '8px',
    padding: '2rem',
    textAlign: 'center',
    backgroundColor: dragActive ? '#e7f3ff' : '#f8f9fa',
    cursor: 'pointer',
    transition: 'all 0.2s',
    marginBottom: '1.5rem'
  };

  const formGroupStyle = {
    marginBottom: '1rem'
  };

  const labelStyle = {
    display: 'block',
    marginBottom: '0.5rem',
    fontWeight: '600',
    fontSize: '0.875rem'
  };

  const inputStyle = {
    width: '100%',
    padding: '0.5rem',
    border: '1px solid #ced4da',
    borderRadius: '4px',
    fontSize: '0.875rem',
    boxSizing: 'border-box'
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
        maxWidth: '600px',
        width: '100%',
        maxHeight: '90vh',
        display: 'flex',
        flexDirection: 'column',
        overflow: 'hidden'
      }} onClick={(e) => e.stopPropagation()}>

        {/* Header */}
        <div style={{
          padding: '1.5rem',
          borderBottom: '1px solid #dee2e6',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <h2 style={{ margin: 0 }}>Send File to Partner</h2>
          <button
            onClick={onClose}
            style={{
              background: 'none',
              border: 'none',
              fontSize: '1.5rem',
              cursor: 'pointer',
              padding: 0,
              color: '#6c757d'
            }}
          >
            ×
          </button>
        </div>

        {/* Content */}
        <div style={{ padding: '1.5rem', overflowY: 'auto', flex: 1 }}>

          {/* File Drop Zone */}
          <div
            style={dropZoneStyle}
            onDragEnter={handleDrag}
            onDragLeave={handleDrag}
            onDragOver={handleDrag}
            onDrop={handleDrop}
            onClick={() => document.getElementById('fileInput').click()}
          >
            <input
              id="fileInput"
              type="file"
              multiple
              onChange={handleFileChange}
              style={{ display: 'none' }}
              disabled={loading}
            />
            {files.length > 0 ? (
              <div>
                <div style={{ fontSize: '3rem', marginBottom: '0.5rem' }}>
                  {files.length === 1 ? '📄' : '📦'}
                </div>
                <div style={{ fontWeight: '600', fontSize: '1rem', marginBottom: '0.5rem' }}>
                  {files.length} file{files.length > 1 ? 's' : ''} selected
                </div>
                <div style={{
                  maxHeight: '150px',
                  overflowY: 'auto',
                  textAlign: 'left',
                  backgroundColor: '#f8f9fa',
                  padding: '0.5rem',
                  borderRadius: '4px',
                  marginBottom: '0.5rem'
                }}>
                  {files.map((file, index) => (
                    <div key={index} style={{
                      display: 'flex',
                      justifyContent: 'space-between',
                      alignItems: 'center',
                      padding: '0.25rem 0',
                      borderBottom: index < files.length - 1 ? '1px solid #dee2e6' : 'none'
                    }}>
                      <div style={{ flex: 1, fontSize: '0.875rem' }}>
                        <strong>{index === 0 ? '📄 ' : '📎 '}</strong>
                        {file.name} ({(file.size / 1024).toFixed(2)} KB)
                        {index === 0 && <span style={{ color: '#007bff', marginLeft: '0.5rem' }}>(main)</span>}
                        {index > 0 && <span style={{ color: '#6c757d', marginLeft: '0.5rem' }}>(attachment)</span>}
                      </div>
                      <button
                        onClick={(e) => {
                          e.stopPropagation();
                          removeFile(index);
                        }}
                        style={{
                          marginLeft: '0.5rem',
                          padding: '0.125rem 0.5rem',
                          backgroundColor: '#dc3545',
                          color: 'white',
                          border: 'none',
                          borderRadius: '3px',
                          cursor: 'pointer',
                          fontSize: '0.75rem'
                        }}
                      >
                        ×
                      </button>
                    </div>
                  ))}
                </div>
                <button
                  onClick={(e) => {
                    e.stopPropagation();
                    setFiles([]);
                  }}
                  style={{
                    marginTop: '0.5rem',
                    padding: '0.25rem 0.75rem',
                    backgroundColor: '#dc3545',
                    color: 'white',
                    border: 'none',
                    borderRadius: '4px',
                    cursor: 'pointer',
                    fontSize: '0.875rem'
                  }}
                >
                  Clear All Files
                </button>
              </div>
            ) : (
              <div>
                <div style={{ fontSize: '3rem', marginBottom: '0.5rem' }}>📁</div>
                <div style={{ fontWeight: '600', marginBottom: '0.5rem', fontSize: '1rem' }}>
                  Drop files here or click to browse
                </div>
                <div style={{ color: '#6c757d', fontSize: '0.875rem' }}>
                  You can select multiple files. The first file will be the main payload, others will be attachments.
                </div>
              </div>
            )}
          </div>

          {partnersLoading ? (
            <div style={{ textAlign: 'center', padding: '1rem', color: '#6c757d' }}>
              Loading partners...
            </div>
          ) : (
            <>
              {/* Sender Selection */}
              {localStations.length > 1 && (
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Sender (Local Station) *</label>
                  <select
                    value={senderPartnerId}
                    onChange={(e) => setSenderPartnerId(e.target.value)}
                    style={inputStyle}
                    disabled={loading}
                  >
                    <option value="">-- Select Local Station --</option>
                    {localStations.map(station => (
                      <option key={station.dbid} value={String(station.dbid)}>
                        {station.name} ({station.as2Identification})
                      </option>
                    ))}
                  </select>
                  {localStations.length === 0 && (
                    <div style={{ fontSize: '0.875rem', color: '#dc3545', marginTop: '0.25rem' }}>
                      No local stations configured!
                    </div>
                  )}
                </div>
              )}

              {localStations.length === 1 && (
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Sender (Local Station)</label>
                  <div style={{
                    ...inputStyle,
                    backgroundColor: '#e9ecef',
                    color: '#495057'
                  }}>
                    {localStations[0].name} ({localStations[0].as2Identification})
                  </div>
                  <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.25rem' }}>
                    Only one local station configured
                  </div>
                </div>
              )}

              {/* Receiver Selection */}
              <div style={formGroupStyle}>
                <label style={labelStyle}>Receiver (Remote Partner) *</label>
                <select
                  value={receiverPartnerId}
                  onChange={(e) => setReceiverPartnerId(e.target.value)}
                  style={inputStyle}
                  disabled={loading}
                >
                  <option value="">-- Select Remote Partner --</option>
                  {remotePartners.map(partner => (
                    <option key={partner.dbid} value={String(partner.dbid)}>
                      {partner.name} ({partner.as2Identification})
                    </option>
                  ))}
                </select>
                {remotePartners.length === 0 && (
                  <div style={{ fontSize: '0.875rem', color: '#dc3545', marginTop: '0.25rem' }}>
                    No remote partners configured!
                  </div>
                )}
              </div>

              {/* Subject */}
              <div style={formGroupStyle}>
                <label style={labelStyle}>Subject</label>
                <input
                  type="text"
                  value={subject}
                  onChange={(e) => setSubject(e.target.value)}
                  style={inputStyle}
                  disabled={loading}
                  placeholder="AS2 message"
                />
              </div>

              {/* Content Type */}
              <div style={formGroupStyle}>
                <label style={labelStyle}>Content Type (First File Only)</label>
                <input
                  type="text"
                  value={contentType}
                  onChange={(e) => setContentType(e.target.value)}
                  style={inputStyle}
                  disabled={loading}
                  placeholder="application/EDI-Consent"
                />
                <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.25rem' }}>
                  MIME type for the first file. Additional files will auto-detect content type from file extension.
                </div>
              </div>
            </>
          )}
        </div>

        {/* Footer */}
        <div style={{
          padding: '1rem 1.5rem',
          borderTop: '1px solid #dee2e6',
          display: 'flex',
          justifyContent: 'flex-end',
          gap: '0.5rem'
        }}>
          <button
            onClick={onClose}
            disabled={loading}
            style={{
              padding: '0.5rem 1rem',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: loading ? 'not-allowed' : 'pointer',
              opacity: loading ? 0.6 : 1
            }}
          >
            Cancel
          </button>
          <button
            onClick={handleSend}
            disabled={loading || files.length === 0 || !senderPartnerId || !receiverPartnerId || partnersLoading}
            style={{
              padding: '0.5rem 1.5rem',
              backgroundColor: (loading || files.length === 0 || !senderPartnerId || !receiverPartnerId) ? '#6c757d' : '#28a745',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: (loading || files.length === 0 || !senderPartnerId || !receiverPartnerId) ? 'not-allowed' : 'pointer',
              opacity: (loading || files.length === 0 || !senderPartnerId || !receiverPartnerId) ? 0.6 : 1
            }}
            title={`Debug: files=${files.length}, sender=${senderPartnerId}, receiver=${receiverPartnerId}, loading=${loading}, partnersLoading=${partnersLoading}`}
          >
            {loading ? 'Sending...' : `Send ${files.length > 0 ? files.length : ''} File${files.length !== 1 ? 's' : ''}`}
          </button>
        </div>
      </div>
    </div>
  );
}
