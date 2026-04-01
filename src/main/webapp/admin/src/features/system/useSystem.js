import { useQuery } from '@tanstack/react-query';
import api from '../../api/client';

export function useSystemInfo() {
  return useQuery({
    queryKey: ['system-info'],
    queryFn: async () => {
      const response = await api.get('/system/info');
      return response.data;
    }
  });
}
