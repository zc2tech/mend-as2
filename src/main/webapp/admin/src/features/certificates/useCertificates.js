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

import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '../../api/client';
import { useAuth } from '../auth/useAuth';

export function useCertificates(keystoreType = 'sign') {
  const { user } = useAuth();

  return useQuery({
    queryKey: ['certificates', keystoreType, user?.id],
    queryFn: async () => {
      // For filtering: use 0 for admin user (legacy compatibility), database ID for others
      const filterUserId = user?.username === 'admin' ? 0 : user?.id;

      // Fetch only certificates visible to the current user
      const response = await api.get('/certificates', {
        params: {
          keystoreType,
          visibleToUser: filterUserId
        }
      });
      return response.data;
    },
    enabled: !!user?.id // Only run query if user ID is available
  });
}

export function useExportCertificate() {
  return useMutation({
    mutationFn: async ({ fingerprintSHA1, format, keystoreType }) => {
      const response = await api.post('/certificates/export', {
        fingerprintSHA1,
        format,
        keystoreType
      }, {
        responseType: 'blob'
      });
      return response.data;
    }
  });
}

export function useExportKeystore() {
  return useMutation({
    mutationFn: async ({ keystoreType, format = 'PKCS12', password }) => {
      const response = await api.post('/certificates/export-keystore', {
        keystoreType,
        format,
        password
      }, {
        responseType: 'blob'
      });
      return response.data;
    }
  });
}

export function useGenerateCSR() {
  return useMutation({
    mutationFn: async ({ fingerprintSHA1, keystoreType }) => {
      const response = await api.post('/certificates/generate-csr', {
        fingerprintSHA1,
        keystoreType,
        requestType: 'PKCS10'  // Default to PKCS10 format
      });
      return response.data;
    }
  });
}

export function useVerifyCRL() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ keystoreType }) => {
      const response = await api.post('/certificates/verify-crl', {
        keystoreType
      });
      return response.data;
    },
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries(['certificates', variables.keystoreType]);
    }
  });
}

export function useGenerateKey() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (keyGenRequest) => {
      const response = await api.post('/certificates/generate-key', keyGenRequest);
      return response.data;
    },
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries(['certificates', variables.keystoreType]);
    }
  });
}

export function useDeleteCertificate() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ alias, keystoreType, force = false }) => {
      const response = await api.delete(`/certificates/${alias}`, {
        params: { keystoreType, force }
      });
      return response.data;
    },
    onSuccess: (data, variables) => {
      queryClient.invalidateQueries(['certificates', variables.keystoreType]);
    }
  });
}
