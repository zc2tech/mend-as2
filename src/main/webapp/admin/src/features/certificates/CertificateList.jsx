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
import { useCertificates, useAllUsersCertificates, useExportCertificate, useDeleteCertificate, useExportKeystore, useGenerateCSR, useVerifyCRL } from './useCertificates';
import { useToast } from '../../components/Toast';
import { LoadingPage } from '../../components/Loading';
import CertificateImport from './CertificateImport';
import CertificateImportTypeSelector from './CertificateImportTypeSelector';
import GenerateKeyDialog from './GenerateKeyDialog';
import { useAuth } from '../auth/useAuth';
import { PERMISSIONS } from '../../constants/permissions';
import api from '../../api/client';

export default function CertificateList() {
  const [ownershipFilter, setOwnershipFilter] = useState('mine'); // Changed default from 'all' to 'mine'
  const [showImportTypeSelector, setShowImportTypeSelector] = useState(false);
  const [showImport, setShowImport] = useState(false);
  const [importType, setImportType] = useState('keystore');
  const [showGenerateKey, setShowGenerateKey] = useState(false);
  const [showExportMenu, setShowExportMenu] = useState(false);
  const [showToolsMenu, setShowToolsMenu] = useState(false);
  const [showPasswordDialog, setShowPasswordDialog] = useState(false);
  const [exportFormat, setExportFormat] = useState(null);
  const [exportPassword, setExportPassword] = useState('');

  const { hasPermission, user } = useAuth();
  const isAdmin = hasPermission(PERMISSIONS.USER_MANAGE);

  // Conditionally fetch certificates based on filter selection
  const shouldFetchAllUsers = isAdmin && ownershipFilter === 'all';

  // Hook for single user certificates - hardcoded to 'sign' keystore only
  const {
    data: myCertificates,
    isLoading: isLoadingMine,
    error: errorMine
  } = useCertificates('sign');

  // Hook for all users' certificates (only runs when admin AND filter is 'all')
  const {
    data: allUsersCertificates,
    isLoading: isLoadingAll,
    error: errorAll
  } = useAllUsersCertificates('sign');

  // Select the appropriate data source
  const certificates = shouldFetchAllUsers ? allUsersCertificates : myCertificates;
  const isLoading = shouldFetchAllUsers ? isLoadingAll : isLoadingMine;
  const error = shouldFetchAllUsers ? errorAll : errorMine;

  const exportCertificate = useExportCertificate();
  const exportKeystore = useExportKeystore();
  const generateCSR = useGenerateCSR();
  const verifyCRL = useVerifyCRL();
  const deleteCertificate = useDeleteCertificate();
  const toast = useToast();

  // Check permissions - only Sign/Encrypt keystore
  const hasSignReadPermission = hasPermission(PERMISSIONS.CERT_READ);
  const hasSignWritePermission = hasPermission(PERMISSIONS.CERT_WRITE);

  // Use sign keystore permissions (no TLS support)
  const canRead = hasSignReadPermission;
  const canWrite = hasSignWritePermission;

  const handleImportClick = () => {
    setShowImportTypeSelector(true);
  };

  const handleImportTypeSelected = (type) => {
    setImportType(type);
    setShowImportTypeSelector(false);
    setShowImport(true);
  };

  const handleImportClose = () => {
    setShowImport(false);
    setImportType('keystore');
  };

  // Close dropdowns when clicking outside
  useEffect(() => {
    const handleClickOutside = () => {
      setShowExportMenu(false);
      setShowToolsMenu(false);
    };

    if (showExportMenu || showToolsMenu) {
      document.addEventListener('click', handleClickOutside);
      return () => document.removeEventListener('click', handleClickOutside);
    }
  }, [showExportMenu, showToolsMenu]);

  const handleExport = async (alias, format) => {
    // Find the certificate to get its fingerprint
    const cert = certificates.find(c => c.alias === alias);
    if (!cert) {
      toast.error('Certificate not found');
      console.error('Certificate not found for alias:', alias);
      return;
    }

    // Check all possible fingerprint field names (case variations)
    const fingerprint = cert.fingerprintSHA1 || cert.fingerprintsha1 ||
                        cert.fingerPrintSHA1 || cert.fingerprintSha1;

    if (!fingerprint) {
      toast.error('Certificate fingerprint not found');
      console.error('Certificate data:', cert);
      console.error('Available fields:', Object.keys(cert));
      return;
    }

    try {
      const blob = await exportCertificate.mutateAsync({
        fingerprintSHA1: fingerprint,
        format,
        keystoreType: 'sign'
      });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      const ext = format === 'PKCS12' ? 'p12' : (format === 'DER' ? 'der' : 'pem');
      a.download = `${alias}.${ext}`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success(`Certificate "${alias}" exported successfully`);
    } catch (error) {
      toast.error('Failed to export certificate: ' + (error.response?.data?.error || error.message));
    }
  };

  const handleExportKeystoreClick = (format) => {
    setExportFormat(format);
    setExportPassword('');
    setShowExportMenu(false);
    setShowPasswordDialog(true);
  };

  const handleExportKeystore = async () => {
    if (!exportPassword) {
      toast.error('Please enter a password');
      return;
    }

    try {
      const blob = await exportKeystore.mutateAsync({
        keystoreType: 'sign',
        format: exportFormat,
        password: exportPassword
      });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      const ext = exportFormat === 'PKCS12' ? 'p12' : 'jks';
      a.download = `sign_keystore.${ext}`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success(`Keystore exported successfully`);
      setShowPasswordDialog(false);
      setExportPassword('');
    } catch (error) {
      toast.error('Failed to export keystore: ' + (error.response?.data?.error || error.message));
    }
  };

  const handleExportAllPublicCertificates = async () => {
    try {
      const response = await api.post('/certificates/export-all-public-pem',
        { keystoreType: 'sign' },
        { responseType: 'blob' }
      );

      const blob = response.data;
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `sign_public_certificates.zip`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success('Public certificates exported successfully');
      setShowExportMenu(false);
    } catch (error) {
      toast.error('Failed to export public certificates: ' + (error.response?.data?.error || error.message));
    }
  };

  const handleGenerateCSR = async (alias) => {
    // Find the certificate to get its fingerprint
    const cert = certificates.find(c => c.alias === alias);
    if (!cert) {
      toast.error('Certificate not found');
      console.error('Certificate not found for alias:', alias);
      return;
    }

    // Check all possible fingerprint field names (case variations)
    const fingerprint = cert.fingerprintSHA1 || cert.fingerprintsha1 ||
                        cert.fingerPrintSHA1 || cert.fingerprintSha1;

    if (!fingerprint) {
      toast.error('Certificate fingerprint not found');
      console.error('Certificate data:', cert);
      console.error('Available fields:', Object.keys(cert));
      return;
    }

    try {
      const response = await generateCSR.mutateAsync({
        fingerprintSHA1: fingerprint,
        keystoreType: 'sign'
      });
      // The response contains csrBase64 in PEM format (already text with headers)
      // No need to decode - it's already in the correct format
      const csrData = response.csrBase64;
      const blob = new Blob([csrData], { type: 'text/plain' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${alias}.csr`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success(`CSR generated for "${alias}"`);
    } catch (error) {
      toast.error('Failed to generate CSR: ' + (error.response?.data?.error || error.message));
    }
  };

  const handleVerifyCertificates = async () => {
    try {
      const response = await verifyCRL.mutateAsync({ keystoreType: 'sign' });
      const revokedCerts = response.revocationInfo?.filter(info => info.isRevoked) || [];
      if (revokedCerts.length === 0) {
        toast.success('All certificates are valid');
      } else {
        toast.warning(`${revokedCerts.length} certificate(s) are revoked`);
      }
      setShowToolsMenu(false);
    } catch (error) {
      toast.error('Failed to verify certificates: ' + (error.response?.data?.error || error.message));
    }
  };

  const handleDelete = async (alias, certName) => {
    if (!window.confirm(`Are you sure you want to delete certificate "${alias}"?`)) {
      return;
    }

    try {
      await deleteCertificate.mutateAsync({ alias, keystoreType: 'sign' });
      toast.success(`Certificate "${alias}" deleted successfully`);
    } catch (error) {
      // Check if certificate is in use
      if (error.response?.status === 409 && error.response?.data?.partnersUsing) {
        const partners = error.response.data.partnersUsing;
        const partnerList = partners.join('\n');
        const forceDelete = window.confirm(
          `Certificate "${alias}" is in use by the following partners:\n\n${partnerList}\n\nAre you sure you want to delete it? This may break partner communication.`
        );

        if (forceDelete) {
          try {
            await deleteCertificate.mutateAsync({ alias, keystoreType: 'sign', force: true });
            toast.success(`Certificate "${alias}" deleted successfully`);
          } catch (forceError) {
            toast.error('Failed to delete certificate: ' + (forceError.response?.data?.error || forceError.message));
          }
        }
      } else {
        toast.error('Failed to delete certificate: ' + (error.response?.data?.error || error.message));
      }
    }
  };

  if (isLoading) {
    return <LoadingPage message="Loading certificates..." />;
  }

  if (error) {
    return <div style={{ color: 'red' }}>Error loading certificates: {error.message}</div>;
  }

  const tableStyle = {
    width: '100%',
    borderCollapse: 'collapse',
    backgroundColor: 'white',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    overflow: 'hidden',
    tableLayout: 'fixed'
  };

  const thStyle = {
    textAlign: 'left',
    padding: '1rem',
    backgroundColor: '#f8f9fa',
    borderBottom: '2px solid #dee2e6',
    fontWeight: '600'
  };

  const tdStyle = {
    padding: '0.75rem 1rem',
    borderBottom: '1px solid #dee2e6',
    fontSize: '0.875rem',
    wordWrap: 'break-word',
    overflowWrap: 'break-word'
  };

  const buttonStyle = {
    padding: '0.375rem 0.75rem',
    marginRight: '0.5rem',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.875rem'
  };

  return (
    <div>
      <div style={{ marginBottom: '1.5rem', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
        <h1 style={{ margin: 0 }}>
          My Sign/Crypt/Auth
        </h1>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
          {/* Export Menu - read-only operation */}
          {canRead && (
            <div style={{ position: 'relative' }}>
              <button
                style={{
                  ...buttonStyle,
                  backgroundColor: '#17a2b8',
                  color: 'white',
                  padding: '0.5rem 1rem'
                }}
                onClick={(e) => {
                  e.stopPropagation();
                  setShowExportMenu(!showExportMenu);
                  setShowToolsMenu(false);
                }}
              >
                Export ▼
              </button>
              {showExportMenu && (
                <div
                  style={{
                    position: 'absolute',
                    top: '100%',
                    right: 0,
                    backgroundColor: 'white',
                    border: '1px solid #dee2e6',
                    borderRadius: '4px',
                    boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                    minWidth: '200px',
                    zIndex: 1000,
                    marginTop: '4px'
                  }}
                  onClick={(e) => e.stopPropagation()}
                >
                  <button
                    style={{
                      width: '100%',
                      padding: '0.75rem 1rem',
                      border: 'none',
                      backgroundColor: 'transparent',
                      textAlign: 'left',
                      cursor: 'pointer',
                      fontSize: '0.875rem'
                    }}
                    onMouseEnter={(e) => e.target.style.backgroundColor = '#f8f9fa'}
                    onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                    onClick={() => handleExportKeystoreClick('PKCS12')}
                  >
                    Export All Keystore (PKCS#12)
                  </button>
                  <button
                    style={{
                      width: '100%',
                      padding: '0.75rem 1rem',
                      border: 'none',
                      backgroundColor: 'transparent',
                      textAlign: 'left',
                      cursor: 'pointer',
                      fontSize: '0.875rem'
                    }}
                    onMouseEnter={(e) => e.target.style.backgroundColor = '#f8f9fa'}
                    onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                    onClick={() => handleExportKeystoreClick('JKS')}
                  >
                    Export All Keystore (JKS)
                  </button>
                  <button
                    style={{
                      width: '100%',
                      padding: '0.75rem 1rem',
                      border: 'none',
                      backgroundColor: 'transparent',
                      textAlign: 'left',
                      cursor: 'pointer',
                      fontSize: '0.875rem'
                    }}
                    onMouseEnter={(e) => e.target.style.backgroundColor = '#f8f9fa'}
                    onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                    onClick={() => handleExportAllPublicCertificates()}
                  >
                    Export All Public Certificates (PEM)
                  </button>
                </div>
              )}
            </div>
          )}

          {/* Tools Menu - requires write permission for Generate Key */}
          {canWrite && (
            <div style={{ position: 'relative' }}>
              <button
                style={{
                  ...buttonStyle,
                  backgroundColor: '#6c757d',
                  color: 'white',
                  padding: '0.5rem 1rem'
                }}
                onClick={(e) => {
                  e.stopPropagation();
                  setShowToolsMenu(!showToolsMenu);
                  setShowExportMenu(false);
                }}
              >
                Tools ▼
              </button>
              {showToolsMenu && (
                <div
                  style={{
                    position: 'absolute',
                    top: '100%',
                    right: 0,
                    backgroundColor: 'white',
                    border: '1px solid #dee2e6',
                    borderRadius: '4px',
                    boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
                    minWidth: '200px',
                    zIndex: 1000,
                    marginTop: '4px'
                  }}
                  onClick={(e) => e.stopPropagation()}
                >
                  <button
                    style={{
                      width: '100%',
                      padding: '0.75rem 1rem',
                      border: 'none',
                      backgroundColor: 'transparent',
                      textAlign: 'left',
                      cursor: 'pointer',
                      fontSize: '0.875rem'
                    }}
                    onMouseEnter={(e) => e.target.style.backgroundColor = '#f8f9fa'}
                    onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                    onClick={() => {
                      setShowGenerateKey(true);
                      setShowToolsMenu(false);
                    }}
                  >
                    Generate Key...
                  </button>
                  <div style={{ borderTop: '1px solid #dee2e6', margin: '0.25rem 0' }}></div>
                  <button
                    style={{
                      width: '100%',
                      padding: '0.75rem 1rem',
                      border: 'none',
                      backgroundColor: 'transparent',
                      textAlign: 'left',
                      cursor: 'pointer',
                      fontSize: '0.875rem'
                    }}
                    onMouseEnter={(e) => e.target.style.backgroundColor = '#f8f9fa'}
                    onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                    onClick={handleVerifyCertificates}
                  >
                    Verify All Certificates
                  </button>
                </div>
              )}
            </div>
          )}

          {/* Import Button - requires write permission */}
          {canWrite && (
            <button
              style={{
                ...buttonStyle,
                backgroundColor: '#28a745',
                color: 'white',
                padding: '0.5rem 1rem'
              }}
              onClick={handleImportClick}
            >
              Import Certificate
            </button>
          )}
        </div>
      </div>

      {/* Ownership Filter - only show for admin users */}
      {isAdmin && (
        <div style={{ marginBottom: '1rem', display: 'flex', justifyContent: 'flex-end', alignItems: 'center' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
            <span style={{ fontSize: '0.875rem', color: '#6c757d' }}>Show:</span>
            <select
              value={ownershipFilter}
              onChange={(e) => setOwnershipFilter(e.target.value)}
              style={{
                padding: '0.375rem 0.75rem',
                border: '1px solid #dee2e6',
                borderRadius: '4px',
                fontSize: '0.875rem',
                cursor: 'pointer'
              }}
            >
              <option value="all">All User Certificates</option>
              <option value="mine">My Certificates</option>
            </select>
          </div>
        </div>
      )}

      <table style={tableStyle}>
        <colgroup>
          <col style={{ width: '5%' }} />
          <col style={{ width: '10%' }} />
          <col style={{ width: shouldFetchAllUsers ? '17%' : '19%' }} />
          <col style={{ width: shouldFetchAllUsers ? '17%' : '19%' }} />
          <col style={{ width: '7%' }} />
          <col style={{ width: '12%' }} />
          {shouldFetchAllUsers && <col style={{ width: '8%' }} />}
          <col style={{ width: shouldFetchAllUsers ? '16%' : '18%' }} />
        </colgroup>
        <thead>
          <tr>
            <th style={thStyle}>Type</th>
            <th style={thStyle}>Alias</th>
            <th style={thStyle}>Subject DN</th>
            <th style={thStyle}>Issuer</th>
            <th style={thStyle}>Valid Until</th>
            <th style={thStyle}>Fingerprint (SHA-1)</th>
            {shouldFetchAllUsers && <th style={thStyle}>Owner</th>}
            <th style={thStyle}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {!certificates || certificates.length === 0 ? (
            <tr>
              <td colSpan={shouldFetchAllUsers ? "8" : "7"} style={{ ...tdStyle, textAlign: 'center', padding: '2rem' }}>
                No certificates found in this keystore
              </td>
            </tr>
          ) : (
            certificates.map((cert, index) => {
              const fingerprint = cert.fingerprintSHA1 || cert.fingerprintsha1 ||
                                  cert.fingerPrintSHA1 || cert.fingerprintSha1;
              const isExpired = cert.notAfter && new Date(cert.notAfter) < new Date();
              return (
              <tr key={cert.alias || index}>
                <td style={{...tdStyle, textAlign: 'center', fontSize: '1.25rem'}}>
                  <span title={cert.isKeyPair ? "Private Key (can sign/decrypt)" : cert.isRootCertificate ? "Root Certificate" : "Public Certificate"}>
                    {cert.isKeyPair ? '🔑' : cert.isRootCertificate ? '📋' : '📜'}
                  </span>
                </td>
                <td style={tdStyle}>{cert.alias || '-'}</td>
                <td style={tdStyle}>{cert.subjectDN || '-'}</td>
                <td style={tdStyle}>{cert.issuerDN || '-'}</td>
                <td style={{
                  ...tdStyle,
                  color: isExpired ? '#dc3545' : 'inherit',
                  fontWeight: isExpired ? '600' : 'normal'
                }}>
                  {cert.notAfter ? new Date(cert.notAfter).toLocaleDateString() : '-'}
                  {isExpired && ' ⚠️'}
                </td>
                <td style={tdStyle}>
                  <code style={{ fontSize: '0.75rem' }}>
                    {fingerprint || '-'}
                  </code>
                </td>
                {shouldFetchAllUsers && (
                  <td style={tdStyle}>
                    <span style={{
                      display: 'inline-block',
                      padding: '0.25rem 0.5rem',
                      backgroundColor: '#007bff',
                      color: 'white',
                      borderRadius: '4px',
                      fontSize: '0.75rem',
                      fontWeight: '500'
                    }}>
                      {cert.username || `User ${cert.userId}`}
                    </span>
                  </td>
                )}
                <td style={tdStyle}>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
                    {canRead && (
                      <div style={{ display: 'flex', gap: '0.25rem' }}>
                        <button
                          style={{
                            padding: '0.25rem 0.5rem',
                            border: 'none',
                            borderRadius: '4px',
                            cursor: 'pointer',
                            fontSize: '0.75rem',
                            backgroundColor: '#17a2b8',
                            color: 'white'
                          }}
                          onClick={() => handleExport(cert.alias, 'PEM')}
                          disabled={exportCertificate.isPending}
                          title="Export certificate in PEM format"
                        >
                          PEM
                        </button>
                        <button
                          style={{
                            padding: '0.25rem 0.5rem',
                            border: 'none',
                            borderRadius: '4px',
                            cursor: cert.isKeyPair ? 'pointer' : 'not-allowed',
                            fontSize: '0.75rem',
                            backgroundColor: cert.isKeyPair ? '#17a2b8' : '#6c757d',
                            color: 'white',
                            opacity: cert.isKeyPair ? 1 : 0.6
                          }}
                          onClick={() => cert.isKeyPair && handleExport(cert.alias, 'PKCS12')}
                          disabled={exportCertificate.isPending || !cert.isKeyPair}
                          title={cert.isKeyPair ? "Export certificate and private key in PKCS#12 format" : "PKCS#12 export requires a private key"}
                        >
                          PKCS#12
                        </button>
                      </div>
                    )}
                    {canWrite && (
                      <div style={{ display: 'flex', gap: '0.25rem' }}>
                        <button
                          style={{
                            padding: '0.25rem 0.5rem',
                            border: 'none',
                            borderRadius: '4px',
                            cursor: cert.isKeyPair ? 'pointer' : 'not-allowed',
                            fontSize: '0.75rem',
                            backgroundColor: cert.isKeyPair ? '#6c757d' : '#adb5bd',
                            color: 'white',
                            opacity: cert.isKeyPair ? 1 : 0.6
                          }}
                          onClick={() => cert.isKeyPair && handleGenerateCSR(cert.alias)}
                          disabled={generateCSR.isPending || !cert.isKeyPair}
                          title={cert.isKeyPair ? "Generate Certificate Signing Request" : "CSR generation requires a private key"}
                        >
                          CSR
                        </button>
                        <button
                          style={{
                            padding: '0.25rem 0.5rem',
                            border: 'none',
                            borderRadius: '4px',
                            cursor: cert.canDelete ? 'pointer' : 'not-allowed',
                            fontSize: '0.75rem',
                            backgroundColor: cert.canDelete ? '#dc3545' : '#6c757d',
                            color: 'white',
                            opacity: cert.canDelete ? 1 : 0.6
                          }}
                          onClick={() => cert.canDelete && handleDelete(cert.alias, cert.subjectDN)}
                          disabled={deleteCertificate.isPending || !cert.canDelete}
                          title={
                            !cert.canDelete
                              ? cert.userId !== user?.id
                                ? `Cannot delete: Certificate belongs to ${cert.username || 'another user'}`
                                : cert.partnersUsing && cert.partnersUsing.length > 0
                                  ? `Cannot delete: Used by partners: ${cert.partnersUsing.join(', ')}`
                                  : "Cannot delete this certificate"
                              : "Delete this certificate"
                          }
                        >
                          Delete
                        </button>
                      </div>
                    )}
                    {!canRead && !canWrite && (
                      <span style={{ fontSize: '0.75rem', color: '#6c757d' }}>No permission</span>
                    )}
                  </div>
                </td>
              </tr>
              );
            })
          )}
        </tbody>
      </table>

      {showImportTypeSelector && (
        <CertificateImportTypeSelector
          keystoreType="sign"
          onClose={() => setShowImportTypeSelector(false)}
          onTypeSelected={handleImportTypeSelected}
        />
      )}

      {showImport && (
        <CertificateImport
          keystoreType="sign"
          importType={importType}
          onClose={handleImportClose}
        />
      )}

      {showGenerateKey && (
        <GenerateKeyDialog
          keystoreType="sign"
          onClose={() => setShowGenerateKey(false)}
          onSuccess={() => {
            // Refresh the certificate list
          }}
        />
      )}

      {showPasswordDialog && (
        <div
          style={{
            position: 'fixed',
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            zIndex: 1000
          }}
          onClick={() => {
            setShowPasswordDialog(false);
            setExportPassword('');
          }}
        >
          <div
            style={{
              backgroundColor: 'white',
              borderRadius: '8px',
              padding: '2rem',
              maxWidth: '400px',
              width: '90%'
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <h2 style={{ marginTop: 0, marginBottom: '1.5rem' }}>Export Keystore</h2>
            <p style={{ marginBottom: '1rem', color: '#6c757d' }}>
              Enter a password to protect the exported keystore file.
            </p>
            <div style={{ marginBottom: '1.5rem' }}>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>
                Password:
              </label>
              <input
                type="password"
                value={exportPassword}
                onChange={(e) => setExportPassword(e.target.value)}
                onKeyPress={(e) => {
                  if (e.key === 'Enter' && exportPassword) {
                    handleExportKeystore();
                  }
                }}
                style={{
                  width: '100%',
                  padding: '0.5rem',
                  border: '1px solid #dee2e6',
                  borderRadius: '4px',
                  fontSize: '1rem'
                }}
                placeholder="Enter password"
                autoFocus
              />
            </div>
            <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.5rem' }}>
              <button
                onClick={() => {
                  setShowPasswordDialog(false);
                  setExportPassword('');
                }}
                style={{
                  padding: '0.5rem 1rem',
                  border: '1px solid #dee2e6',
                  borderRadius: '4px',
                  backgroundColor: 'white',
                  cursor: 'pointer'
                }}
              >
                Cancel
              </button>
              <button
                onClick={handleExportKeystore}
                disabled={!exportPassword}
                style={{
                  padding: '0.5rem 1rem',
                  border: 'none',
                  borderRadius: '4px',
                  backgroundColor: exportPassword ? '#007bff' : '#6c757d',
                  color: 'white',
                  cursor: exportPassword ? 'pointer' : 'not-allowed'
                }}
              >
                Export
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
