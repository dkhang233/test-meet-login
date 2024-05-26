package com.dkhang.testmeetevent.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInfor {
    private String email;
    private String username;
    private int[] roles;
}
