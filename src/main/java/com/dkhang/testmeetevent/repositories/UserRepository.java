package com.dkhang.testmeetevent.repositories;

import java.util.List;
import java.util.Optional;

import com.dkhang.testmeetevent.models.Status;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.dkhang.testmeetevent.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);

    List<User> findAllByStatus(Status status);
}
