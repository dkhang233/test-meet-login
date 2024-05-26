package com.dkhang.testmeetevent.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dkhang.testmeetevent.models.Role;

public interface RoleRepository extends JpaRepository<Role, Integer> {

}
