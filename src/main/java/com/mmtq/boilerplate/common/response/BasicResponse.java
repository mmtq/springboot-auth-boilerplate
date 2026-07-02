package com.mmtq.boilerplate.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BasicResponse {
    private Long id;
    private String name;
}
