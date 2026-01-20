package com.bugmaker.apt.grpc;

import com.bugmaker.apt.domain.news.NewsCreateRequest;
import com.bugmaker.apt.domain.news.NewsResponse;
import com.bugmaker.apt.enums.NewsCategory;
import com.bugmaker.apt.grpc.proto.NewsProto;
import com.bugmaker.apt.grpc.proto.NewsServiceGrpc;
import com.bugmaker.apt.service.news.NewsService;
import io.grpc.Status;
import io.grpc.stub.StreamObserver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.service.GrpcService;
import org.springframework.beans.factory.annotation.Value;

/**
 * 뉴스 gRPC 서비스
 * Python 크롤러로부터 gRPC 요청 받아 처리
 */
@Slf4j
@GrpcService
@RequiredArgsConstructor
public class NewsGrpcService extends NewsServiceGrpc.NewsServiceImplBase {

    private final NewsService newsService;

    @Value("${news.crawler.api-key}")
    private String expectedApiKey;

    @Override
    public void createNews(
            NewsProto.NewsCreateRequest request,
            StreamObserver<NewsProto.NewsResponse> responseObserver) {

        try {
            log.info("[gRPC] 뉴스 등록 요청 - 제목: {}", request.getTitle());

            // Domain 객체로 변환
            NewsCreateRequest domainRequest = new NewsCreateRequest(
                    request.getTitle(),
                    request.getContent(),
                    request.getReference(),
                    request.getCategory()
            );

            // 서비스 호출
            NewsResponse domainResponse = newsService.createNews(domainRequest);

            // Proto 응답으로 변환
            NewsProto.NewsResponse protoResponse = NewsProto.NewsResponse.newBuilder()
                    .setId(domainResponse.id())
                    .setTitle(domainResponse.title())
                    .setReference(domainResponse.reference())
                    .setCategory(domainResponse.category())
                    .setViewCount(domainResponse.viewCount())
                    .setHeartCount(domainResponse.heartCount())
                    .setShareCount(domainResponse.shareCount())
                    .setReportCount(0L)  // 크롤러 등록 시에는 reportCount = 0
                    .setCreatedDate(domainResponse.createdDate().toString())
                    .build();

            responseObserver.onNext(protoResponse);
            responseObserver.onCompleted();

            log.info("[gRPC] 뉴스 등록 성공 - ID: {}", domainResponse.id());

        } catch (IllegalArgumentException e) {
            log.error("[gRPC] 요청 검증 실패: {}", e.getMessage());
            responseObserver.onError(
                    Status.INVALID_ARGUMENT
                            .withDescription(e.getMessage())
                            .asRuntimeException()
            );
        } catch (Exception e) {
            log.error("[gRPC] 뉴스 등록 실패", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("뉴스 등록 중 오류 발생")
                            .asRuntimeException()
            );
        }
    }

    @Override
    public void getCategories(
            NewsProto.Empty request,
            StreamObserver<NewsProto.GetCategoriesResponse> responseObserver) {

        try {
            log.info("[gRPC] 카테고리 목록 조회 요청");

            // NewsCategory Enum에서 모든 카테고리 정보 추출
            NewsProto.GetCategoriesResponse.Builder responseBuilder =
                    NewsProto.GetCategoriesResponse.newBuilder();

            for (NewsCategory category : NewsCategory.values()) {
                NewsProto.CategoryInfo categoryInfo = NewsProto.CategoryInfo.newBuilder()
                        .setDisplayName(category.getDisplayName())
                        .addAllKeywords(category.getKeywordList())  // 가중치 제외, 키워드만 전달
                        .build();

                responseBuilder.addCategories(categoryInfo);
            }

            NewsProto.GetCategoriesResponse response = responseBuilder.build();

            responseObserver.onNext(response);
            responseObserver.onCompleted();

            log.info("[gRPC] 카테고리 목록 조회 성공 - {} 개", NewsCategory.values().length);

        } catch (Exception e) {
            log.error("[gRPC] 카테고리 목록 조회 실패", e);
            responseObserver.onError(
                    Status.INTERNAL
                            .withDescription("카테고리 조회 중 오류 발생")
                            .asRuntimeException()
            );
        }
    }
}
