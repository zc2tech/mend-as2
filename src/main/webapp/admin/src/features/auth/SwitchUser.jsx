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
import { useNavigate } from 'react-router-dom';
import { useQueryClient } from '@tanstack/react-query';
import { useAuth } from './useAuth';
import { useToast } from '../../components/Toast';
import api from '../../api/client';
import { LoadingPage } from '../../components/Loading';

export default function SwitchUser() {
  const { user, switchUser } = useAuth();
  const navigate = useNavigate();
  const toast = useToast();
  const queryClient = useQueryClient();
  const [users, setUsers] = useState([]);
  const [loading, setLoading] = useState(true);
  const [switching, setSwitching] = useState(false);
  const [searchTerm, setSearchTerm] = useState('');

  useEffect(() => {
    loadUsers();
  }, []);

  const loadUsers = async () => {
    try {
      // Get all enabled users
      const response = await api.get('/users', {
        params: {
          enabledOnly: true
        }
      });

      // Check if current user is 'admin' super user or has ADMIN role
      const isSuperUser = user?.username === 'admin';
      const isCurrentUserAdmin = user?.roleIds?.includes(1) || user?.roles?.some(r => r.name === 'ADMIN');

      // Filter users:
      // 1. Remove current user
      // 2. If super user 'admin' -> can see all users
      // 3. If ADMIN role user (not 'admin') -> exclude ADMIN users and 'admin' super user
      const otherUsers = response.data.filter(u => {
        if (u.id === user?.id) {
          return false; // Remove self
        }

        if (isSuperUser) {
          // Super user 'admin' can switch to anyone
          return true;
        }

        if (isCurrentUserAdmin) {
          // Regular ADMIN role user - filter out ADMIN users and 'admin' super user
          if (u.username === 'admin') {
            return false; // Exclude super user
          }
          const isTargetAdmin = u.roleIds?.includes(1) || u.roles?.some(r => r.name === 'ADMIN');
        
          return !isTargetAdmin; // Only show non-ADMIN users
        }

        return true; // Non-ADMIN users can see all other users
      });

      setUsers(otherUsers);
    } catch (error) {
      toast.error('Failed to load users: ' + (error.response?.data?.error || error.message));
    } finally {
      setLoading(false);
    }
  };

  const handleSwitchUser = async (targetUsername) => {
    if (!window.confirm(`Switch to user "${targetUsername}"?\n\nYou will be logged in as this user and see their data.`)) {
      return;
    }

    setSwitching(true);
    try {
      await switchUser(targetUsername);

      // Invalidate all cached queries to force refetch with new user's data
      queryClient.clear();

      toast.success(`Switched to user: ${targetUsername}`);
      navigate('/');
    } catch (error) {
      toast.error('Failed to switch user: ' + (error.response?.data?.error || error.message));
    } finally {
      setSwitching(false);
    }
  };

  if (loading) {
    return <LoadingPage message="Loading users..." />;
  }

  const filteredUsers = users.filter(u =>
    u.username?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.fullName?.toLowerCase().includes(searchTerm.toLowerCase()) ||
    u.email?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const containerStyle = {
    maxWidth: '800px',
    margin: '0 auto'
  };

  const cardStyle = {
    backgroundColor: 'white',
    borderRadius: '8px',
    boxShadow: '0 2px 4px rgba(0,0,0,0.1)',
    padding: '2rem'
  };

  const tableStyle = {
    width: '100%',
    borderCollapse: 'collapse',
    marginTop: '1rem'
  };

  const thStyle = {
    textAlign: 'left',
    padding: '0.75rem',
    borderBottom: '2px solid #dee2e6',
    fontWeight: '600',
    backgroundColor: '#f8f9fa'
  };

  const tdStyle = {
    padding: '0.75rem',
    borderBottom: '1px solid #dee2e6'
  };

  const buttonStyle = {
    padding: '0.5rem 1rem',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    cursor: 'pointer',
    fontSize: '0.875rem'
  };

  const searchInputStyle = {
    width: '100%',
    padding: '0.5rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
    marginBottom: '1rem'
  };

  return (
    <div style={containerStyle}>
      <div style={cardStyle}>
        <h1>Switch User</h1>
        <p style={{ color: '#666', marginBottom: '1.5rem' }}>
          Select a user to switch to their account context. You will see their partners, certificates, and messages.
          {user?.username === 'admin' ? (
            <span> Super user can switch to any enabled user.</span>
          ) : (
            <span> ADMIN role users can only switch to non-ADMIN users.</span>
          )}
        </p>

        <input
          type="text"
          placeholder="Search by username, name, or email..."
          value={searchTerm}
          onChange={(e) => setSearchTerm(e.target.value)}
          style={searchInputStyle}
        />

        {filteredUsers.length === 0 ? (
          <p style={{ textAlign: 'center', color: '#666', padding: '2rem' }}>
            {searchTerm ? 'No users match your search' : 'No other users available'}
          </p>
        ) : (
          <table style={tableStyle}>
            <thead>
              <tr>
                <th style={thStyle}>Username</th>
                <th style={thStyle}>Full Name</th>
                <th style={thStyle}>Email</th>
                <th style={thStyle}>Role</th>
                <th style={thStyle}>Status</th>
                <th style={thStyle}>Action</th>
              </tr>
            </thead>
            <tbody>
              {filteredUsers.map(u => (
                <tr key={u.id}>
                  <td style={tdStyle}>{u.username}</td>
                  <td style={tdStyle}>{u.fullName || '-'}</td>
                  <td style={tdStyle}>{u.email || '-'}</td>
                  <td style={tdStyle}>
                    {u.roleIds?.includes(1) || u.roles?.some(r => r.name === 'ADMIN') ? (
                      <span style={{
                        display: 'inline-block',
                        padding: '0.25rem 0.5rem',
                        borderRadius: '4px',
                        fontSize: '0.75rem',
                        backgroundColor: '#dc3545',
                        color: 'white',
                        fontWeight: '500'
                      }}>
                        Admin
                      </span>
                    ) : (
                      <span style={{
                        display: 'inline-block',
                        padding: '0.25rem 0.5rem',
                        borderRadius: '4px',
                        fontSize: '0.75rem',
                        backgroundColor: '#007bff',
                        color: 'white',
                        fontWeight: '500'
                      }}>
                        User
                      </span>
                    )}
                  </td>
                  <td style={tdStyle}>
                    {u.enabled ? (
                      <span style={{ color: '#28a745' }}>✓ Active</span>
                    ) : (
                      <span style={{ color: '#dc3545' }}>✗ Disabled</span>
                    )}
                  </td>
                  <td style={tdStyle}>
                    <button
                      style={buttonStyle}
                      onClick={() => handleSwitchUser(u.username)}
                      disabled={switching || !u.enabled}
                      onMouseEnter={(e) => e.target.style.backgroundColor = '#0056b3'}
                      onMouseLeave={(e) => e.target.style.backgroundColor = '#007bff'}
                    >
                      {switching ? 'Switching...' : 'Switch'}
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}

        <div style={{ marginTop: '1.5rem', paddingTop: '1.5rem', borderTop: '1px solid #dee2e6' }}>
          <button
            onClick={() => navigate('/')}
            style={{
              ...buttonStyle,
              backgroundColor: '#6c757d'
            }}
            onMouseEnter={(e) => e.target.style.backgroundColor = '#545b62'}
            onMouseLeave={(e) => e.target.style.backgroundColor = '#6c757d'}
          >
            Cancel
          </button>
        </div>
      </div>
    </div>
  );
}
