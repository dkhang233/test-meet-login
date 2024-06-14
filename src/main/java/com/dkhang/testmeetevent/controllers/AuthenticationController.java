package com.dkhang.testmeetevent.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import com.dkhang.testmeetevent.dtos.user.LoginUserDto;
import com.dkhang.testmeetevent.dtos.user.RegisterUserDto;
import com.dkhang.testmeetevent.models.User;
import com.dkhang.testmeetevent.responses.ApiResponseData;
import com.dkhang.testmeetevent.responses.user.LoginResponse;
import com.dkhang.testmeetevent.responses.user.UserInfor;
import com.dkhang.testmeetevent.services.AuthenticationService;
import com.dkhang.testmeetevent.services.JwtService;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/users")
@RequiredArgsConstructor
@Slf4j
//@CrossOrigin(origins = "http://localhost:3333")
public class AuthenticationController {

    @Value("${api.prefix}")
    private String apiPrefix;
    private final JwtService jwtService;
    private final AuthenticationService authenticationService;

    @PostMapping("/signup")
    public ApiResponseData<String> register(@RequestBody RegisterUserDto user) {
        authenticationService.signup(user);
        ApiResponseData<String> response = new ApiResponseData<>(0, "", "");
        return response;
    }

    @PostMapping("/login")
    public ApiResponseData<LoginResponse> login(@RequestBody LoginUserDto user) {
        User authenticatedUser = authenticationService.authenticate(user);
        String jwtToken = jwtService.generateToken(authenticatedUser);
        ApiResponseData<LoginResponse> response = new ApiResponseData<>(0, new LoginResponse(jwtToken), "");
        return response;
    }

    @GetMapping("/code")
    public ApiResponseData<String> getCode() {
        ApiResponseData<String> response = new ApiResponseData<>(0, "abc", "");
        return response;
    }

    @GetMapping("/info")
    public ApiResponseData<UserInfor> getUserInfor(Authentication authentication) {
        UserInfor infor = authenticationService.getUserInfor(authentication);
        ApiResponseData<UserInfor> response = new ApiResponseData<>(0, infor, "");
        return response;
    }

    @GetMapping("/find-connected-users")
    public ResponseEntity<List<User>> findConnectUsers(){
        return ResponseEntity.ok(authenticationService.findConnectUsers());
    }
}
