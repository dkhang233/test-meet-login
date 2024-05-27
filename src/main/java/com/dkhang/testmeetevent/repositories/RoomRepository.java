package com.dkhang.testmeetevent.repositories;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.dkhang.testmeetevent.models.Room;

import jakarta.transaction.Transactional;

public interface RoomRepository extends JpaRepository<Room, String> {

    @Modifying
    @Transactional
    @Query("UPDATE Room r SET r.destroyedAt=:destroyedAt WHERE r.id = :id")
    int destroyRoom(@Param("id") String id, @Param("destroyedAt") Date destroyedAt);

    @Query("SELECT r FROM Room r ORDER BY r.createdAt")
    List<Room> findAll();
    // @Modifying
    // @Transactional
    // @Query("")
    // int handleOccupantJoined();
    // @Modifying
    // @Query("UPDATE Room r SET r.isBreakout=:isBreakout WHERE r.name='bcd'")
    // int changeBreakout(@Param("isBreakout") boolean isBreakout);
}
