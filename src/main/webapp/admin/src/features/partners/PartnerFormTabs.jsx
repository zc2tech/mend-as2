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

import { useState, useEffect, useMemo } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { useCreatePartner, useUpdatePartner } from './usePartners';
import { useCertificates } from '../certificates/useCertificates';
import { useToast } from '../../components/Toast';
import api from '../../api/client';

const partnerSchema = z.object({
  // General tab
  name: z.string().min(1, 'Name is required').max(255, 'Name too long'),
  as2Identification: z.string().min(1, 'AS2 ID is required').max(255, 'AS2 ID too long'),
  localStation: z.boolean().default(false),
  comment: z.string().optional(),

  // Send tab
  url: z.string().optional(),
  subject: z.string().optional(),
  contentType: z.string().optional(),
  email: z.string().optional(),
  encryptionType: z.coerce.number().optional(),
  signType: z.coerce.number().optional(),
  compressionType: z.coerce.number().optional(),

  // Receive/MDN tab
  mdnURL: z.string().optional(),
  syncMDN: z.boolean().default(true),
  signedMDN: z.boolean().default(false),

  // Security tab
  signFingerprintSHA1: z.string().optional(),
  cryptFingerprintSHA1: z.string().optional(),
  overwriteLocalStationSecurity: z.boolean().default(false),
  signOverwriteLocalstationFingerprintSHA1: z.string().optional(),
  cryptOverwriteLocalstationFingerprintSHA1: z.string().optional(),
  useAlgorithmIdentifierProtectionAttribute: z.boolean().default(true),

  // Directory Poll tab
  enableDirPoll: z.boolean().default(true),
  pollInterval: z.coerce.number().min(1).default(30),
  maxPollFiles: z.coerce.number().min(1).default(100),
  pollIgnoreListAsString: z.string().optional(),
  keepFilenameOnReceipt: z.boolean().default(false),

  // HTTP tab
  httpProtocolVersion: z.string().default('1.1'),
  contentTransferEncoding: z.coerce.number().default(0),

  // HTTP Authentication tab
  authModeMessage: z.coerce.number().default(0),
  useHttpAuthMessage: z.boolean().default(false),
  httpAuthMessageUser: z.string().optional(),
  httpAuthMessagePassword: z.string().optional(),
  authModeAsyncMDN: z.coerce.number().default(0),
  useHttpAuthAsyncMDN: z.boolean().default(false),
  httpAuthAsyncMDNUser: z.string().optional(),
  httpAuthAsyncMDNPassword: z.string().optional(),

  // Contact tab
  contactAS2: z.string().optional(),
  contactCompany: z.string().optional(),

  // Additional fields from Partner.java
  notifySend: z.coerce.number().optional(),
  notifyReceive: z.coerce.number().optional(),
  notifySendReceive: z.coerce.number().optional(),
  notifySendEnabled: z.boolean().optional(),
  notifyReceiveEnabled: z.boolean().optional(),
  notifySendReceiveEnabled: z.boolean().optional()
});

// Helper function to flatten nested authentication credentials from backend format
const getFlattenedPartnerData = (partner) => {
  if (!partner) return null;

  const authModeMessageValue = partner.authenticationCredentialsMessage?.authMode ?? 0;
  const authModeAsyncMDNValue = partner.authenticationCredentialsAsyncMDN?.authMode ?? 0;

  // Create a copy WITHOUT the nested auth objects
  const { authenticationCredentialsMessage, authenticationCredentialsAsyncMDN, ...partnerWithoutNestedAuth } = partner;

  const flattened = {
    ...partnerWithoutNestedAuth,
    // Flatten authentication credentials from nested objects
    // Convert to STRING to match select values (z.coerce.number in schema will convert back)
    authModeMessage: String(authModeMessageValue),
    httpAuthMessageUser: partner.authenticationCredentialsMessage?.user ?? '',
    httpAuthMessagePassword: partner.authenticationCredentialsMessage?.password ?? '',
    useHttpAuthMessage: partner.authenticationCredentialsMessage?.enabled ?? false,

    authModeAsyncMDN: String(authModeAsyncMDNValue),
    httpAuthAsyncMDNUser: partner.authenticationCredentialsAsyncMDN?.user ?? '',
    httpAuthAsyncMDNPassword: partner.authenticationCredentialsAsyncMDN?.password ?? '',
    useHttpAuthAsyncMDN: partner.authenticationCredentialsAsyncMDN?.enabled ?? false
  };

  return flattened;
};

