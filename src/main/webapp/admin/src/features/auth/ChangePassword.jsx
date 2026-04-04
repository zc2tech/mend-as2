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

import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from './useAuth';
import api from '../../api/client';

export default function ChangePassword() {
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const { user, logout, clearMustChangePassword } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!newPassword || newPassword.length < 6) {
      setError('Password must be at least 6 characters long');
      return;
    }

    if (newPassword !== confirmPassword) {
      setError('Passwords do not match');
      return;
    }

    setLoading(true);

    try {
      // Get current user ID from the system
      const usersResponse = await api.get('/users');
      const currentUser = usersResponse.data.find(u => u.username === user.username);

      if (!currentUser) {
        setError('Failed to find user');
        setLoading(false);
        return;
      }

      // Change password
      await api.post(`/users/${currentUser.id}/password`, {
        newPassword
      });

      // Clear the must change password flag
      clearMustChangePassword();

      // Fetch permissions after password change
      const permissionsResponse = await api.get('/users/current/permissions');
      // Permissions are now loaded in context

      alert('Password changed successfully!');
      navigate('/');
    } catch (err) {
      setError(err.response?.data?.error || 'Failed to change password');
    } finally {
      setLoading(false);
    }
  };

  const containerStyle = {
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'center',
    minHeight: '100vh',
    backgroundColor: '#f5f5f5'
  };

  const formContainerStyle = {
    backgroundColor: 'white',
    padding: '2rem',
    borderRadius: '8px',
    boxShadow: '0 2px 10px rgba(0,0,0,0.1)',
    width: '100%',
    maxWidth: '400px'
  };

  const inputStyle = {
    width: '100%',
    padding: '0.75rem',
    border: '1px solid #ddd',
    borderRadius: '4px',
    fontSize: '1rem',
    marginBottom: '1rem',
    boxSizing: 'border-box'
  };

  const buttonStyle = {
    width: '100%',
    padding: '0.75rem',
    backgroundColor: '#007bff',
    color: 'white',
    border: 'none',
    borderRadius: '4px',
    fontSize: '1rem',
    cursor: 'pointer',
    fontWeight: '500'
  };

  const errorStyle = {
    backgroundColor: '#f8d7da',
    color: '#721c24',
    padding: '0.75rem',
    borderRadius: '4px',
    marginBottom: '1rem',
    border: '1px solid #f5c6cb'
  };

  return (
    <div style={containerStyle}>
      <div style={formContainerStyle}>
        <h2 style={{ marginTop: 0, marginBottom: '0.5rem', textAlign: 'center' }}>Change Password</h2>
        <p style={{ textAlign: 'center', color: '#666', marginBottom: '1.5rem' }}>
          You must change your password before continuing
        </p>

        <form onSubmit={handleSubmit}>
          {error && <div style={errorStyle}>{error}</div>}

          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>
            New Password
          </label>
          <input
            type="password"
            value={newPassword}
            onChange={(e) => setNewPassword(e.target.value)}
            style={inputStyle}
            disabled={loading}
            required
            autoFocus
          />

          <label style={{ display: 'block', marginBottom: '0.5rem', fontWeight: '500' }}>
            Confirm New Password
          </label>
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            style={inputStyle}
            disabled={loading}
            required
          />

          <button
            type="submit"
            style={buttonStyle}
            disabled={loading}
          >
            {loading ? 'Changing Password...' : 'Change Password'}
          </button>

          <button
            type="button"
            onClick={logout}
            style={{
              ...buttonStyle,
              backgroundColor: '#6c757d',
              marginTop: '0.5rem'
            }}
          >
            Cancel and Logout
          </button>
        </form>
      </div>
    </div>
  );
}
