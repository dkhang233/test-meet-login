package com.dkhang.testmeetevent.services;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.management.RuntimeErrorException;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.dkhang.testmeetevent.dtos.occupant.OccupantDto;
import com.dkhang.testmeetevent.dtos.occupant.OccupantJoinedDto;
import com.dkhang.testmeetevent.dtos.occupant.OccupantLeftDto;
import com.dkhang.testmeetevent.dtos.room.RoomCreatedDto;
import com.dkhang.testmeetevent.dtos.room.RoomDestroyedDto;
import com.dkhang.testmeetevent.dtos.user.MeetUserDto;
import com.dkhang.testmeetevent.models.Occupant;
import com.dkhang.testmeetevent.models.Room;
import com.dkhang.testmeetevent.models.User;
import com.dkhang.testmeetevent.repositories.OccupantRepository;
import com.dkhang.testmeetevent.repositories.RoomRepository;
import com.dkhang.testmeetevent.repositories.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MeetService {
    private final Map<String, Object> extraClaims = new HashMap<>();

    private final JwtService jwtService;

    private final UserRepository userRepository;

    private final RoomRepository roomRepository;

    private final OccupantRepository occupantRepository;

    public void createRoom(RoomCreatedDto input) {
        Room room = Room
                .builder()
                .id(input.getRoomID())
                .name(input.getRoomName())
                .isBreakout(input.isBreakout())
                .createdAt(new Date(input.getCreatedAt() * 1000))
                .build();
        roomRepository.save(room);
    }

    public void destroyRoom(RoomDestroyedDto input) {
        try {
            roomRepository.destroyRoom(input.getRoomID(), new Date(input.getDestroyedAt() * 1000));
        } catch (Exception e) {
        }
    }

    public void handleOccupantJoined(OccupantJoinedDto input) {
        OccupantDto occupantDto = input.getOccupantDto();
        String id = occupantDto.getOccupantJID().split("@")[0];
        String email = occupantDto.getEmail();
        userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Not found user"));
        String roomID = input.getRoomID();
        roomRepository.findById(roomID).orElseThrow(() -> new RuntimeException("Not found room"));
        Occupant occupant = Occupant.builder()
                .id(id)
                .email(email)
                .roomID(roomID)
                .joinedAt(new Date(occupantDto.getJoinedAt() * 1000))
                .leftAt(new Date(occupantDto.getLeftAt() * 1000))
                .build();
        occupantRepository.save(occupant);
    }

    public void handleOccupantLeft(OccupantLeftDto input) {
        OccupantDto occupantDto = input.getOccupantDto();
        String id = occupantDto.getOccupantJID().split("@")[0];
        Date leftAt = new Date(occupantDto.getLeftAt() * 1000);
        occupantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found occupant with id = " + id));
        occupantRepository.updateOccupantLeft(id, leftAt);
    }

    public void setExtraClaims() {
        User user = new User();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication instanceof AnonymousAuthenticationToken)) {
            user = userRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Not found user"));
        } else {
            throw new RuntimeException("No User");
        }
        MeetUserDto meetUserDto = MeetUserDto
                .builder()
                .avatar("")
                .name(user.getName())
                .email(user.getEmail())
                .build();
        Map<String, Object> context = new HashMap<>();
        context.put("user", meetUserDto);
        extraClaims.put("context", context);
        extraClaims.put("room", "*");
    }

    public String generateMeetToken() {
        return jwtService.generateMeetToken(extraClaims);
    }

    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
}
