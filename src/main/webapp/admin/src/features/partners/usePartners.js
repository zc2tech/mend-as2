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

export function usePartners() {
  const { user } = useAuth();

  return useQuery({
    queryKey: ['partners', user?.id],
    queryFn: async () => {
      // For filtering: use 0 for admin user (legacy compatibility), database ID for others
      const filterUserId = user?.username === 'admin' ? 0 : user?.id;

      // Fetch only partners visible to the current user
      const response = await api.get('/partners', {
        params: {
          visibleToUser: filterUserId
        }
      });
      return response.data;
    },
    enabled: !!user?.id // Only run query if user ID is available
  });
}

export function useCreatePartner() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (partner) => {
      const response = await api.post('/partners', partner);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['partners']);
    }
  });
}

export function useUpdatePartner() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ id, partner }) => {
      const response = await api.put(`/partners/${id}`, partner);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['partners']);
    }
  });
}

export function useDeletePartner() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async (as2id) => {
      const response = await api.delete(`/partners/${as2id}`);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['partners']);
    }
  });
}
