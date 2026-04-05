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
import api from '../../api/client';

export default function HTTPServerConfig() {
  const { data: config, isLoading, error } = useQuery({
    queryKey: ['httpServerConfig'],
    queryFn: async () => {
      const response = await api.get('/system/http-config');
      return response.data;
    }
  });

  if (isLoading) {
    return <LoadingPage message="Loading HTTP server configuration..." />;
  }

  if (error) {
    return <div style={{ color: 'red' }}>Error loading HTTP server config: {error.message}</div>;
  }

  const cardStyle = {
    backgroundColor: 'white',
    padding: '1.5rem',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    marginBottom: '1.5rem'
  };

  const labelStyle = {
    fontWeight: '600',
    color: '#495057',
    marginBottom: '0.25rem',
    fontSize: '0.875rem'
  };

  const valueStyle = {
    color: '#212529',
    marginBottom: '1rem',
    fontSize: '0.9rem'
  };

  const tableStyle = {
    width: '100%',
    borderCollapse: 'collapse',
    fontSize: '0.875rem'
  };

  const thStyle = {
    textAlign: 'left',
    padding: '0.5rem',
    borderBottom: '2px solid #dee2e6',
    fontWeight: '600',
    color: '#495057',
    backgroundColor: '#f8f9fa'
  };

  const tdStyle = {
    padding: '0.5rem',
    borderBottom: '1px solid #dee2e6'
  };

  const listStyle = {
    maxHeight: '200px',
    overflowY: 'auto',
    backgroundColor: '#f8f9fa',
    padding: '0.5rem',
    borderRadius: '4px',
    fontSize: '0.8rem',
    lineHeight: '1.6'
  };

  return (
    <div>
      <div style={cardStyle}>
        <h2 style={{ marginTop: 0 }}>Server Information</h2>
        <div>
          <div style={labelStyle}>Jetty Version</div>
          <div style={valueStyle}>{config.jettyVersion || '-'}</div>

          <div style={labelStyle}>Java Version</div>
          <div style={valueStyle}>{config.javaVersion || '-'}</div>

          <div style={labelStyle}>TLS/SSL Enabled</div>
          <div style={valueStyle}>{config.tlsEnabled ? 'Yes' : 'No'}</div>

          {config.tlsEnabled && (
            <>
              <div style={labelStyle}>TLS Security Provider</div>
              <div style={valueStyle}>{config.tlsSecurityProvider || '-'}</div>

              <div style={labelStyle}>Client Authentication Required</div>
              <div style={valueStyle}>{config.needClientAuth ? 'Yes' : 'No'}</div>
            </>
          )}

          <div style={labelStyle}>Receipt URL Path</div>
          <div style={valueStyle}>{config.receiptURLPath || '-'}</div>

          <div style={labelStyle}>Server State Path</div>
          <div style={valueStyle}>{config.serverStatePath || '-'}</div>

          <div style={labelStyle}>Configuration File</div>
          <div style={valueStyle}>{config.configFile || '-'}</div>

          <div style={labelStyle}>User Configuration File</div>
          <div style={valueStyle}>{config.userConfigFile || '-'}</div>
        </div>
      </div>

      {config.listeners && config.listeners.length > 0 && (
        <div style={cardStyle}>
          <h2 style={{ marginTop: 0 }}>Network Listeners</h2>
          <table style={tableStyle}>
            <thead>
              <tr>
                <th style={thStyle}>Protocol</th>
                <th style={thStyle}>Port</th>
                <th style={thStyle}>Adapter/Address</th>
              </tr>
            </thead>
            <tbody>
              {config.listeners.map((listener, index) => (
                <tr key={index}>
                  <td style={tdStyle}>{listener.protocol || '-'}</td>
                  <td style={tdStyle}>{listener.port}</td>
                  <td style={tdStyle}>{listener.adapter || '-'}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}

      {config.tlsEnabled && (
        <div style={cardStyle}>
          <h2 style={{ marginTop: 0 }}>TLS/SSL Configuration</h2>

          <div style={labelStyle}>Possible Protocols ({config.possibleProtocols?.length || 0})</div>
          <div style={{...listStyle, marginBottom: '1rem'}}>
            {config.possibleProtocols && config.possibleProtocols.length > 0 ? (
              config.possibleProtocols.map((protocol, index) => (
                <div key={index}>{protocol}</div>
              ))
            ) : (
              <div style={{ color: '#6c757d' }}>None</div>
            )}
          </div>

          <div style={labelStyle}>Excluded Protocols ({config.excludedProtocols?.length || 0})</div>
          <div style={{...listStyle, marginBottom: '1rem'}}>
            {config.excludedProtocols && config.excludedProtocols.length > 0 ? (
              config.excludedProtocols.map((protocol, index) => (
                <div key={index} style={{ color: '#dc3545' }}>{protocol}</div>
              ))
            ) : (
              <div style={{ color: '#6c757d' }}>None</div>
            )}
          </div>

          <div style={labelStyle}>Possible Ciphers ({config.possibleCiphers?.length || 0})</div>
          <div style={{...listStyle, marginBottom: '1rem'}}>
            {config.possibleCiphers && config.possibleCiphers.length > 0 ? (
              config.possibleCiphers.map((cipher, index) => (
                <div key={index}>{cipher}</div>
              ))
            ) : (
              <div style={{ color: '#6c757d' }}>None</div>
            )}
          </div>

          <div style={labelStyle}>Excluded Ciphers ({config.excludedCiphers?.length || 0})</div>
          <div style={listStyle}>
            {config.excludedCiphers && config.excludedCiphers.length > 0 ? (
              config.excludedCiphers.map((cipher, index) => (
                <div key={index} style={{ color: '#dc3545' }}>{cipher}</div>
              ))
            ) : (
              <div style={{ color: '#6c757d' }}>None</div>
            )}
          </div>
        </div>
      )}

      {config.deployedWars && config.deployedWars.length > 0 && (
        <div style={cardStyle}>
          <h2 style={{ marginTop: 0 }}>Deployed WAR Files</h2>
          {config.deployedWars.map((war, index) => (
            <div key={index} style={{ marginBottom: '0.5rem', fontSize: '0.875rem' }}>
              {war}
            </div>
          ))}
        </div>
      )}
    </div>
  );
}
