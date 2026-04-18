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
import { useQuery, useQueryClient } from '@tanstack/react-query';
import api from '../../api/client';
import { useToast } from '../../components/Toast';
import { useAuth } from '../auth/useAuth';
import { LoadingPage } from '../../components/Loading';
import {
  useDeleteCertificate,
  useExportCertificate,
  useExportKeystore,
  useVerifyCRL,
  useGenerateCSR
} from '../certificates/useCertificates';
import CertificateImportTypeSelector from '../certificates/CertificateImportTypeSelector';
import CertificateImport from '../certificates/CertificateImport';
import GenerateKeyDialog from '../certificates/GenerateKeyDialog';

// Custom hook for system-wide TLS certificates (no user filtering)
function useSystemTLSCertificates() {
  return useQuery({
    queryKey: ['certificates', 'system-tls'],
    queryFn: async () => {
      // Don't pass visibleToUser parameter for system-wide certificates
      const response = await api.get('/certificates', {
        params: {
          keystoreType: 'ssl'
          // No visibleToUser parameter - returns system-wide certs
        }
      });
      return response.data;
    }
  });
}

export default function SystemTLS() {
  const { hasPermission } = useAuth();
  const toast = useToast();
  const queryClient = useQueryClient();
  const [showImportTypeSelector, setShowImportTypeSelector] = useState(false);
  const [showImport, setShowImport] = useState(false);
  const [importType, setImportType] = useState(null);
  const [showGenerateKey, setShowGenerateKey] = useState(false);
  const [showExportMenu, setShowExportMenu] = useState(false);
  const [showToolsMenu, setShowToolsMenu] = useState(false);
  const [showPasswordDialog, setShowPasswordDialog] = useState(false);
  const [exportFormat, setExportFormat] = useState(null);
  const [exportPassword, setExportPassword] = useState('');

  // Use 'ssl' keystoreType which maps to system-wide TLS/SSL certificates
  const keystoreType = 'ssl';
  const { data: certificates, isLoading, error } = useSystemTLSCertificates();
  const exportCertificate = useExportCertificate();
  const exportKeystore = useExportKeystore();
  const deleteCertificate = useDeleteCertificate();
  const verifyCRL = useVerifyCRL();
  const generateCSR = useGenerateCSR();

  // Check permissions - READ for viewing, WRITE for modifying
  const hasTLSReadPermission = hasPermission('CERT_TLS_READ');
  const hasTLSWritePermission = hasPermission('CERT_TLS_WRITE');

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

  if (!hasTLSReadPermission) {
    return (
      <div style={{
        padding: '2rem',
        backgroundColor: '#fff3cd',
        border: '1px solid #ffc107',
        borderRadius: '8px',
        marginTop: '1rem'
      }}>
        <p style={{ margin: 0, color: '#856404' }}>
          You do not have permission to view system TLS certificates.
          Please contact your administrator.
        </p>
      </div>
    );
  }

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
    setImportType(null);
    // Invalidate system TLS certificates query to refresh the list
    queryClient.invalidateQueries(['certificates', 'system-tls']);
  };

  const handleExport = async (alias, format) => {
    // Find the certificate to get its fingerprint
    const cert = certificates.find(c => c.alias === alias);
    if (!cert) {
      toast.error('Certificate not found');
      return;
    }

    const fingerprint = cert.fingerprintSHA1 || cert.fingerprintsha1 ||
                        cert.fingerPrintSHA1 || cert.fingerprintSha1;

    if (!fingerprint) {
      toast.error('Certificate fingerprint not found');
      return;
    }

    try {
      const blob = await exportCertificate.mutateAsync({
        fingerprintSHA1: fingerprint,
        format,
        keystoreType
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
        keystoreType,
        format: exportFormat,
        password: exportPassword
      });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      const ext = exportFormat === 'PKCS12' ? 'p12' : 'jks';
      a.download = `${keystoreType}_keystore.${ext}`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success('Keystore exported successfully');
      setShowPasswordDialog(false);
      setExportPassword('');
    } catch (error) {
      toast.error('Failed to export keystore: ' + (error.response?.data?.error || error.message));
    }
  };

  const handleGenerateCSR = async (fingerprint) => {
    try {
      const response = await generateCSR.mutateAsync({
        fingerprintSHA1: fingerprint,
        keystoreType
      });
      const csrData = response.csrBase64 || response.crmfTLSBase64;
      if (!csrData) {
        throw new Error('No CSR data returned');
      }
      // CSR is already in PEM format (text with headers), don't decode
      const blob = new Blob([csrData], { type: 'application/pkcs10' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'certificate.csr';
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success('CSR generated successfully');
      setShowToolsMenu(false);
    } catch (error) {
      toast.error('Failed to generate CSR: ' + (error.response?.data?.error || error.message));
    }
  };

  const handleVerifyCertificates = async () => {
    try {
      const response = await verifyCRL.mutateAsync({ keystoreType });
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

  const handleDelete = async (alias) => {
    if (!window.confirm(`Are you sure you want to delete certificate "${alias}"?`)) {
      return;
    }

    try {
      await deleteCertificate.mutateAsync({ alias, keystoreType });
      toast.success(`Certificate "${alias}" deleted successfully`);
    } catch (error) {
      if (error.response?.status === 409) {
        const inUseData = error.response.data;
        const forceDelete = window.confirm(
          `${inUseData.message}\n\nPartners using this certificate:\n${inUseData.partnersUsing.join('\n')}\n\nDo you want to force delete anyway?`
        );
        if (forceDelete) {
          try {
            await deleteCertificate.mutateAsync({ alias, keystoreType, force: true });
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

  const formatDate = (dateString) => {
    if (!dateString) return 'N/A';
    try {
      return new Date(dateString).toLocaleString();
    } catch (e) {
      return dateString;
    }
  };

  const formatSubjectDN = (dn) => {
    if (!dn) return 'N/A';
    // Extract CN if present
    const cnMatch = dn.match(/CN=([^,]+)/);
    return cnMatch ? cnMatch[1] : dn;
  };

  const buttonStyle = {
    padding: '0.375rem 0.75rem',
    marginRight: '0.5rem',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.875rem'
  };

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

  return (
    <div>
      <div style={{
        padding: '1rem',
        backgroundColor: '#e7f3ff',
        border: '1px solid #b3d9ff',
        borderRadius: '8px',
        marginBottom: '1rem'
      }}>
        <p style={{ margin: 0, color: '#004085' }}>
          <strong>System-wide HTTPS Server Certificates</strong><br/>
          These certificates are used for HTTPS connections to the AS2 server.
          They are system-wide and apply to all users. The Jetty web server uses these
          certificates to present itself to incoming HTTPS clients.
        </p>
      </div>

      {!hasTLSWritePermission && (
        <div style={{
          padding: '1rem',
          backgroundColor: '#fff3cd',
          border: '1px solid #ffc107',
          borderRadius: '8px',
          marginBottom: '1rem'
        }}>
          <p style={{ margin: 0, color: '#856404' }}>
            <strong>⚠️ Read-Only Mode:</strong> You have permission to view certificates but not to modify them.
            Import, Generate, and Delete operations are disabled. Contact your administrator if you need write access.
          </p>
        </div>
      )}

      <div style={{ marginBottom: '1.5rem', display: 'flex', justifyContent: 'flex-end', alignItems: 'center', gap: '0.5rem' }}>
        {/* Export Menu */}
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
                Export Keystore (PKCS#12)
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
                Export Keystore (JKS)
              </button>
            </div>
          )}
        </div>

        {/* Tools Menu */}
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
                  cursor: hasTLSWritePermission ? 'pointer' : 'not-allowed',
                  fontSize: '0.875rem',
                  opacity: hasTLSWritePermission ? 1 : 0.6
                }}
                onMouseEnter={(e) => hasTLSWritePermission && (e.target.style.backgroundColor = '#f8f9fa')}
                onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                onClick={() => hasTLSWritePermission && setShowGenerateKey(true)}
                disabled={!hasTLSWritePermission}
              >
                Generate New Key
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
                onClick={handleVerifyCertificates}
              >
                Verify Certificates (CRL)
              </button>
            </div>
          )}
        </div>

        {/* Import Button */}
        <button
          style={{
            ...buttonStyle,
            backgroundColor: hasTLSWritePermission ? '#28a745' : '#6c757d',
            color: 'white',
            padding: '0.5rem 1rem',
            opacity: hasTLSWritePermission ? 1 : 0.6,
            cursor: hasTLSWritePermission ? 'pointer' : 'not-allowed'
          }}
          onClick={handleImportClick}
          disabled={!hasTLSWritePermission}
        >
          Import Certificate
        </button>
      </div>

      <table style={tableStyle}>
        <colgroup>
          <col style={{ width: '5%' }} />
          <col style={{ width: '10%' }} />
          <col style={{ width: '20%' }} />
          <col style={{ width: '20%' }} />
          <col style={{ width: '7%' }} />
          <col style={{ width: '15%' }} />
          <col style={{ width: '23%' }} />
        </colgroup>
        <thead>
          <tr>
            <th style={thStyle}>Type</th>
            <th style={thStyle}>Alias</th>
            <th style={thStyle}>Subject DN</th>
            <th style={thStyle}>Issuer</th>
            <th style={thStyle}>Valid Until</th>
            <th style={thStyle}>Fingerprint (SHA-1)</th>
            <th style={thStyle}>Actions</th>
          </tr>
        </thead>
        <tbody>
          {certificates && certificates.length > 0 ? (
            certificates.map((cert, index) => {
              const fingerprint = cert.fingerprintSHA1 || cert.fingerprintsha1 ||
                                 cert.fingerPrintSHA1 || cert.fingerprintSha1;
              const isExpired = cert.notAfter && new Date(cert.notAfter) < new Date();
              return (
                <tr key={index}>
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
                  <td style={tdStyle}>
                    <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
                      {hasTLSReadPermission && (
                        <div style={{ display: 'flex', gap: '0.25rem' }}>
                          <button
                            style={{
                              padding: '0.25rem 0.5rem',
                              border: 'none',
                              borderRadius: '4px',
                              cursor: 'pointer',
                              fontSize: '0.75rem',
                              backgroundColor: '#17a2b8',
                              color: 'white',
                              margin: 0
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
                              opacity: cert.isKeyPair ? 1 : 0.6,
                              margin: 0
                            }}
                            onClick={() => cert.isKeyPair && handleExport(cert.alias, 'PKCS12')}
                            disabled={exportCertificate.isPending || !cert.isKeyPair}
                            title={cert.isKeyPair ? "Export certificate and private key in PKCS#12 format" : "PKCS#12 export requires a private key"}
                          >
                            PKCS#12
                          </button>
                        </div>
                      )}
                      {hasTLSWritePermission && (
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
                              opacity: cert.isKeyPair ? 1 : 0.6,
                              margin: 0
                            }}
                            onClick={() => cert.isKeyPair && handleGenerateCSR(fingerprint)}
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
                              cursor: 'pointer',
                              fontSize: '0.75rem',
                              backgroundColor: '#dc3545',
                              color: 'white',
                              margin: 0
                            }}
                            onClick={() => handleDelete(cert.alias)}
                            disabled={deleteCertificate.isPending}
                            title="Delete this certificate"
                          >
                            Delete
                          </button>
                        </div>
                      )}
                    </div>
                  </td>
                </tr>
              );
            })
          ) : (
            <tr>
              <td colSpan="7" style={{ ...tdStyle, textAlign: 'center', padding: '2rem', color: '#6c757d' }}>
                No certificates found. Import or generate a certificate to get started.
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {/* Export Keystore Password Dialog */}
      {showPasswordDialog && (
        <div style={{
          position: 'fixed',
          top: 0,
          left: 0,
          right: 0,
          bottom: 0,
          backgroundColor: 'rgba(0,0,0,0.5)',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          zIndex: 1000
        }}>
          <div style={{
            backgroundColor: 'white',
            padding: '2rem',
            borderRadius: '8px',
            minWidth: '400px',
            boxShadow: '0 4px 12px rgba(0,0,0,0.3)'
          }}>
            <h3 style={{ marginTop: 0 }}>Export Keystore</h3>
            <div style={{ marginBottom: '1rem' }}>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>
                Format: {exportFormat === 'PKCS12' ? 'PKCS#12 (.p12)' : 'JKS (.jks)'}
              </label>
            </div>
            <div style={{ marginBottom: '1.5rem' }}>
              <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>Password:</label>
              <input
                type="password"
                value={exportPassword}
                onChange={(e) => setExportPassword(e.target.value)}
                style={{
                  width: '100%',
                  padding: '0.5rem',
                  border: '1px solid #dee2e6',
                  borderRadius: '4px',
                  fontSize: '0.875rem'
                }}
                placeholder="Enter password for keystore"
                onKeyPress={(e) => {
                  if (e.key === 'Enter') {
                    handleExportKeystore();
                  }
                }}
              />
            </div>
            <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
              <button
                style={{
                  ...buttonStyle,
                  backgroundColor: '#6c757d',
                  color: 'white',
                  padding: '0.5rem 1rem',
                  margin: 0
                }}
                onClick={() => {
                  setShowPasswordDialog(false);
                  setExportPassword('');
                }}
              >
                Cancel
              </button>
              <button
                style={{
                  ...buttonStyle,
                  backgroundColor: '#28a745',
                  color: 'white',
                  padding: '0.5rem 1rem',
                  margin: 0
                }}
                onClick={handleExportKeystore}
              >
                Export
              </button>
            </div>
          </div>
        </div>
      )}

      {showImportTypeSelector && (
        <CertificateImportTypeSelector
          keystoreType={keystoreType}
          onClose={() => setShowImportTypeSelector(false)}
          onTypeSelected={handleImportTypeSelected}
        />
      )}

      {showImport && (
        <CertificateImport
          keystoreType={keystoreType}
          importType={importType}
          onClose={handleImportClose}
        />
      )}

      {showGenerateKey && (
        <GenerateKeyDialog
          keystoreType={keystoreType}
          onClose={() => setShowGenerateKey(false)}
          onSuccess={() => {
            setShowGenerateKey(false);
            queryClient.invalidateQueries(['certificates', 'system-tls']);
            toast.success('Key pair generated successfully');
          }}
        />
      )}
    </div>
  );
}
