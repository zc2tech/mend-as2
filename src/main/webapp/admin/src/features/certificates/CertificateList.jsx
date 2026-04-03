import { useState, useEffect } from 'react';
import { useCertificates, useExportCertificate, useDeleteCertificate, useExportKeystore, useGenerateCSR, useVerifyCRL } from './useCertificates';
import { useToast } from '../../components/Toast';
import { LoadingPage } from '../../components/Loading';
import CertificateImport from './CertificateImport';
import GenerateKeyDialog from './GenerateKeyDialog';

export default function CertificateList() {
  const [keystoreType, setKeystoreType] = useState('sign');
  const [showImport, setShowImport] = useState(false);
  const [showGenerateKey, setShowGenerateKey] = useState(false);
  const [showExportMenu, setShowExportMenu] = useState(false);
  const [showToolsMenu, setShowToolsMenu] = useState(false);
  const { data: certificates, isLoading, error } = useCertificates(keystoreType);
  const exportCertificate = useExportCertificate();
  const exportKeystore = useExportKeystore();
  const generateCSR = useGenerateCSR();
  const verifyCRL = useVerifyCRL();
  const deleteCertificate = useDeleteCertificate();
  const toast = useToast();

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

  const handleExportKeystore = async (format) => {
    try {
      const blob = await exportKeystore.mutateAsync({ keystoreType, format });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      const ext = format === 'PKCS12' ? 'p12' : 'jks';
      a.download = `${keystoreType}_keystore.${ext}`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success(`Keystore exported successfully`);
      setShowExportMenu(false);
    } catch (error) {
      toast.error('Failed to export keystore: ' + (error.response?.data?.error || error.message));
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
        keystoreType
      });
      // The response contains csrBase64, decode and download it
      const csrData = atob(response.csrBase64);
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

  const handleDelete = async (alias, certName) => {
    if (!window.confirm(`Are you sure you want to delete certificate "${alias}"?`)) {
      return;
    }

    try {
      await deleteCertificate.mutateAsync({ alias, keystoreType });
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

  const tabStyle = (active) => ({
    padding: '0.75rem 1.5rem',
    backgroundColor: active ? '#007bff' : '#e9ecef',
    color: active ? 'white' : '#495057',
    border: 'none',
    cursor: 'pointer',
    fontSize: '1rem',
    marginRight: '0.5rem',
    borderRadius: '4px 4px 0 0'
  });

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
        <h1 style={{ margin: 0 }}>Certificates</h1>
        <div style={{ display: 'flex', gap: '0.5rem' }}>
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
                  onClick={() => handleExportKeystore('PKCS12')}
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
                  onClick={() => handleExportKeystore('JKS')}
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

          <button
            style={{
              ...buttonStyle,
              backgroundColor: '#28a745',
              color: 'white',
              padding: '0.5rem 1rem'
            }}
            onClick={() => setShowImport(true)}
          >
            Import Certificate
          </button>
        </div>
      </div>

      <div style={{ marginBottom: '1rem' }}>
        <button
          style={tabStyle(keystoreType === 'sign')}
          onClick={() => setKeystoreType('sign')}
        >
          Sign/Encrypt Certificates
        </button>
        <button
          style={tabStyle(keystoreType === 'tls')}
          onClick={() => setKeystoreType('tls')}
        >
          TLS Certificates
        </button>
      </div>

      <table style={tableStyle}>
        <colgroup>
          <col style={{ width: '5%' }} />
          <col style={{ width: '10%' }} />
          <col style={{ width: '22%' }} />
          <col style={{ width: '22%' }} />
          <col style={{ width: '10%' }} />
          <col style={{ width: '15%' }} />
          <col style={{ width: '16%' }} />
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
          {!certificates || certificates.length === 0 ? (
            <tr>
              <td colSpan="7" style={{ ...tdStyle, textAlign: 'center', padding: '2rem' }}>
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
                <td style={tdStyle}>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '0.25rem' }}>
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
                          cursor: 'pointer',
                          fontSize: '0.75rem',
                          backgroundColor: '#dc3545',
                          color: 'white'
                        }}
                        onClick={() => handleDelete(cert.alias, cert.subjectDN)}
                        disabled={deleteCertificate.isPending}
                        title="Delete this certificate"
                      >
                        Delete
                      </button>
                    </div>
                  </div>
                </td>
              </tr>
              );
            })
          )}
        </tbody>
      </table>

      {showImport && (
        <CertificateImport
          keystoreType={keystoreType}
          onClose={() => setShowImport(false)}
        />
      )}

      {showGenerateKey && (
        <GenerateKeyDialog
          keystoreType={keystoreType}
          onClose={() => setShowGenerateKey(false)}
          onSuccess={() => {
            // Refresh the certificate list
          }}
        />
      )}
    </div>
  );
}
