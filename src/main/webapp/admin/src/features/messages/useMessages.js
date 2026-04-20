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
import { useEffect, useCallback } from 'react';
import api from '../../api/client';

export function useMessages(filters = {}) {
  // Remove messageId from the query key since we filter it client-side
  const { messageId, ...serverFilters } = filters;

  // Build a clean query key that only includes non-empty filter values
  const cleanFilters = Object.fromEntries(
    Object.entries(serverFilters).filter(([_, value]) => value !== '' && value !== null && value !== undefined)
  );
  const queryKey = ['messages', cleanFilters];

  return useQuery({
    queryKey,
    queryFn: async () => {
      const params = new URLSearchParams();

      // Convert fromDate to startTime (milliseconds)
      if (serverFilters.fromDate) {
        const fromTime = new Date(serverFilters.fromDate).getTime();
        params.append('startTime', fromTime);
      }

      // Convert toDate to endTime (milliseconds) - set to end of day
      if (serverFilters.toDate) {
        const toTime = new Date(serverFilters.toDate + 'T23:59:59.999').getTime();
        params.append('endTime', toTime);
      }

      if (serverFilters.limit) params.append('limit', serverFilters.limit);
      if (serverFilters.direction !== undefined && serverFilters.direction !== 0) params.append('direction', serverFilters.direction);
      if (serverFilters.showFinished !== undefined) params.append('showFinished', serverFilters.showFinished);
      if (serverFilters.showPending !== undefined) params.append('showPending', serverFilters.showPending);
      if (serverFilters.showStopped !== undefined) params.append('showStopped', serverFilters.showStopped);
      if (serverFilters.partnerId) params.append('partnerId', serverFilters.partnerId);
      if (serverFilters.localStationId) params.append('localStationId', serverFilters.localStationId);
      if (serverFilters.format) params.append('format', serverFilters.format);
      if (serverFilters.userId) params.append('userId', serverFilters.userId);

      const response = await api.get(`/messages?${params.toString()}`);
      return response.data;
    }
  });
}

export function useMessageDetails(messageId) {
  return useQuery({
    queryKey: ['message-details', messageId],
    queryFn: async () => {
      const response = await api.get(`/messages/${messageId}/details`);
      return response.data;
    },
    enabled: !!messageId
  });
}

export function useMessageLog(messageId) {
  return useQuery({
    queryKey: ['message-log', messageId],
    queryFn: async () => {
      const response = await api.get(`/messages/${messageId}/log`);
      return response.data;
    },
    enabled: !!messageId
  });
}

export function useMessagePayload(messageId) {
  return useMutation({
    mutationFn: async () => {
      const response = await api.get(`/messages/${messageId}/payload`, {
        responseType: 'blob'
      });
      return response.data;
    }
  });
}

// Hook for real-time message updates via Server-Sent Events
export function useMessageEvents() {
  const queryClient = useQueryClient();

  useEffect(() => {
    // Note: SSE endpoint not yet implemented in backend
    // This is a placeholder for future implementation
    const eventSource = new EventSource('/as2/api/v1/messages/events', {
      withCredentials: true
    });

    eventSource.onmessage = (event) => {
      try {
        const data = JSON.parse(event.data);
        // Invalidate messages query to trigger refetch
        queryClient.invalidateQueries(['messages']);
      } catch (error) {
        console.error('Error parsing SSE message:', error);
      }
    };

    eventSource.onerror = (error) => {
      console.error('SSE connection error:', error);
      eventSource.close();
    };

    return () => {
      eventSource.close();
    };
  }, [queryClient]);
}
