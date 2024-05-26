package com.dkhang.testmeetevent.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.dkhang.testmeetevent.dtos.occupant.OccupantJoinedDto;
import com.dkhang.testmeetevent.dtos.occupant.OccupantLeftDto;
import com.dkhang.testmeetevent.dtos.room.RoomCreatedDto;
import com.dkhang.testmeetevent.dtos.room.RoomDestroyedDto;
import com.dkhang.testmeetevent.models.Room;
import com.dkhang.testmeetevent.responses.ApiResponseData;
import com.dkhang.testmeetevent.services.MeetService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

@RestController()
@RequestMapping("${api.prefix}/meets/")
@RequiredArgsConstructor
public class MeetController {
    private final MeetService meetService;

    @PostMapping("/events/room/created")
    public void handleRoomCreated(@RequestBody RoomCreatedDto data) {
        meetService.createRoom(data);
    }

    @PostMapping("/events/room/destroyed")
    public void handleRoomDestroyed(@RequestBody RoomDestroyedDto data) {
        meetService.destroyRoom(data);
    }

    @PostMapping("/events/occupant/joined")
    public void handleOccupantJoined(@RequestBody OccupantJoinedDto data) {
        meetService.handleOccupantJoined(data);
    }

    @PostMapping("/events/occupant/left")
    public void handleOccupantLeft(@RequestBody OccupantLeftDto data) {
        meetService.handleOccupantLeft(data);
    }

    @PostMapping("/token")
    public String generateMeetToken() {
        meetService.setExtraClaims();
        return meetService.generateMeetToken();
    }

    @GetMapping("/allrooms")
    public ApiResponseData<List<Room>> getAllRooms() {
        List<Room> rooms = meetService.getAllRooms();
        return new ApiResponseData<>(0, rooms, "");
    }

}
