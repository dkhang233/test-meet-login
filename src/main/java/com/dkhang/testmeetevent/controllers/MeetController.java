package com.dkhang.testmeetevent.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.dkhang.testmeetevent.dtos.occupant.OccupantJoinedDto;
import com.dkhang.testmeetevent.dtos.occupant.OccupantLeftDto;
import com.dkhang.testmeetevent.dtos.room.RoomCreatedDto;
import com.dkhang.testmeetevent.dtos.room.RoomDestroyedDto;
import com.dkhang.testmeetevent.models.Room;
import com.dkhang.testmeetevent.responses.ApiResponseData;
import com.dkhang.testmeetevent.responses.room.RoomDestroyedResponse;
import com.dkhang.testmeetevent.services.MeetService;

import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@RestController()
@RequestMapping("${api.prefix}/meets/")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3333")
public class MeetController {
    private final MeetService meetService;
    private final SimpMessagingTemplate template;

    @PostMapping("/events/room/created")
    public void handleRoomCreated(@RequestBody RoomCreatedDto data) {
        Room room = meetService.createRoom(data);
        template.convertAndSend("/topic/room/created", room);
    }

    @PostMapping("/events/room/destroyed")
    public void handleRoomDestroyed(@RequestBody RoomDestroyedDto data) {
        RoomDestroyedResponse room = meetService.destroyRoom(data);
        template.convertAndSend("/topic/room/destroyed", room);
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
    public ApiResponseData<String> generateMeetToken() {
        meetService.setExtraClaims();
        return meetService.generateMeetToken();
    }

    @GetMapping("/allrooms")
    public ApiResponseData<List<Room>> getAllRooms() {
        List<Room> rooms = meetService.getAllRooms();
        return new ApiResponseData<>(0, rooms, "");
    }

}
