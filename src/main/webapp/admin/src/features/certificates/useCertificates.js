import { useQuery, useMutation, useQueryClient } from '@tanstack/react-query';
import api from '../../api/client';

export function useCertificates(keystoreType = 'sign') {
  return useQuery({
    queryKey: ['certificates', keystoreType],
    queryFn: async () => {
      const response = await api.get(`/certificates?keystoreType=${keystoreType}`);
      return response.data;
    }
  });
}

export function useExportCertificate() {
  return useMutation({
    mutationFn: async ({ alias, format, keystoreType }) => {
      const response = await api.post('/certificates/export', {
        alias,
        exportFormat: format,
        keystoreType
      }, {
        responseType: 'blob'
      });
      return response.data;
    }
  });
}

export function useGenerateCSR() {
  return useMutation({
    mutationFn: async ({ alias, format, keystoreType }) => {
      const response = await api.post('/certificates/generate-csr', {
        alias,
        requestFormat: format,
        keystoreType
      });
      return response.data;
    }
  });
}

export function useVerifyCRL() {
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async ({ keystoreType, fingerprint }) => {
      const response = await api.post('/certificates/verify-crl', {
        keystoreType,
        certificateFingerprint: fingerprint
      });
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