export default function PartnerFormTabs({ partner, onClose, onSuccess }) {
  // Memoize the flattened partner data so it doesn't change on every render
  const initialPartnerData = useMemo(() => {
    const flattened = getFlattenedPartnerData(partner);
    return flattened;
  }, [partner?.dbid]); // Only re-compute if the partner ID changes

  const [activeTab, setActiveTab] = useState('general');
  const [localStationType, setLocalStationType] = useState(partner?.localStation ?? null);
  const createPartner = useCreatePartner();
  const updatePartner = useUpdatePartner();
  const toast = useToast();
  const isEdit = !!partner;

  // Visibility control state
  const [visibilityMode, setVisibilityMode] = useState('all');
  const [selectedUserIds, setSelectedUserIds] = useState([]);
  const [users, setUsers] = useState([]);
  const [loadingUsers, setLoadingUsers] = useState(false);

  // Fetch certificates for the dropdowns
  const { data: certificates, isLoading: certsLoading } = useCertificates('sign');

  const {
    register,
    handleSubmit,
    formState: { errors, isSubmitting },
    watch,
    setValue,
    reset,
    getValues
  } = useForm({
    resolver: zodResolver(partnerSchema),
    defaultValues: initialPartnerData || {
      name: '',
      as2Identification: '',
      localStation: false,
      comment: '',
      url: '',
      mdnURL: '',
      subject: 'AS2 message',
      contentType: 'application/EDI-Consent',
      email: 'sender@as2server.com',
      syncMDN: true,
      signedMDN: false,
      encryptionType: 5, // AES-128-CBC
      signType: 13, // SHA-256
      compressionType: 0, // None
      enableDirPoll: true,
      pollInterval: 30,
      maxPollFiles: 100,
      keepFilenameOnReceipt: false,
      httpProtocolVersion: '1.1',
      contentTransferEncoding: 0,
      authModeMessage: "0",
      useHttpAuthMessage: false,
      httpAuthMessageUser: '',
      httpAuthMessagePassword: '',
      authModeAsyncMDN: "0",
      useHttpAuthAsyncMDN: false,
      httpAuthAsyncMDNUser: '',
      httpAuthAsyncMDNPassword: '',
      overwriteLocalStationSecurity: false,
      useAlgorithmIdentifierProtectionAttribute: true,
      signFingerprintSHA1: '',
      cryptFingerprintSHA1: '',
      overwriteSignFingerprintSHA1: '',
      overwriteCryptFingerprintSHA1: ''
    }
  });

  // REMOVED: This useEffect was causing form resets on every render
  // The initialPartnerData with useMemo handles initialization correctly

  // Log form validation errors
  useEffect(() => {
    if (Object.keys(errors).length > 0) {
      // Validation errors - handled by form display
    }
  }, [errors]);

  // REMOVED: This useEffect with reset() was also causing form resets
  // The memoized initialPartnerData in defaultValues handles initialization

  const enableDirPoll = watch('enableDirPoll');
  const overwriteLocalStationSecurity = watch('overwriteLocalStationSecurity');

  // For new partners, determine localStation from the selection step
  const localStation = isEdit ? partner.localStation : localStationType;

  // Fetch server URL when creating a new local station
  useEffect(() => {
    if (!isEdit && localStation) {
      // Only fetch for new local stations
      fetch('/as2/api/v1/system/server-url', {
        credentials: 'include'
      })
        .then(res => res.json())
        .then(data => {
          if (data.httpReceiverUrl) {
            setValue('url', data.httpReceiverUrl);
            setValue('mdnURL', data.httpReceiverUrl);
          }
        })
        .catch(err => {
          console.error('Failed to fetch server URL:', err);
        });
    }
  }, [localStation, isEdit, setValue]);

  const handleLocalStationSelection = (isLocal) => {
    setLocalStationType(isLocal);
    setValue('localStation', isLocal);
  };

  const onSubmit = async (data) => {
    // Transform flat auth fields back to nested structure for backend
    const {
      authModeMessage,
      httpAuthMessageUser,
      httpAuthMessagePassword,
      useHttpAuthMessage,
      authModeAsyncMDN,
      httpAuthAsyncMDNUser,
      httpAuthAsyncMDNPassword,
      useHttpAuthAsyncMDN,
      ...otherData
    } = data;

    const backendData = {
      ...otherData,
      authenticationCredentialsMessage: {
        authMode: authModeMessage,
        user: httpAuthMessageUser || '',
        password: httpAuthMessagePassword || '',
        enabled: useHttpAuthMessage
      },
      authenticationCredentialsAsyncMDN: {
        authMode: authModeAsyncMDN,
        user: httpAuthAsyncMDNUser || '',
        password: httpAuthAsyncMDNPassword || '',
        enabled: useHttpAuthAsyncMDN
      }
    };

    try {
      let savedPartnerId;
      if (isEdit) {
        // Use database ID for update - property name is 'dbid' (all lowercase)
        const dbId = partner.dbid || partner.dbId || partner.id || partner.DBId;

        if (!dbId) {
          toast.error('Failed to update: Database ID not found');
          return;
        }

        await updatePartner.mutateAsync({ id: dbId, partner: backendData });
        savedPartnerId = dbId;
        toast.success('Partner updated successfully');
      } else {
        const result = await createPartner.mutateAsync(backendData);
        savedPartnerId = result?.dbId || result?.id || result?.dbid;
        toast.success('Partner created successfully');
      }

      // Save visibility settings (only for remote partners)
      if (!localStation && savedPartnerId) {
        try {
          await api.put(`/partners/${savedPartnerId}/visibility`, {
            visibleToAll: visibilityMode === 'all',
            userIds: visibilityMode === 'specific' ? selectedUserIds : []
          });
        } catch (error) {
          toast.error('Partner saved, but failed to update visibility settings');
        }
      }

      onSuccess?.();
      onClose();
    } catch (error) {
      toast.error('Failed to save partner: ' + (error.response?.data?.error || error.message));
    }
  };

  // Fetch users list when visibility tab is active
  useEffect(() => {
    const fetchUsers = async () => {
      if (activeTab === 'visibility' && !localStation) {
        setLoadingUsers(true);
        try {
          const response = await api.get('/users');
          setUsers(response.data || []);
        } catch (error) {
          console.error('Failed to fetch users:', error);
          toast.error('Failed to load users list');
        } finally {
          setLoadingUsers(false);
        }
      }
    };
    fetchUsers();
  }, [activeTab, localStation, toast]);

  // Load visibility settings when editing existing partner
  useEffect(() => {
    const fetchVisibility = async () => {
      if (partner && partner.dbId && !localStation) {
        try {
          const response = await api.get(`/partners/${partner.dbId}/visibility`);
          const data = response.data;

          if (data.visibleToAll) {
            setVisibilityMode('all');
            setSelectedUserIds([]);
          } else {
            setVisibilityMode('specific');
            setSelectedUserIds(data.visibleToUserIds || []);
          }
        } catch (error) {
          console.error('Failed to fetch visibility settings:', error);
          // Default to visible to all
          setVisibilityMode('all');
          setSelectedUserIds([]);
        }
      }
    };
    fetchVisibility();
  }, [partner, localStation]);

  // Define all tabs, but filter based on localStation
  const allTabs = [
    { id: 'general', label: 'General', showForLocal: true, showForRemote: true },
    { id: 'security', label: 'Security', showForLocal: true, showForRemote: true },
    { id: 'send', label: 'Send', showForLocal: false, showForRemote: true },
    { id: 'mdn', label: 'MDN', showForLocal: true, showForRemote: true },
    { id: 'dirpoll', label: 'Directory Poll', showForLocal: false, showForRemote: true },
    { id: 'httpauth', label: 'HTTP Authentication', showForLocal: false, showForRemote: true },
    { id: 'visibility', label: 'Visibility', showForLocal: false, showForRemote: true },
    { id: 'contact', label: 'Contact', showForLocal: true, showForRemote: true }
  ];

  // Filter tabs based on localStation value
  const tabs = allTabs.filter(tab =>
    localStation ? tab.showForLocal : tab.showForRemote
  );

  const tabStyle = (active) => ({
    padding: '0.75rem 1rem',
    backgroundColor: active ? '#007bff' : '#f8f9fa',
    color: active ? 'white' : '#495057',
    border: 'none',
    cursor: 'pointer',
    fontSize: '0.875rem',
    borderRadius: '4px 4px 0 0',
    marginRight: '2px'
  });

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

  const checkboxContainerStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem'
  };

  const errorStyle = {
    color: '#dc3545',
    fontSize: '0.875rem',
    marginTop: '0.25rem'
  };

  const selectionCardStyle = {
    border: '2px solid #dee2e6',
    borderRadius: '8px',
    padding: '2rem',
    cursor: 'pointer',
    textAlign: 'center',
    transition: 'all 0.2s',
    backgroundColor: 'white'
  };

  const selectionCardHoverStyle = {
    borderColor: '#007bff',
    backgroundColor: '#f8f9ff'
  };

  // Step 1: For new partners, show partner type selection
  if (!isEdit && localStationType === null) {
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
          padding: '2rem'
        }} onClick={(e) => e.stopPropagation()}>

          <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
            <h2 style={{ margin: '0 0 0.5rem 0' }}>Add Partner</h2>
            <p style={{ color: '#6c757d', margin: 0 }}>Select partner type to continue</p>
          </div>

          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '1.5rem', marginBottom: '2rem' }}>
            <div
              style={selectionCardStyle}
              onMouseEnter={(e) => {
                e.currentTarget.style.borderColor = '#007bff';
                e.currentTarget.style.backgroundColor = '#f8f9ff';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.borderColor = '#dee2e6';
                e.currentTarget.style.backgroundColor = 'white';
              }}
              onClick={() => handleLocalStationSelection(true)}
            >
              <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🏠</div>
              <h3 style={{ margin: '0 0 0.5rem 0' }}>Local Station</h3>
              <p style={{ color: '#6c757d', fontSize: '0.875rem', margin: 0 }}>
                Your local AS2 server configuration
              </p>
            </div>

            <div
              style={selectionCardStyle}
              onMouseEnter={(e) => {
                e.currentTarget.style.borderColor = '#007bff';
                e.currentTarget.style.backgroundColor = '#f8f9ff';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.borderColor = '#dee2e6';
                e.currentTarget.style.backgroundColor = 'white';
              }}
              onClick={() => handleLocalStationSelection(false)}
            >
              <div style={{ fontSize: '3rem', marginBottom: '1rem' }}>🌐</div>
              <h3 style={{ margin: '0 0 0.5rem 0' }}>Remote Partner</h3>
              <p style={{ color: '#6c757d', fontSize: '0.875rem', margin: 0 }}>
                External trading partner configuration
              </p>
            </div>
          </div>

          <div style={{ display: 'flex', justifyContent: 'center' }}>
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
              Cancel
            </button>
          </div>
        </div>
      </div>
    );
  }

  // Step 2: Show the full form with tabs
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
        maxWidth: '800px',
        width: '100%',
        maxHeight: '90vh',
        display: 'flex',
        flexDirection: 'column'
      }} onClick={(e) => e.stopPropagation()}>

        {/* Header */}
        <div style={{
          padding: '1.5rem',
          borderBottom: '1px solid #dee2e6',
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center'
        }}>
          <div>
            <h2 style={{ margin: 0 }}>
              {isEdit ? 'Edit Partner' : `Add ${localStation ? 'Local Station' : 'Remote Partner'}`}
            </h2>
            {!isEdit && (
              <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.25rem' }}>
                {localStation ? '🏠 Local Station' : '🌐 Remote Partner'}
              </div>
            )}
            {isEdit && (
              <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.25rem' }}>
                {partner?.localStation ? '🏠 Local Station' : '🌐 Remote Partner'}
              </div>
            )}
          </div>
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

        {/* Tabs */}
        <div style={{
          padding: '0 1.5rem',
          borderBottom: '1px solid #dee2e6',
          display: 'flex',
          gap: '2px',
          overflowX: 'auto'
        }}>
          {tabs.map(tab => (
            <button
              key={tab.id}
              type="button"
              style={tabStyle(activeTab === tab.id)}
              onClick={() => setActiveTab(tab.id)}
            >
              {tab.label}
            </button>
          ))}
        </div>

        {/* Form Content */}
        <form onSubmit={handleSubmit(onSubmit)} style={{ display: 'flex', flexDirection: 'column', flex: 1, overflow: 'hidden' }}>
          <div style={{ padding: '1.5rem', overflowY: 'auto', flex: 1 }}>

            {/* General Tab */}
            {activeTab === 'general' && (
              <>
                <div style={formGroupStyle}>
                  <label style={labelStyle}>Name *</label>
                  <input type="text" {...register('name')} style={inputStyle} disabled={isSubmitting} />
                  {errors.name && <div style={errorStyle}>{errors.name.message}</div>}
                </div>

                <div style={formGroupStyle}>
                  <label style={labelStyle}>AS2 Identification *</label>
                  <input
                    type="text"
                    {...register('as2Identification')}
                    style={inputStyle}
                    disabled={isSubmitting}
                  />
                  {errors.as2Identification && <div style={errorStyle}>{errors.as2Identification.message}</div>}
                </div>

                <div style={formGroupStyle}>
                  <label style={labelStyle}>Comment</label>
                  <textarea {...register('comment')} style={{...inputStyle, minHeight: '80px'}} disabled={isSubmitting} />
                </div>
              </>
            )}

            {/* Send Tab - Only visible for remote partners (includes HTTP settings) */}
            {activeTab === 'send' && !localStation && (
              <>
                <div style={formGroupStyle}>
                  <label style={labelStyle}>URL</label>
                  <input type="text" {...register('url')} placeholder="http://partner.com:8080/as2/HttpReceiver" style={inputStyle} disabled={isSubmitting} />
                  {errors.url && <div style={errorStyle}>{errors.url.message}</div>}
                </div>

                <div style={formGroupStyle}>
                  <label style={labelStyle}>Subject</label>
                  <input type="text" {...register('subject')} style={inputStyle} disabled={isSubmitting} />
                </div>

                <div style={formGroupStyle}>
                  <label style={labelStyle}>Content Type</label>
                  <input type="text" {...register('contentType')} style={inputStyle} disabled={isSubmitting} />
                </div>

                <div style={formGroupStyle}>
                  <label style={labelStyle}>Email</label>
                  <input type="email" {...register('email')} style={inputStyle} disabled={isSubmitting || localStation} />
                </div>

                <div style={formGroupStyle}>
                  <label style={labelStyle}>Compression</label>
                  <select {...register('compressionType', { setValueAs: v => Number(v) })} style={inputStyle} disabled={isSubmitting}>
                    <option value="0">None</option>
                    <option value="1">ZLIB</option>
                  </select>
                </div>

                {/* HTTP Settings Section */}
                <div style={{ marginTop: '2rem', marginBottom: '1rem', paddingTop: '1rem', borderTop: '1px solid #dee2e6' }}>
                  <h3 style={{ fontSize: '1rem', marginBottom: '1rem', color: '#495057' }}>HTTP Protocol Settings</h3>
                </div>

                <div style={formGroupStyle}>
                  <label style={labelStyle}>HTTP Protocol Version</label>
                  <select {...register('httpProtocolVersion')} style={inputStyle} disabled={isSubmitting}>
                    <option value="1.1">HTTP/1.1</option>
                    <option value="1.0">HTTP/1.0</option>
                  </select>
                </div>

                <div style={formGroupStyle}>
                  <label style={labelStyle}>Content Transfer Encoding</label>
                  <select {...register('contentTransferEncoding', { setValueAs: v => Number(v) })} style={inputStyle} disabled={isSubmitting}>
                    <option value="0">Binary</option>
                    <option value="1">Base64</option>
                    <option value="2">Quoted-Printable</option>
                    <option value="3">8bit</option>
                    <option value="4">7bit</option>
                  </select>
                </div>
              </>
            )}

            {/* MDN Tab */}
            {activeTab === 'mdn' && (
              <>
                <div style={formGroupStyle}>
                  <label style={labelStyle}>MDN URL</label>
                  <input
                    type="text"
                    {...register('mdnURL')}
                    placeholder="http://partner.com:8080/as2/HttpReceiver"
                    style={inputStyle}
                    disabled={isSubmitting}
                  />
                  {errors.mdnURL && <div style={errorStyle}>{errors.mdnURL.message}</div>}
                  {localStation && (
                    <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.5rem' }}>
                      For local station: Use your server's public URL (not localhost) if accessible from internet
                    </div>
                  )}
                </div>

                <div style={formGroupStyle}>
                  <div style={checkboxContainerStyle}>
                    <input type="checkbox" {...register('syncMDN')} disabled={isSubmitting} />
                    <label style={{...labelStyle, marginBottom: 0}}>Synchronous MDN</label>
                  </div>
                  <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.5rem' }}>
                    Request MDN in the same HTTP response
                  </div>
                </div>

                <div style={formGroupStyle}>
                  <div style={checkboxContainerStyle}>
                    <input type="checkbox" {...register('signedMDN')} disabled={isSubmitting} />
                    <label style={{...labelStyle, marginBottom: 0}}>Signed MDN</label>
                  </div>
                  <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.5rem' }}>
                    Request digitally signed MDN
                  </div>
                </div>

                <div style={formGroupStyle}>
                  <div style={checkboxContainerStyle}>
                    <input type="checkbox" {...register('useAlgorithmIdentifierProtectionAttribute')} disabled={isSubmitting} />
                    <label style={{...labelStyle, marginBottom: 0}}>Use Algorithm Identifier Protection Attribute</label>
                  </div>
                  <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.5rem' }}>
                    Recommended (RFC 6211)
                  </div>
                </div>
              </>
            )}

            {/* Security Tab */}
            {activeTab === 'security' && (
              <>
                {localStation ? (
                  // Local Station: Show certificate selection
                  <>
                    <div style={formGroupStyle}>
                      <label style={labelStyle}>Signature Certificate (Local)</label>
                      <select
                        {...register('signFingerprintSHA1')}
                        style={inputStyle}
                        disabled={isSubmitting || certsLoading}
                      >
                        <option value="">-- Select Certificate --</option>
                        {certificates?.filter(cert => cert.isKeyPair).map((cert) => (
                          <option key={cert.fingerPrintSHA1} value={cert.fingerPrintSHA1}>
                            🔑 {cert.alias} - {cert.subjectDN || 'No DN'}
                          </option>
                        ))}
                      </select>
                      <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.5rem' }}>
                        Private key used to sign outgoing messages
                      </div>
                    </div>

                    <div style={formGroupStyle}>
                      <label style={labelStyle}>Encryption Certificate (Local)</label>
                      <select
                        {...register('cryptFingerprintSHA1')}
                        style={inputStyle}
                        disabled={isSubmitting || certsLoading}
                      >
                        <option value="">-- Select Certificate --</option>
                        {certificates?.filter(cert => cert.isKeyPair).map((cert) => (
                          <option key={cert.fingerPrintSHA1} value={cert.fingerPrintSHA1}>
                            🔑 {cert.alias} - {cert.subjectDN || 'No DN'}
                          </option>
                        ))}
                      </select>
                      <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.5rem' }}>
                        Private key used to decrypt incoming messages
                      </div>
                    </div>
                  </>
                ) : (
                  // Remote Partner: 5 fields matching Swing UI
                  <>
                    {/* 1. Partner certificate (Outbound data encryption) */}
                    <div style={formGroupStyle}>
                      <label style={labelStyle}>Partner Certificate (Outbound Data Encryption)</label>
                      <select
                        {...register('cryptFingerprintSHA1')}
                        style={inputStyle}
                        disabled={isSubmitting || certsLoading}
                      >
                        <option value="">-- Select Certificate --</option>
                        {certificates?.map((cert) => {
                          const icon = cert.isKeyPair ? '🔑' : '📜';
                          return (
                            <option key={cert.fingerPrintSHA1} value={cert.fingerPrintSHA1}>
                              {icon} {cert.alias} - {cert.subjectDN || 'No DN'}
                            </option>
                          );
                        })}
                      </select>
                      <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.5rem' }}>
                        Partner's public certificate to encrypt outgoing messages
                      </div>
                    </div>

                    {/* 2. Partner certificate (Inbound signature verification) */}
                    <div style={formGroupStyle}>
                      <label style={labelStyle}>Partner Certificate (Inbound Signature Verification)</label>
                      <select
                        {...register('signFingerprintSHA1')}
                        style={inputStyle}
                        disabled={isSubmitting || certsLoading}
                      >
                        <option value="">-- Select Certificate --</option>
                        {certificates?.map((cert) => {
                          const icon = cert.isKeyPair ? '🔑' : '📜';
                          return (
                            <option key={cert.fingerPrintSHA1} value={cert.fingerPrintSHA1}>
                              {icon} {cert.alias} - {cert.subjectDN || 'No DN'}
                            </option>
                          );
                        })}
                      </select>
                      <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.5rem' }}>
                        Partner's public certificate to verify incoming message signatures
                      </div>
                    </div>

                    {/* 3. Digital signature algorithm */}
                    <div style={formGroupStyle}>
                      <label style={labelStyle}>Digital Signature Algorithm</label>
                      <select {...register('signType', { setValueAs: v => Number(v) })} style={inputStyle} disabled={isSubmitting}>
                        <option value="0">None</option>
                        <option value="11">MD5</option>
                        <option value="12">SHA-1</option>
                        <option value="13">SHA-256</option>
                        <option value="14">SHA-384</option>
                        <option value="15">SHA-512</option>
                        <option value="16">SHA-1 (RSASSA-PSS)</option>
                        <option value="17">SHA-256 (RSASSA-PSS)</option>
                        <option value="18">SHA-384 (RSASSA-PSS)</option>
                        <option value="19">SHA-512 (RSASSA-PSS)</option>
                        <option value="20">SHA3-224</option>
                        <option value="21">SHA3-256</option>
                        <option value="22">SHA3-384</option>
                        <option value="23">SHA3-512</option>
                        <option value="24">SHA3-224 (RSASSA-PSS)</option>
                        <option value="25">SHA3-256 (RSASSA-PSS)</option>
                        <option value="26">SHA3-384 (RSASSA-PSS)</option>
                        <option value="27">SHA3-512 (RSASSA-PSS)</option>
                      </select>
                    </div>

                    {/* 4. Message encryption algorithm */}
                    <div style={formGroupStyle}>
                      <label style={labelStyle}>Message Encryption Algorithm</label>
                      <select {...register('encryptionType', { setValueAs: v => Number(v) })} style={inputStyle} disabled={isSubmitting}>
                        <option value="0">None</option>
                        <option value="1">3DES</option>
                        <option value="5">AES-128-CBC</option>
                        <option value="6">AES-192-CBC</option>
                        <option value="7">AES-256-CBC</option>
                        <option value="2">AES-128-CCM</option>
                        <option value="3">AES-192-CCM</option>
                        <option value="4">AES-256-CCM</option>
                        <option value="8">AES-128-GCM</option>
                        <option value="9">AES-192-GCM</option>
                        <option value="10">AES-256-GCM</option>
                        <option value="30">AES-128-CBC (RSAES-OAEP)</option>
                        <option value="31">AES-192-CBC (RSAES-OAEP)</option>
                        <option value="32">AES-256-CBC (RSAES-OAEP)</option>
                        <option value="33">AES-128-GCM (RSAES-OAEP)</option>
                        <option value="34">AES-192-GCM (RSAES-OAEP)</option>
                        <option value="35">AES-256-GCM (RSAES-OAEP)</option>
                        <option value="40">Camellia-128-CBC</option>
                        <option value="41">Camellia-192-CBC</option>
                        <option value="42">Camellia-256-CBC</option>
                        <option value="50">ChaCha20-Poly1305</option>
                        <option value="60">DES</option>
                        <option value="70">RC2-40</option>
                        <option value="71">RC2-64</option>
                        <option value="72">RC2-128</option>
                        <option value="73">RC2-196</option>
                        <option value="80">RC4-40</option>
                        <option value="81">RC4-56</option>
                        <option value="82">RC4-128</option>
                      </select>
                    </div>

                    {/* 5. Algorithm Identifier Protection Attribute */}
                    <div style={formGroupStyle}>
                      <div style={checkboxContainerStyle}>
                        <input type="checkbox" {...register('useAlgorithmIdentifierProtectionAttribute')} disabled={isSubmitting} />
                        <label style={{...labelStyle, marginBottom: 0}}>Algorithm Identifier Protection Attribute</label>
                      </div>
                      <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.5rem' }}>
                        RFC 6211 - Protects the algorithm identifier from modification
                      </div>
                    </div>
                  </>
                )}
              </>
            )}

            {/* Directory Poll Tab - Only visible for remote partners */}
            {activeTab === 'dirpoll' && !localStation && (
              <>
                <div style={formGroupStyle}>
                  <div style={checkboxContainerStyle}>
                    <input type="checkbox" {...register('enableDirPoll')} disabled={isSubmitting} />
                    <label style={{...labelStyle, marginBottom: 0}}>Enable Directory Polling</label>
                  </div>
                </div>

                <div style={formGroupStyle}>
                  <label style={labelStyle}>Poll Interval (seconds)</label>
                  <input type="number" {...register('pollInterval', { valueAsNumber: true })} style={inputStyle} disabled={isSubmitting || !enableDirPoll} min="1" />
                </div>

                <div style={formGroupStyle}>
                  <label style={labelStyle}>Max Files Per Poll</label>
                  <input type="number" {...register('maxPollFiles', { valueAsNumber: true })} style={inputStyle} disabled={isSubmitting || !enableDirPoll} min="1" />
                </div>

                <div style={formGroupStyle}>
                  <label style={labelStyle}>Poll Ignore List (comma-separated patterns)</label>
                  <input type="text" {...register('pollIgnoreListAsString')} placeholder="*.tmp,*.bak" style={inputStyle} disabled={isSubmitting || !enableDirPoll} />
                  <div style={{ fontSize: '0.875rem', color: '#6c757d', marginTop: '0.5rem' }}>
                    File patterns to ignore during directory polling
                  </div>
                </div>

                <div style={formGroupStyle}>
                  <div style={checkboxContainerStyle}>
                    <input type="checkbox" {...register('keepFilenameOnReceipt')} disabled={isSubmitting} />
                    <label style={{...labelStyle, marginBottom: 0}}>Keep Original Filename on Receipt</label>
                  </div>
                </div>
              </>
            )}

            {/* HTTP Authentication Tab - Only visible for remote partners */}
            {activeTab === 'httpauth' && !localStation && (
              <>
                <div style={{ marginBottom: '2rem' }}>
                  <h3 style={{ fontSize: '1rem', marginBottom: '0.5rem', color: '#495057' }}>HTTP Authentication Settings</h3>
                  <p style={{ fontSize: '0.875rem', color: '#6c757d', marginBottom: 0 }}>
                    Configure HTTP basic authentication for message transmission and async MDN delivery
                  </p>
                </div>

                {/* Message Authentication Section */}
                <div style={{ marginBottom: '2rem', paddingBottom: '1.5rem', borderBottom: '1px solid #dee2e6' }}>
                  <h4 style={{ fontSize: '0.95rem', marginBottom: '1rem', color: '#495057' }}>Message Transmission Authentication</h4>

                  <div style={formGroupStyle}>
                    <label style={labelStyle}>Authentication Method</label>
                    <select
                      {...register('authModeMessage')}
                      style={inputStyle}
                      disabled={isSubmitting}
                    >
                      <option value="0">No HTTP Authentication</option>
                      <option value="1">Basic Auth (credentials in this form)</option>
                      <option value="2">Use User Preference</option>
                    </select>
                  </div>

                  {watch('authModeMessage') === "1" && (
                    <>
                      <div style={formGroupStyle}>
                        <label style={labelStyle}>Username</label>
                        <input
                          type="text"
                          {...register('httpAuthMessageUser')}
                          placeholder="HTTP authentication username"
                          style={inputStyle}
                          disabled={isSubmitting}
                        />
                      </div>

                      <div style={formGroupStyle}>
                        <label style={labelStyle}>Password</label>
                        <input
                          type="password"
                          {...register('httpAuthMessagePassword')}
                          placeholder="HTTP authentication password"
                          style={inputStyle}
                          disabled={isSubmitting}
                        />
                      </div>
                    </>
                  )}
                </div>

                {/* Async MDN Authentication Section */}
                <div style={{ marginBottom: '1rem' }}>
                  <h4 style={{ fontSize: '0.95rem', marginBottom: '1rem', color: '#495057' }}>Async MDN Authentication</h4>

                  <div style={formGroupStyle}>
                    <label style={labelStyle}>Authentication Method</label>
                    <select
                      {...register('authModeAsyncMDN')}
                      style={inputStyle}
                      disabled={isSubmitting}
                    >
                      <option value="0">No HTTP Authentication</option>
                      <option value="1">Basic Auth (credentials in this form)</option>
                      <option value="2">Use User Preference</option>
                    </select>
                  </div>

                  {watch('authModeAsyncMDN') === "1" && (
                    <>
                      <div style={formGroupStyle}>
                        <label style={labelStyle}>Username</label>
                        <input
                          type="text"
                          {...register('httpAuthAsyncMDNUser')}
                          placeholder="HTTP authentication username for async MDN"
                          style={inputStyle}
                          disabled={isSubmitting}
                        />
                      </div>

                      <div style={formGroupStyle}>
                        <label style={labelStyle}>Password</label>
                        <input
                          type="password"
                          {...register('httpAuthAsyncMDNPassword')}
                          placeholder="HTTP authentication password for async MDN"
                          style={inputStyle}
                          disabled={isSubmitting}
                        />
                      </div>
                    </>
                  )}
                </div>
              </>
            )}

            {/* Contact Tab */}
            {activeTab === 'contact' && (
              <>
                <div style={formGroupStyle}>
                  <label style={labelStyle}>AS2 Contact</label>
                  <input type="text" {...register('contactAS2')} placeholder="Name or email of AS2 administrator" style={inputStyle} disabled={isSubmitting} />
                </div>

                <div style={formGroupStyle}>
                  <label style={labelStyle}>Company Contact</label>
                  <input type="text" {...register('contactCompany')} placeholder="Company name" style={inputStyle} disabled={isSubmitting} />
                </div>
              </>
            )}

            {/* Visibility Tab */}
            {activeTab === 'visibility' && (
              <>
                <h3 style={{ marginBottom: '1rem', fontSize: '1.1rem' }}>Partner Visibility</h3>
                <p style={{ marginBottom: '1rem', color: '#666', fontSize: '0.875rem' }}>
                  Control which WebUI users can see and use this partner when sending messages.
                </p>

                <div style={formGroupStyle}>
                  <label style={labelStyle}>
                    <input
                      type="radio"
                      checked={visibilityMode === 'all'}
                      onChange={() => {
                        setVisibilityMode('all');
                        setSelectedUserIds([]);
                      }}
                      style={{ marginRight: '0.5rem' }}
                    />
                    Visible to all WebUI users
                  </label>
                </div>

                <div style={formGroupStyle}>
                  <label style={labelStyle}>
                    <input
                      type="radio"
                      checked={visibilityMode === 'specific'}
                      onChange={() => setVisibilityMode('specific')}
                      style={{ marginRight: '0.5rem' }}
                    />
                    Visible to specific users only
                  </label>
                </div>

                {visibilityMode === 'specific' && (
                  <div style={{ marginTop: '1rem', padding: '1rem', backgroundColor: '#f8f9fa', borderRadius: '4px' }}>
                    <label style={{ ...labelStyle, marginBottom: '0.75rem', display: 'block' }}>
                      Select Users Who Can See This Partner:
                    </label>
                    {loadingUsers ? (
                      <p style={{ color: '#666', fontSize: '0.875rem' }}>Loading users...</p>
                    ) : (
                      <div style={{
                        maxHeight: '300px',
                        overflowY: 'auto',
                        border: '1px solid #dee2e6',
                        borderRadius: '4px',
                        padding: '0.5rem',
                        backgroundColor: 'white'
                      }}>
                        {users.length === 0 ? (
                          <p style={{ color: '#666', fontSize: '0.875rem', padding: '0.5rem' }}>
                            No users available
                          </p>
                        ) : (
                          users.map(user => (
                            <div key={user.id} style={{ padding: '0.5rem', borderBottom: '1px solid #f0f0f0' }}>
                              <label style={{ display: 'flex', alignItems: 'center', cursor: 'pointer' }}>
                                <input
                                  type="checkbox"
                                  checked={selectedUserIds.includes(user.id)}
                                  onChange={(e) => {
                                    if (e.target.checked) {
                                      setSelectedUserIds([...selectedUserIds, user.id]);
                                    } else {
                                      setSelectedUserIds(selectedUserIds.filter(id => id !== user.id));
                                    }
                                  }}
                                  style={{ marginRight: '0.75rem' }}
                                />
                                <div>
                                  <div style={{ fontWeight: '600', fontSize: '0.875rem' }}>{user.username}</div>
                                  {user.fullName && (
                                    <div style={{ color: '#666', fontSize: '0.75rem' }}>{user.fullName}</div>
                                  )}
                                  {user.email && (
                                    <div style={{ color: '#999', fontSize: '0.75rem' }}>{user.email}</div>
                                  )}
                                </div>
                              </label>
                            </div>
                          ))
                        )}
                      </div>
                    )}
                    {visibilityMode === 'specific' && selectedUserIds.length === 0 && (
                      <p style={{ color: '#dc3545', fontSize: '0.875rem', marginTop: '0.5rem' }}>
                        ⚠️ Warning: No users selected. Partner will not be visible to anyone when sending messages.
                      </p>
                    )}
                  </div>
                )}
              </>
            )}
          </div>

          {/* Footer Buttons */}
          <div style={{
            padding: '1rem 1.5rem',
            borderTop: '1px solid #dee2e6',
            display: 'flex',
            justifyContent: 'flex-end',
            gap: '0.5rem'
          }}>
            <button
              type="button"
              onClick={onClose}
              style={{
                padding: '0.5rem 1rem',
                backgroundColor: '#6c757d',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: 'pointer'
              }}
            >
              Cancel
            </button>
            <button
              type="submit"
              disabled={isSubmitting}
              style={{
                padding: '0.5rem 1rem',
                backgroundColor: '#007bff',
                color: 'white',
                border: 'none',
                borderRadius: '4px',
                cursor: isSubmitting ? 'not-allowed' : 'pointer',
                opacity: isSubmitting ? 0.6 : 1
              }}
            >
              {isSubmitting ? 'Saving...' : isEdit ? 'Update Partner' : 'Create Partner'}
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}
