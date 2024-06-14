package com.dkhang.testmeetevent.responses.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoginResponse {
    private String token;
}
