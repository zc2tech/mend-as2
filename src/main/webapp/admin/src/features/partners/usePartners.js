import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '../../api/client';

export function usePartners() {
  return useQuery({
    queryKey: ['partners'],
    queryFn: async () => {
      const response = await api.get('/partners');
      return response.data;
    }
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
    mutationFn: async (id) => {
      const response = await api.delete(`/partners/${id}`);
      return response.data;
    },
    onSuccess: () => {
      queryClient.invalidateQueries(['partners']);
    }
  });
}
