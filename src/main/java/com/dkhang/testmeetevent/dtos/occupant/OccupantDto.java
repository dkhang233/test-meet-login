package com.dkhang.testmeetevent.dtos.occupant;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OccupantDto {
    @JsonProperty("name")
    private String name;

    @JsonProperty("email")
    private String email;

    @JsonProperty("occupant_jid")
    private String occupantJID;

    @JsonProperty("joined_at")
    private long joinedAt;

    @JsonProperty("left_at")
    private long leftAt;
}
