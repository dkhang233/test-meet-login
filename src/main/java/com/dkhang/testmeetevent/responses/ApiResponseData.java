package com.dkhang.testmeetevent.responses;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ApiResponseData<T> {
    private int code;
    private T data;
    private String message;

    public ApiResponseData(int code, T data, String message) {
        this.code = code;
        this.data = data;
        this.message = message;
    }

}
