package com.creatorconnect.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Uniform success-envelope for simple message/ack responses. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse {
    private boolean success;
    private String message;
    private Object data;

    public static ApiResponse ok(String message) {
        return ApiResponse.builder().success(true).message(message).build();
    }

    public static ApiResponse of(String message, Object data) {
        return ApiResponse.builder().success(true).message(message).data(data).build();
    }
}
