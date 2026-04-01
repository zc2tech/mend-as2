import api from '../../api/client';

export async function useSendCemRequest() {
  return async (cemData) => {
    const response = await api.post('/cem/send', cemData);
    return response.data;
  };
}

export async function useCancelCem() {
  return async (requestId) => {
    const response = await api.post('/cem/cancel', { requestId });
    return response.data;
  };
}

export async function useDeleteCem() {
  return async (requestId) => {
    const response = await api.post('/cem/delete', { requestId });
    return response.data;
  };
}
