import { useQuery } from '@tanstack/react-query';
import api from '../../api/client';

export function useCemEntries() {
  return useQuery({
    queryKey: ['cem'],
    queryFn: async () => {
      const response = await api.get('/cem');
      return response.data;
    }
  });
}
