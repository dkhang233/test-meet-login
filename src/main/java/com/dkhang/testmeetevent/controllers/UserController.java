package com.dkhang.testmeetevent.controllers;

import com.dkhang.testmeetevent.models.User;
import com.dkhang.testmeetevent.responses.user.EmailResponse;
import com.dkhang.testmeetevent.services.AuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.web.bind.annotation.RestController;



@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {
    private final AuthenticationService authenticationService;
    @MessageMapping("/user.setOnlineStatus/{email}")
    @SendTo("/topic/public")//gửi người dùng này đến nhóm những người dùng
    public User saveUser(@Payload EmailResponse emailResponse){ // trong socket thì request đc coi là 1 payload
        User user = authenticationService.setOnlineStatus(emailResponse.getEmail());
        System.out.println(emailResponse.getEmail() +" SET ONLINE STATUS");
        return user;

        //co the thay the @SendTo cho dong duoi day
//        simpMessagingTemplate.convertAndSend("/app/"+user.getNickName(),user.getNickName());
    }

    @MessageMapping("/user.disconnectUser/{nickname}")
    @SendTo("/topic/public")//gửi người dùng này đến nhóm những người dùng
    public User disconnect(@Payload EmailResponse emailResponse){ // trong socket thì request đc coi là 1 payload
        User user = authenticationService.setOfflineStatus(emailResponse.getEmail());

        //co the thay the @SendTo cho dong duoi day
//        simpMessagingTemplate.convertAndSend("/app/"+user.getNickName(),user.getNickName());
        System.out.println(emailResponse.getEmail() +" SET ONLINE STATUS");
        return user;
    }
}
