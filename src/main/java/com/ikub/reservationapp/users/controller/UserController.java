package com.ikub.reservationapp.users.controller;

import com.ikub.reservationapp.common.model.AuthToken;
import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.common.model.LoginUser;
import com.ikub.reservationapp.users.dto.UserResponseDto;
import com.ikub.reservationapp.users.dto.UserUpdateDto;
import com.ikub.reservationapp.users.service.UserService;
import com.ikub.reservationapp.users.dto.UserSearchRequestDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/refreshtoken")
    public AuthToken generateRefreshToken(HttpServletRequest request) throws AuthenticationException {
        log.info("Generating refresh token...");
        return userService.generateRefreshToken(request);
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthToken> generateToken(@Valid @RequestBody LoginUser loginUser) throws AuthenticationException {
        log.info("Authenticating user...");
        return new ResponseEntity<>(userService.authenticate(loginUser), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> saveUser(@Valid @RequestBody UserDto userDto) {
        log.info("Registering user...");
        return new ResponseEntity<>(userService.save(userDto), HttpStatus.OK);
    }

    //@PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping
    public ResponseEntity<UserUpdateDto> addRoleToUser(@Valid @RequestBody UserUpdateDto userDto) {
        log.info("Updating user...");
        return new ResponseEntity<>(userService.updateUser(userDto), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDto>> getAllUsers(@RequestBody(required = false) UserSearchRequestDto userRequest) {
        log.info("Get all users...");
        return new ResponseEntity<>(userService.findAll(userRequest), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable("id") Long id) {
        log.info("Get user by id {}...", id);
        return new ResponseEntity<>(userService.findById(id), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserResponseDto>> searchUsers(@RequestBody UserSearchRequestDto userRequest) {
        return new ResponseEntity<>(userService.getUserList(userRequest), HttpStatus.OK);
    }
}