package com.mmtq.auth_boilerplate.common.response;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaginatedApiResponse<T> {

    private List<T> content;

    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean last;
}