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

@Entity
@Table(name = "room")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Room {
    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "name", length = 200, nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    private Date createdAt;

    @Column(name = "destroyed_at")
    private Date destroyedAt;

    @Column(name = "is_breakout", nullable = false)
    private boolean isBreakout;
}
