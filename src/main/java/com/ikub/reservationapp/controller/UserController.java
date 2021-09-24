package com.ikub.reservationapp.controller;

import com.ikub.reservationapp.dto.UserDto;
import com.ikub.reservationapp.model.LoginUser;
import com.ikub.reservationapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/authenticate")
    public ResponseEntity<String> generateToken(@RequestBody LoginUser loginUser) throws AuthenticationException {
        return new ResponseEntity<>(userService.authenticate(loginUser), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto) {
        return new ResponseEntity<>(userService.save(userDto), HttpStatus.OK);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/adminping")
    public ResponseEntity<String> adminPing() {
        return new ResponseEntity<>("Only Admins Can Read This", HttpStatus.OK);
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("userping")
    public ResponseEntity<String> userPing() {
        return new ResponseEntity<>("Any User Can Read This", HttpStatus.OK);
    }
}