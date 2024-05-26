package com.dkhang.testmeetevent.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "role")
public class Role {
    @Id
    @Column(name = "id", nullable = false)
    private int id;

    @Column(name = "value", nullable = false)
    private String value;
}
