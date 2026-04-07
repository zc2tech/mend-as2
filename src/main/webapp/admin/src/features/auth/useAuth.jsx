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

import { createContext, useContext, useState, useEffect } from 'react';
import api from '../../api/client';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [permissions, setPermissions] = useState([]);
  const [loading, setLoading] = useState(true);
  const [mustChangePassword, setMustChangePassword] = useState(false);

  useEffect(() => {
    // Only check auth if not on login page
    if (!window.location.pathname.includes('/login')) {
      checkAuth();
    } else {
      setLoading(false);
    }
  }, []);

  const checkAuth = async () => {
    try {
      // Try to get system info - if it works, we're authenticated
      await api.get('/system/info');

      // Fetch current user info
      const userResponse = await api.get('/users/current');
      setUser({ id: userResponse.data.id, username: userResponse.data.username });

      // Fetch user permissions
      const permissionsResponse = await api.get('/users/current/permissions');
      const userPermissions = permissionsResponse.data;

      setPermissions(userPermissions);
    } catch (error) {
      setUser(null);
      setPermissions([]);
    } finally {
      setLoading(false);
    }
  };

  const login = async (username, password) => {
    try {
      const response = await api.post('/auth/login', { username, password });

      console.log('Login response:', response.data);
      console.log('mustChangePassword:', response.data.mustChangePassword);

      // Check if user must change password
      if (response.data.mustChangePassword) {
        setMustChangePassword(true);
        setUser({ id: response.data.id, username: response.data.username });
        return { success: true, mustChangePassword: true };
      }

      // Fetch user permissions after login
      const permissionsResponse = await api.get('/users/current/permissions');
      const userPermissions = permissionsResponse.data;

      setPermissions(userPermissions);
      setUser({ id: response.data.id, username: response.data.username });
      setMustChangePassword(false);
      return { success: true, mustChangePassword: false };
    } catch (error) {
      return {
        success: false,
        error: error.response?.data?.error || 'Login failed'
      };
    }
  };

  const logout = async () => {
    try {
      await api.post('/auth/logout');
    } catch (error) {
      // Ignore errors during logout
    } finally {
      setUser(null);
      setPermissions([]);
      setMustChangePassword(false);
      window.location.href = '/as2/webui/login';
    }
  };

  const clearMustChangePassword = () => {
    setMustChangePassword(false);
  };

  const refreshPermissions = async () => {
    try {
      const permissionsResponse = await api.get('/users/current/permissions');
      setPermissions(permissionsResponse.data);
    } catch (error) {
      console.error('Failed to refresh permissions:', error);
    }
  };

  const hasPermission = (permissionName) => {
    return permissions.some(p => p.name === permissionName);
  };

  const hasAnyPermission = (...permissionNames) => {
    return permissionNames.some(name => hasPermission(name));
  };

  return (
    <AuthContext.Provider value={{
      user,
      permissions,
      loading,
      mustChangePassword,
      login,
      logout,
      clearMustChangePassword,
      refreshPermissions,
      hasPermission,
      hasAnyPermission
    }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth must be used within AuthProvider');
  }
  return context;
}
