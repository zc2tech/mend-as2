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

export default function CertificateImportTypeSelector({ keystoreType, onClose, onTypeSelected }) {
  const [selectedType, setSelectedType] = useState('certificate');

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

  const handleContinue = () => {
    onTypeSelected(selectedType);
  };

  const typeOptions = [
    {
      value: 'certificate',
      icon: '📜',
      title: 'Import Certificate',
      subtitle: 'from your trading partner',
      description: 'Import a standalone certificate file (.cer, .crt, .pem) without a private key. Use this for partner certificates that you receive for encryption and signature verification.'
    },
    {
      value: 'keystore',
      icon: '🔑',
      title: 'Import Your Own Private Key',
      subtitle: 'from keystore PKCS#12, JKS',
      description: 'Import a keystore file (.p12, .pfx, .jks) containing your private key and certificate chain. Use this for your own encryption and signing certificates.'
    }
  ];

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
        padding: '2rem',
        borderRadius: '8px',
        maxWidth: '600px',
        width: '100%',
        maxHeight: '90vh',
        overflow: 'auto'
      }} onClick={(e) => e.stopPropagation()}>
        <h2 style={{ marginTop: 0, marginBottom: '0.5rem' }}>Import Certificate</h2>
        <p style={{ color: '#666', fontSize: '0.875rem', marginBottom: '1.5rem' }}>
          Importing to: Sign/Encrypt Keystore
        </p>

        <p style={{ fontWeight: '600', marginBottom: '1rem' }}>What would you like to import?</p>

        <div style={{ marginBottom: '1.5rem' }}>
          {typeOptions.map((option) => (
            <div
              key={option.value}
              onClick={() => setSelectedType(option.value)}
              style={{
                border: selectedType === option.value ? '2px solid #007bff' : '2px solid #ddd',
                borderRadius: '8px',
                padding: '1rem',
                marginBottom: '1rem',
                cursor: 'pointer',
                backgroundColor: selectedType === option.value ? '#f0f8ff' : 'white',
                transition: 'all 0.2s'
              }}
            >
              <div style={{ display: 'flex', alignItems: 'start', gap: '1rem' }}>
                <div style={{
                  fontSize: '2rem',
                  flexShrink: 0,
                  width: '40px',
                  textAlign: 'center'
                }}>
                  {option.icon}
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontWeight: '600', marginBottom: '0.25rem' }}>
                    {option.title}
                  </div>
                  <div style={{ fontSize: '0.875rem', color: '#666', marginBottom: '0.5rem' }}>
                    {option.subtitle}
                  </div>
                  <div style={{ fontSize: '0.875rem', color: '#666', lineHeight: '1.4' }}>
                    {option.description}
                  </div>
                </div>
                <div style={{
                  width: '20px',
                  height: '20px',
                  borderRadius: '50%',
                  border: selectedType === option.value ? '6px solid #007bff' : '2px solid #ccc',
                  flexShrink: 0,
                  marginTop: '0.25rem'
                }} />
              </div>
            </div>
          ))}
        </div>

        <div style={{ display: 'flex', gap: '0.5rem' }}>
          <button
            onClick={handleContinue}
            style={{
              padding: '0.75rem 1.5rem',
              backgroundColor: '#28a745',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '1rem',
              fontWeight: '500'
            }}
          >
            Continue
          </button>
          <button
            onClick={onClose}
            style={{
              padding: '0.75rem 1.5rem',
              backgroundColor: '#6c757d',
              color: 'white',
              border: 'none',
              borderRadius: '4px',
              cursor: 'pointer',
              fontSize: '1rem'
            }}
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}
