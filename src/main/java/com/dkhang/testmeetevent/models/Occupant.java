package com.dkhang.testmeetevent.models;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "occupant")
public class Occupant {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "joined_at", nullable = false)
    private Date joinedAt;

    @Column(name = "left_at")
    private Date leftAt;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "room_id", nullable = false)
    private String roomID;
}
