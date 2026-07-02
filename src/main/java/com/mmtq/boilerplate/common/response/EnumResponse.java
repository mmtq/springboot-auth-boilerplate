package com.mmtq.boilerplate.common.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnumResponse {

    private String name;
    private String constant;
}