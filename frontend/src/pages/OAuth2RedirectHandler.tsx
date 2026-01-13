import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

/**
 * OAuth2 로그인 성공 후 콜백 처리
 * Spring Security가 쿠키로 JWT를 전달하면 메인 페이지로 이동
 */
export default function OAuth2RedirectHandler() {
    const navigate = useNavigate();

    useEffect(() => {
        // 쿠키에서 accessToken 확인
        const hasAccessToken = document.cookie.includes('accessToken');

        if (hasAccessToken) {
            console.log('✅ OAuth2 로그인 성공 - 메인으로 이동');
            // 메인 페이지로 이동
            navigate('/', { replace: true });
        } else {
            console.error('❌ OAuth2 로그인 실패 - 로그인 페이지로 이동');
            // 로그인 실패 시 로그인 페이지로
            navigate('/login', { replace: true });
        }
    }, [navigate]);

    return (
        <div style={{
            display: 'flex',
            justifyContent: 'center',
            alignItems: 'center',
            height: '100vh'
        }}>
            <div>
                <h2>로그인 처리 중...</h2>
                <p>잠시만 기다려주세요.</p>
            </div>
        </div>
    );
}