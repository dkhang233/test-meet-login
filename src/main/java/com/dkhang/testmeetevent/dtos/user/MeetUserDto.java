package com.dkhang.testmeetevent.dtos.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeetUserDto {
    private String avatar;
    private String name;
    private String email;
}