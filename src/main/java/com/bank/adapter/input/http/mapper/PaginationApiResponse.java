package com.bank.adapter.input.http.mapper;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.data.domain.Page;

@Schema(title = "페이지네이션 정보", description = "페이지네이션 정보")
public record PaginationApiResponse(
        @Schema(description = "전체 데이터 수", example = "100")
        long totalCount,
        @Schema(description = "페이지당 데이터 수", example = "10")
        int pageSize,
        @Schema(description = "현재 페이지 번호", example = "1")
        int pageNumber,
        @Schema(description = "전체 페이지 수", example = "10")
        int totalPage
) {
    public static PaginationApiResponse of(
            Page<?> page
    ) {
        return new PaginationApiResponse(
                page.getTotalElements(),
                page.getSize(),
                page.getNumber(),
                page.getTotalPages()
        );
    }
}
