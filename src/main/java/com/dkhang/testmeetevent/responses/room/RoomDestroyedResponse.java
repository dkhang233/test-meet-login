package com.dkhang.testmeetevent.responses.room;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RoomDestroyedResponse {
    private String id;
    private Date destroyedAt;
}
