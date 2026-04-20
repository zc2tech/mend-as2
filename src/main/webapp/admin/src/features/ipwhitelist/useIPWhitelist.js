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

const API_BASE = '/ipwhitelist';

// ========== Settings Hooks ==========

export function useWhitelistSettings() {
  return useQuery({
    queryKey: ['ipwhitelist', 'settings'],
    queryFn: async () => {
      const response = await api.get(`${API_BASE}/settings`);
      return response.data;
    }
  });
}

export function useUpdateWhitelistSettings() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (settings) => {
      const response = await api.post(`${API_BASE}/settings`, settings);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ipwhitelist', 'settings'] });
    }
  });
}

// ========== Global Whitelist Hooks ==========

export function useGlobalWhitelist(targetType = null) {
  return useQuery({
    queryKey: ['ipwhitelist', 'global', targetType],
    queryFn: async () => {
      const params = targetType ? `?targetType=${targetType}` : '';
      const response = await api.get(`${API_BASE}/global${params}`);
      return response.data;
    }
  });
}

export function useAddGlobalWhitelist() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (entry) => {
      const response = await api.post(`${API_BASE}/global`, entry);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ipwhitelist', 'global'] });
    }
  });
}

export function useUpdateGlobalWhitelist() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ id, ...entry }) => {
      const response = await api.put(`${API_BASE}/global/${id}`, entry);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ipwhitelist', 'global'] });
    }
  });
}

export function useDeleteGlobalWhitelist() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async (id) => {
      const response = await api.delete(`${API_BASE}/global/${id}`);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries({ queryKey: ['ipwhitelist', 'global'] });
    }
  });
}

// ========== Partner Whitelist Hooks ==========

export function usePartnerWhitelist(partnerId) {
  return useQuery({
    queryKey: ['ipwhitelist', 'partner', partnerId],
    queryFn: async () => {
      const response = await api.get(`${API_BASE}/partner/${partnerId}`);
      return response.data;
    },
    enabled: !!partnerId
  });
}

export function useAddPartnerWhitelist() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ partnerId, ...entry }) => {
      const response = await api.post(`${API_BASE}/partner/${partnerId}`, entry);
      return response.data;
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['ipwhitelist', 'partner', variables.partnerId] });
    }
  });
}

export function useDeletePartnerWhitelist() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ partnerId, entryId }) => {
      const response = await api.delete(`${API_BASE}/partner/${partnerId}/entry/${entryId}`);
      return response.data;
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['ipwhitelist', 'partner', variables.partnerId] });
    }
  });
}

// ========== User Whitelist Hooks ==========

export function useUserWhitelist(userId) {
  return useQuery({
    queryKey: ['ipwhitelist', 'user', userId],
    queryFn: async () => {
      const response = await api.get(`${API_BASE}/user/${userId}`);
      return response.data;
    },
    enabled: !!userId
  });
}

export function useAddUserWhitelist() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ userId, ...entry }) => {
      const response = await api.post(`${API_BASE}/user/${userId}`, entry);
      return response.data;
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['ipwhitelist', 'user', variables.userId] });
    }
  });
}

export function useDeleteUserWhitelist() {
  const queryClient = useQueryClient();
  return useMutation({
    mutationFn: async ({ userId, entryId }) => {
      const response = await api.delete(`${API_BASE}/user/${userId}/entry/${entryId}`);
      return response.data;
    },
    onSuccess: (_, variables) => {
      queryClient.invalidateQueries({ queryKey: ['ipwhitelist', 'user', variables.userId] });
    }
  });
}

// ========== Block Log Hook ==========

export function useBlockLog(targetType = null, days = 7) {
  return useQuery({
    queryKey: ['ipwhitelist', 'blocklog', targetType, days],
    queryFn: async () => {
      const params = new URLSearchParams();
      if (targetType) params.append('targetType', targetType);
      params.append('days', days);
      const response = await api.get(`${API_BASE}/blocklog?${params.toString()}`);
      return response.data;
    }
  });
}

// ========== Validate Pattern Hook ==========

export function useValidatePattern() {
  return useMutation({
    mutationFn: async (pattern) => {
      const response = await api.post(`${API_BASE}/validate`, { pattern });
      return response.data;
    }
  });
}
