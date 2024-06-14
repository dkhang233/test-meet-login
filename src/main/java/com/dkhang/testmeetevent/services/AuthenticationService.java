package com.dkhang.testmeetevent.services;

import com.dkhang.testmeetevent.models.Status;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dkhang.testmeetevent.dtos.user.LoginUserDto;
import com.dkhang.testmeetevent.dtos.user.RegisterUserDto;
import com.dkhang.testmeetevent.models.User;
import com.dkhang.testmeetevent.repositories.UserRepository;
import com.dkhang.testmeetevent.responses.user.UserInfor;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public User signup(RegisterUserDto input) {
        User user = User
                .builder()
                .email(input.getEmail())
                .status(Status.OFFLINE)
                .role(1)
                .password(passwordEncoder.encode(input.getPassword()))
                .username(input.getUsername())
                .build();
        return userRepository.save(user);
    }

    public User setOnlineStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Not found user"));
        user.setStatus(Status.ONLINE);
        return userRepository.save(user);
    }

    public User setOfflineStatus(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Not found user"));
        user.setStatus(Status.OFFLINE);
        return userRepository.save(user);
    }

    public User authenticate(LoginUserDto loginUserDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginUserDto.getEmail(), loginUserDto.getPassword()));
        return userRepository.findByEmail(loginUserDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Not found user"));
    }

    public UserInfor getUserInfor(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Not found user"));
        int[] roles = { user.getRole() };
        return new UserInfor(user.getUsername(), user.getName(), roles);
    }
    public List<User> findConnectUsers(){
        return userRepository.findAllByStatus(Status.ONLINE);
    }


}
