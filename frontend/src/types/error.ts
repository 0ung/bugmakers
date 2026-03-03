/**
 * 백엔드 ErrorCode와 동일하게 정의
 */
export const ErrorCode = {
  // 400
  BAD_REQUEST: 400,

  // 403
  INCLUDE_IMPROPER_WORDS: 403,
  DEACTIVATED_MEMBER: 403,

  // 404
  NOT_FOUND_END_POINT: 404,
  ENTITY_NOT_FOUND: 404,
  MEMBER_NOT_FOUND: 404,
  NEWS_NOT_FOUND: 404,
  NOT_LIKED_YET: 404,
  NOT_FAVORITED_YET: 404,

  // 409
  ALREADY_LIKED: 409,
  ALREADY_FAVORITED: 409,

  // 500
  INTERNAL_SERVER_ERROR: 500,
} as const;

/**
 * 백엔드 ExceptionDto와 동일한 구조
 */
export interface ExceptionDto {
  code: number;
  message: string;
}

/**
 * 백엔드 ApiResponse<T> 구조
 */
export interface ApiResponse<T> {
  success: boolean;
  data: T | null;
  error: ExceptionDto | null;
}

/**
 * 커스텀 에러 클래스
 * axios 에러를 감싸서 백엔드 ErrorCode를 쉽게 접근
 */
export class ApiError extends Error {
  readonly code: number;
  readonly status: number;

  constructor(code: number, status: number, message: string) {
    super(message);
    this.name = 'ApiError';
    this.code = code;
    this.status = status;
  }

  static from(error: any): ApiError {
    // axios 에러에서 백엔드 에러 정보 추출
    const status = error.response?.status || 500;
    const errorData = error.response?.data?.error as ExceptionDto | undefined;
    
    const code = errorData?.code || status;
    const message = errorData?.message || error.message || '알 수 없는 오류가 발생했습니다.';

    return new ApiError(code, status, message);
  }

  /**
   * 에러 코드 체크
   */
  is(errorCode: number): boolean {
    return this.code === errorCode;
  }

  /**
   * HTTP 상태 코드 체크
   */
  hasStatus(status: number): boolean {
    return this.status === status;
  }
}
