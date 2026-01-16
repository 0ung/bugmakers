package com.bugmaker.apt.grpc;

import io.grpc.*;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.interceptor.GrpcGlobalServerInterceptor;
import org.springframework.beans.factory.annotation.Value;

/**
 * gRPC 서버 인터셉터
 * API Key 인증 처리
 */
@Slf4j
@GrpcGlobalServerInterceptor
public class GrpcServerInterceptor implements ServerInterceptor {

    @Value("${news.crawler.api-key}")
    private String expectedApiKey;

    @Override
    public <ReqT, RespT> ServerCall.Listener<ReqT> interceptCall(
            ServerCall<ReqT, RespT> call,
            Metadata headers,
            ServerCallHandler<ReqT, RespT> next) {

        // API Key 확인
        String apiKey = headers.get(Metadata.Key.of("x-api-key", Metadata.ASCII_STRING_MARSHALLER));

        if (apiKey == null || !apiKey.equals(expectedApiKey)) {
            log.warn("[gRPC] 인증 실패 - API Key 불일치");
            call.close(Status.UNAUTHENTICATED.withDescription("유효하지 않은 API Key"), new Metadata());
            return new ServerCall.Listener<>() {};
        }

        log.debug("[gRPC] 인증 성공");
        return next.startCall(call, headers);
    }
}
