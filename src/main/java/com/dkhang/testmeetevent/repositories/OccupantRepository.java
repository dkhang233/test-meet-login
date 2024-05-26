package com.dkhang.testmeetevent.repositories;

import java.util.Date;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dkhang.testmeetevent.models.Occupant;

import jakarta.transaction.Transactional;

public interface OccupantRepository extends JpaRepository<Occupant, String> {

    @Modifying
    @Transactional
    @Query("UPDATE Occupant ocp SET ocp.leftAt = :leftAt WHERE ocp.id = :id")
    int updateOccupantLeft(@Param("id") String id, @Param("leftAt") Date leftAt);
}
