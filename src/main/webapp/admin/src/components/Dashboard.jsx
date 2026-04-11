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

import { Link } from 'react-router-dom';
import { useAuth } from '../features/auth/useAuth';
import AdminWarnings from './AdminWarnings';

export default function Dashboard() {
  const { hasAnyPermission } = useAuth();

  const cardStyle = {
    padding: '1.5rem',
    backgroundColor: 'white',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    textDecoration: 'none',
    color: 'inherit',
    display: 'block',
    transition: 'transform 0.2s, box-shadow 0.2s',
    cursor: 'pointer'
  };

  const cardHoverStyle = {
    transform: 'translateY(-2px)',
    boxShadow: '0 4px 8px rgba(0,0,0,0.15)'
  };

  const showPartners = hasAnyPermission('PARTNER_WRITE') ||
                       (hasAnyPermission('PARTNER_READ') &&
                        !hasAnyPermission('MESSAGE_READ', 'MESSAGE_WRITE'));
  const showCertificates = hasAnyPermission('CERT_READ', 'CERT_WRITE');
  const showMessages = hasAnyPermission('MESSAGE_READ', 'MESSAGE_WRITE');
  const showSystem = hasAnyPermission('SYSTEM_READ', 'SYSTEM_WRITE');
  const showUsers = hasAnyPermission('USER_MANAGE');

  return (
    <div>
      <h1>Dashboard</h1>
      <p>Welcome to the AS2 Server Administration Interface</p>

      <AdminWarnings />

      <div style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fit, minmax(250px, 1fr))',
        gap: '1rem',
        marginTop: '2rem'
      }}>
        {showPartners && (
          <Link
            to="/partners"
            style={cardStyle}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = cardHoverStyle.transform;
              e.currentTarget.style.boxShadow = cardHoverStyle.boxShadow;
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = '';
              e.currentTarget.style.boxShadow = cardStyle.boxShadow;
            }}
          >
            <h3 style={{ marginTop: 0, color: '#007bff' }}>Partners</h3>
            <p style={{ color: '#666', marginBottom: 0 }}>Manage AS2 trading partners and their configurations</p>
          </Link>
        )}

        {showCertificates && (
          <Link
            to="/certificates"
            style={cardStyle}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = cardHoverStyle.transform;
              e.currentTarget.style.boxShadow = cardHoverStyle.boxShadow;
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = '';
              e.currentTarget.style.boxShadow = cardStyle.boxShadow;
            }}
          >
            <h3 style={{ marginTop: 0, color: '#28a745' }}>Certificates</h3>
            <p style={{ color: '#666', marginBottom: 0 }}>Manage security certificates for encryption and signing</p>
          </Link>
        )}

        {showMessages && (
          <Link
            to="/messages"
            style={cardStyle}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = cardHoverStyle.transform;
              e.currentTarget.style.boxShadow = cardHoverStyle.boxShadow;
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = '';
              e.currentTarget.style.boxShadow = cardStyle.boxShadow;
            }}
          >
            <h3 style={{ marginTop: 0, color: '#17a2b8' }}>AS2 Messages</h3>
            <p style={{ color: '#666', marginBottom: 0 }}>Monitor AS2 message transactions and MDNs</p>
          </Link>
        )}

        {showMessages && (
          <Link
            to="/tracker-messages"
            style={cardStyle}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = cardHoverStyle.transform;
              e.currentTarget.style.boxShadow = cardHoverStyle.boxShadow;
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = '';
              e.currentTarget.style.boxShadow = cardStyle.boxShadow;
            }}
          >
            <h3 style={{ marginTop: 0, color: '#20c997' }}>Tracker Messages</h3>
            <p style={{ color: '#666', marginBottom: 0 }}>View HTTP POST tracker messages and payloads</p>
          </Link>
        )}

        {showSystem && (
          <Link
            to="/system"
            style={cardStyle}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = cardHoverStyle.transform;
              e.currentTarget.style.boxShadow = cardHoverStyle.boxShadow;
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = '';
              e.currentTarget.style.boxShadow = cardStyle.boxShadow;
            }}
          >
            <h3 style={{ marginTop: 0, color: '#6c757d' }}>System</h3>
            <p style={{ color: '#666', marginBottom: 0 }}>View and configure system settings</p>
          </Link>
        )}

        {showUsers && (
          <Link
            to="/users"
            style={cardStyle}
            onMouseEnter={(e) => {
              e.currentTarget.style.transform = cardHoverStyle.transform;
              e.currentTarget.style.boxShadow = cardHoverStyle.boxShadow;
            }}
            onMouseLeave={(e) => {
              e.currentTarget.style.transform = '';
              e.currentTarget.style.boxShadow = cardStyle.boxShadow;
            }}
          >
            <h3 style={{ marginTop: 0, color: '#ffc107' }}>User Management</h3>
            <p style={{ color: '#666', marginBottom: 0 }}>Manage users, roles, and permissions</p>
          </Link>
        )}
      </div>
    </div>
  );
}
