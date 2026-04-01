import { useState } from 'react';
import { useCertificates, useExportCertificate, useDeleteCertificate } from './useCertificates';
import { useToast } from '../../components/Toast';
import { LoadingPage } from '../../components/Loading';
import CertificateImport from './CertificateImport';

export default function CertificateList() {
  const [keystoreType, setKeystoreType] = useState('sign');
  const [showImport, setShowImport] = useState(false);
  const { data: certificates, isLoading, error } = useCertificates(keystoreType);
  const exportCertificate = useExportCertificate();
  const deleteCertificate = useDeleteCertificate();
  const toast = useToast();

  const handleExport = async (alias, format) => {
    try {
      const blob = await exportCertificate.mutateAsync({ alias, format, keystoreType });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `${alias}.${format.toLowerCase()}`;
      document.body.appendChild(a);
      a.click();
      window.URL.revokeObjectURL(url);
      document.body.removeChild(a);
      toast.success(`Certificate "${alias}" exported successfully`);
    } catch (error) {
      toast.error('Failed to export certificate: ' + (error.response?.data?.error || error.message));
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
    overflow: 'hidden'
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
    fontSize: '0.875rem'
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
        <thead>
          <tr>
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
              <td colSpan="6" style={{ ...tdStyle, textAlign: 'center', padding: '2rem' }}>
                No certificates found in this keystore
              </td>
            </tr>
          ) : (
            certificates.map((cert, index) => (
              <tr key={cert.alias || index}>
                <td style={tdStyle}>{cert.alias || '-'}</td>
                <td style={tdStyle}>{cert.subjectDN || '-'}</td>
                <td style={tdStyle}>{cert.issuerDN || '-'}</td>
                <td style={tdStyle}>
                  {cert.notAfter ? new Date(cert.notAfter).toLocaleDateString() : '-'}
                </td>
                <td style={tdStyle}>
                  <code style={{ fontSize: '0.75rem' }}>
                    {cert.fingerprintSHA1 || '-'}
                  </code>
                </td>
                <td style={tdStyle}>
                  <button
                    style={{
                      ...buttonStyle,
                      backgroundColor: '#17a2b8',
                      color: 'white'
                    }}
                    onClick={() => handleExport(cert.alias, 'PEM')}
                    disabled={exportCertificate.isPending}
                  >
                    Export
                  </button>
                  <button
                    style={{
                      ...buttonStyle,
                      backgroundColor: '#6c757d',
                      color: 'white'
                    }}
                    onClick={() => alert('Generate CSR coming soon')}
                  >
                    CSR
                  </button>
                  <button
                    style={{
                      ...buttonStyle,
                      backgroundColor: '#dc3545',
                      color: 'white',
                      marginRight: 0
                    }}
                    onClick={() => handleDelete(cert.alias, cert.subjectDN)}
                    disabled={deleteCertificate.isPending}
                  >
                    Delete
                  </button>
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>

      {showImport && (
        <CertificateImport
          keystoreType={keystoreType}
          onClose={() => setShowImport(false)}
        />
      )}
    </div>
  );
}
