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

import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from './features/auth/useAuth';
import { ToastProvider } from './components/Toast';
import Login from './features/auth/Login';
import ChangePassword from './features/auth/ChangePassword';
import SwitchUser from './features/auth/SwitchUser';
import ProtectedRoute from './features/auth/ProtectedRoute';
import PermissionRoute from './features/auth/PermissionRoute';
import Layout from './components/Layout';
import Dashboard from './components/Dashboard';
import PartnerList from './features/partners/PartnerList';
import CertificateList from './features/certificates/CertificateList';
import MessageList from './features/messages/MessageList';
import TrackerMessageList from './features/tracker/TrackerMessageList';
import SystemInfo from './features/system/SystemInfo';
import UserManagement from './features/users/UserManagement';
import UserPreferences from './features/preferences/UserPreferences';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
      staleTime: 5 * 60 * 1000, // 5 minutes
    },
  },
});

function App() {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter basename="/as2/webui">
        <ToastProvider>
          <AuthProvider>
            <Routes>
              <Route path="/login" element={<Login />} />
              {/* Standalone change password for forced password change */}
              <Route path="/change-password-forced" element={<ChangePassword />} />
            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <Layout />
                </ProtectedRoute>
              }
            >
              <Route index element={<Dashboard />} />
              {/* Change password within the app layout */}
              <Route path="change-password" element={<ChangePassword />} />
              <Route path="preferences" element={<UserPreferences />} />
              {/* Switch user - only accessible to admins */}
              <Route
                path="switch-user"
                element={
                  <PermissionRoute requiredPermissions={['USER_MANAGE']}>
                    <SwitchUser />
                  </PermissionRoute>
                }
              />
              <Route
                path="partners"
                element={
                  <PermissionRoute requiredPermissions={['PARTNER_READ', 'PARTNER_WRITE']}>
                    <PartnerList />
                  </PermissionRoute>
                }
              />
              <Route
                path="certificates"
                element={
                  <PermissionRoute requiredPermissions={['CERT_READ', 'CERT_WRITE']}>
                    <CertificateList />
                  </PermissionRoute>
                }
              />
              <Route
                path="messages"
                element={
                  <PermissionRoute requiredPermissions={['MESSAGE_READ', 'MESSAGE_WRITE']}>
                    <MessageList />
                  </PermissionRoute>
                }
              />
              <Route
                path="tracker-messages"
                element={
                  <PermissionRoute requiredPermissions={['MESSAGE_READ', 'MESSAGE_WRITE']}>
                    <TrackerMessageList />
                  </PermissionRoute>
                }
              />
              <Route
                path="system"
                element={
                  <PermissionRoute requiredPermissions={['SYSTEM_READ', 'SYSTEM_WRITE']}>
                    <SystemInfo />
                  </PermissionRoute>
                }
              />
              <Route
                path="users"
                element={
                  <PermissionRoute requiredPermissions={['USER_MANAGE']}>
                    <UserManagement />
                  </PermissionRoute>
                }
              />
            </Route>
            <Route path="*" element={<Navigate to="/" replace />} />
          </Routes>
        </AuthProvider>
      </ToastProvider>
    </BrowserRouter>
    </QueryClientProvider>
  );
}

export default App;
