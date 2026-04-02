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
    mutationFn: async ({ keystoreType, format = 'PKCS12' }) => {
      const response = await api.post('/certificates/export-keystore', {
        keystoreType,
        format
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
