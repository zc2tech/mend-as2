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
import { useQueryClient } from '@tanstack/react-query';
import { useToast } from './Toast';

export default function Layout() {
  const { user, logout, switchBack, originalUser, hasAnyPermission, permissions } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();
  const queryClient = useQueryClient();
  const [dropdownOpen, setDropdownOpen] = useState(false);
  const dropdownRef = useRef(null);

  // Check if current user is admin (has USER_MANAGE permission)
  const isAdmin = hasAnyPermission('USER_MANAGE');

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

  // Global keyboard shortcut: Cmd+M (Mac) or Ctrl+M (Windows/Linux) to open Send Message
  useEffect(() => {
    const handleKeyDown = (event) => {
      // Check for Cmd+M (Mac) or Ctrl+M (Windows/Linux)
      const isCmdOrCtrl = event.metaKey || event.ctrlKey;
      const isM = event.key.toLowerCase() === 'm';

      if (isCmdOrCtrl && isM) {
        // Don't trigger if user is typing in an input/textarea
        const activeElement = document.activeElement;
        if (activeElement && (
          activeElement.tagName === 'INPUT' ||
          activeElement.tagName === 'TEXTAREA' ||
          activeElement.isContentEditable
        )) {
          return;
        }

        // Prevent default browser behavior
        event.preventDefault();

        // Navigate to manual send on messages page
        navigate('/messages', { state: { openManualSend: true } });
      }
    };

    document.addEventListener('keydown', handleKeyDown);
    return () => document.removeEventListener('keydown', handleKeyDown);
  }, [navigate]);

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

  const handleSwitchUser = () => {
    setDropdownOpen(false);
    navigate('/switch-user');
  };

  const handleLogout = () => {
    setDropdownOpen(false);
    logout();
  };

  const handleSwitchBack = async () => {
    try {
      await switchBack();
      queryClient.clear(); // Clear all cached data
      toast.success(`Switched back to: ${originalUser.username}`);
      navigate('/');
    } catch (error) {
      toast.error('Failed to switch back: ' + (error.response?.data?.error || error.message));
    }
  };

  // Menu visibility based on permissions
  const showPartners = hasAnyPermission('PARTNER_WRITE') ||
                       (hasAnyPermission('PARTNER_READ') &&
                        !hasAnyPermission('MESSAGE_READ', 'MESSAGE_WRITE'));
  const showCertificates = hasAnyPermission('CERT_READ', 'CERT_WRITE');
  const showMessages = hasAnyPermission('MESSAGE_READ', 'MESSAGE_WRITE');
  const showSystem = hasAnyPermission('SYSTEM_READ', 'SYSTEM_WRITE');
  const showUsers = hasAnyPermission('USER_MANAGE');
  // IP Whitelist is only visible to 'admin' super user
  const showIPWhitelist = user?.username === 'admin';

  const switchedUserBannerStyle = {
    backgroundColor: '#fff3cd',
    borderBottom: '2px solid #ffc107',
    padding: '0.75rem 2rem',
    display: 'flex',
    justifyContent: 'space-between',
    alignItems: 'center',
    color: '#856404'
  };

  const switchBackButtonStyle = {
    padding: '0.5rem 1rem',
    backgroundColor: '#ffc107',
    color: '#000',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.875rem',
    fontWeight: '600'
  };

  return (
    <div>
      {/* Show banner when viewing as another user */}
      {originalUser && (
        <div style={switchedUserBannerStyle}>
          <span>
            <strong>⚠️ Viewing as: {user?.username}</strong>
            {' '} (Originally logged in as: {originalUser.username})
          </span>
          <button
            onClick={handleSwitchBack}
            style={switchBackButtonStyle}
            onMouseEnter={(e) => e.target.style.backgroundColor = '#e0a800'}
            onMouseLeave={(e) => e.target.style.backgroundColor = '#ffc107'}
          >
            Switch Back to {originalUser.username}
          </button>
        </div>
      )}
      <nav style={navStyle}>
        <div style={{ display: 'flex', alignItems: 'center' }}>
          <h2 style={{ margin: 0, marginRight: '2rem' }}>AS2 Server</h2>
          <Link to="/" style={linkStyle}>Dashboard</Link>
          {showPartners && <Link to="/partners" style={linkStyle}>My Partners</Link>}
          {showCertificates && <Link to="/certificates" style={linkStyle}>My Sign/Crypt</Link>}
          {showMessages && <Link to="/messages" style={linkStyle}>AS2 Messages</Link>}
          {showMessages && <Link to="/tracker-messages" style={linkStyle}>Tracker Messages</Link>}
          {showSystem && <Link to="/system" style={linkStyle}>System</Link>}
          {showUsers && <Link to="/users" style={linkStyle}>Users</Link>}
          {showIPWhitelist && <Link to="/ipwhitelist" style={linkStyle}>IP Whitelist</Link>}
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
                {isAdmin && !originalUser && (
                  <button
                    onClick={handleSwitchUser}
                    style={dropdownItemStyle}
                    onMouseEnter={(e) => e.target.style.backgroundColor = '#f5f5f5'}
                    onMouseLeave={(e) => e.target.style.backgroundColor = 'transparent'}
                  >
                    Switch User
                  </button>
                )}
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
