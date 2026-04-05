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

import { Link, Outlet, useNavigate } from 'react-router-dom';
import { useState, useRef, useEffect } from 'react';
import { useAuth } from '../features/auth/useAuth';

export default function Layout() {
  const { user, logout, hasAnyPermission, permissions } = useAuth();
  const navigate = useNavigate();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  // Close dropdown when clicking outside
  useEffect(() => {
    const handleClickOutside = (event) => {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setDropdownOpen(false);
      }
    };

    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const navStyle = {
    backgroundColor: '#2c3e50',
    color: 'white',
    padding: '1rem 2rem',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center'
  };

  const linkStyle = {
    color: 'white',
    textDecoration: 'none',
    padding: '0.5rem 1rem',
    marginRight: '1rem',
    borderRadius: '4px',
    transition: 'background-color 0.2s'
  };

  const mainStyle = {
    padding: '2rem',
    maxWidth: '1400px',
    margin: '0 auto'
  };

  const dropdownContainerStyle = {
    position: 'relative',
    display: 'inline-block'
  };

  const userButtonStyle = {
    display: 'flex',
    alignItems: 'center',
    gap: '0.5rem',
    padding: '0.5rem 1rem',
    backgroundColor: '#34495e',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.95rem',
    transition: 'background-color 0.2s'
  };

  const triangleStyle = {
    display: 'inline-block',
    width: 0,
    height: 0,
    borderLeft: '5px solid transparent',
    borderRight: '5px solid transparent',
    borderTop: '5px solid white',
    marginLeft: '0.25rem',
    transition: 'transform 0.2s'
  };

  const dropdownMenuStyle = {
    position: 'absolute',
    right: 0,
    top: '100%',
    marginTop: '0.5rem',
    backgroundColor: 'white',
    borderRadius: '4px',
    boxShadow: '0 4px 12px rgba(0, 0, 0, 0.15)',
    minWidth: '200px',
    zIndex: 1000,
    overflow: 'hidden'
  };

  const dropdownItemStyle = {
    display: 'block',
    width: '100%',
    padding: '0.75rem 1rem',
    backgroundColor: 'transparent',
    color: '#333',
    border: 'none',
    textAlign: 'left',
    cursor: 'pointer',
    fontSize: '0.9rem',
    transition: 'background-color 0.2s',
    textDecoration: 'none'
  };

  const handlePreferences = () => {
    setDropdownOpen(false);
    navigate('/preferences');
  };

  const handleChangePassword = () => {
    setDropdownOpen(false);
    navigate('/change-password');
  };

  const handleLogout = () => {
    setDropdownOpen(false);
    logout();
  };

  // Menu visibility based on permissions
  console.log('User permissions:', permissions);
  const showPartners = hasAnyPermission('PARTNER_READ', 'PARTNER_WRITE');
  const showCertificates = hasAnyPermission('CERT_READ', 'CERT_WRITE');
  const showMessages = hasAnyPermission('MESSAGE_READ', 'MESSAGE_WRITE');
  const showSystem = hasAnyPermission('SYSTEM_READ', 'SYSTEM_WRITE');
  console.log('showSystem:', showSystem);
  const showUsers = hasAnyPermission('USER_MANAGE');

  return (
    <div>
      <nav style={navStyle}>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <h2 style={{ margin: 0, marginRight: '2rem' }}>AS2 Server</h2>
          <Link to="/" style={linkStyle}>Dashboard</Link>
          {showPartners && <Link to="/partners" style={linkStyle}>Partners</Link>}
          {showCertificates && <Link to="/certificates" style={linkStyle}>Certificates</Link>}
          {showMessages && <Link to="/messages" style={linkStyle}>Messages</Link>}
          {showSystem && <Link to="/system" style={linkStyle}>System</Link>}
          {showUsers && <Link to="/users" style={linkStyle}>Users</Link>}
        </div>
        <div style={{ display: 'flex', alignItems: 'center', gap: '1rem' }}>
          <div style={dropdownContainerStyle} ref={dropdownRef}>
            <button
              onClick={() => setDropdownOpen(!dropdownOpen)}
              style={{
                ...userButtonStyle,
                backgroundColor: dropdownOpen ? '#3d566e' : '#34495e'
              }}
              onMouseEnter={(e) => e.target.style.backgroundColor = '#3d566e'}
              onMouseLeave={(e) => e.target.style.backgroundColor = dropdownOpen ? '#3d566e' : '#34495e'}
            >
              <span>User: {user?.username}</span>
              <span style={{
                ...triangleStyle,
                transform: dropdownOpen ? 'rotate(180deg)' : 'rotate(0deg)'
              }} />
            </button>

            {dropdownOpen && (
              <div style={dropdownMenuStyle}>
                <button
                  onClick={handlePreferences}
                  style={dropdownItemStyle}
                  onMouseEnter={(e) => e.target.style.backgroundColor = '#f5f5f5'}
                  onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                >
                  Preferences
                </button>
                <button
                  onClick={handleChangePassword}
                  style={dropdownItemStyle}
                  onMouseEnter={(e) => e.target.style.backgroundColor = '#f5f5f5'}
                  onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                >
                  Change Password
                </button>
                <div style={{ borderTop: '1px solid #e0e0e0', margin: '0.25rem 0' }} />
                <button
                  onClick={handleLogout}
                  style={{
                    ...dropdownItemStyle,
                    color: '#e74c3c',
                    fontWeight: '500'
                  }}
                  onMouseEnter={(e) => e.target.style.backgroundColor = '#fee'}
                  onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                >
                  Logout
                </button>
              </div>
            )}
          </div>
        </div>
      </nav>
      <main style={mainStyle}>
        <Outlet />
      </main>
    </div>
  );
}
