import { api } from './axios';
import type { DigestResponse } from '../types/digest';

/**
 * News Digest API (프론트엔드용)
 */
export const digestApi = {
  /**
   * 특정 날짜의 모든 다이제스트 조회
   */
  getDigestsByDate: async (date: string): Promise<DigestResponse[]> => {
    const response = await api.get<DigestResponse[]>(
      `/api/news/digest/date/${date}`
    );
    return response.data;
  },
};
