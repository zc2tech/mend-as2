import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from './features/auth/useAuth';
import { ToastProvider } from './components/Toast';
import Login from './features/auth/Login';
import ProtectedRoute from './features/auth/ProtectedRoute';
import Layout from './components/Layout';
import Dashboard from './components/Dashboard';
import PartnerList from './features/partners/PartnerList';
import CertificateList from './features/certificates/CertificateList';
import MessageList from './features/messages/MessageList';
import CemList from './features/cem/CemList';
import StatisticsDashboard from './features/statistics/StatisticsDashboard';
import PreferencesForm from './features/preferences/PreferencesForm';
import SystemInfo from './features/system/SystemInfo';

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
      <BrowserRouter basename="/as2/admin">
        <ToastProvider>
          <AuthProvider>
            <Routes>
              <Route path="/login" element={<Login />} />
            <Route
              path="/"
              element={
                <ProtectedRoute>
                  <Layout />
                </ProtectedRoute>
              }
            >
              <Route index element={<Dashboard />} />
              <Route path="partners" element={<PartnerList />} />
              <Route path="certificates" element={<CertificateList />} />
              <Route path="messages" element={<MessageList />} />
              <Route path="cem" element={<CemList />} />
              <Route path="statistics" element={<StatisticsDashboard />} />
              <Route path="preferences" element={<PreferencesForm />} />
              <Route path="system" element={<SystemInfo />} />
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
