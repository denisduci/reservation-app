package com.ikub.reservationapp.mongodb.controller;

import com.ikub.reservationapp.mongodb.dto.PasswordDto;
import com.ikub.reservationapp.mongodb.dto.UserMongoDto;
import com.ikub.reservationapp.mongodb.dto.UserMongoResponseDto;
import com.ikub.reservationapp.mongodb.model.NamesOnly;
import com.ikub.reservationapp.mongodb.model.Role;
import com.ikub.reservationapp.mongodb.model.UserMongo;
import com.ikub.reservationapp.mongodb.service.UserMongoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/user-mongo")
@Slf4j
public class UserMongoController {

    @Autowired
    private UserMongoService userMongoService;

    @PostMapping
    public ResponseEntity<UserMongoResponseDto> saveUser(@Valid @RequestBody UserMongoDto userMongoDto) {
        log.info("Registering user in mongodb...");
        return new ResponseEntity<>(userMongoService.saveUser(userMongoDto), HttpStatus.OK);
    }

    @PostMapping("/doctor")
    public ResponseEntity<UserMongoResponseDto> saveDoctor(@Valid @RequestBody UserMongoDto userMongoDto) {
        log.info("Registering doctor in mongodb...");
        return new ResponseEntity<>(userMongoService.saveDoctor(userMongoDto), HttpStatus.OK);
    }

    @PostMapping("/role")
    public ResponseEntity<Role> saveRole(@RequestBody Role role) {
        log.info("Registering role in mongodb...");
        return new ResponseEntity<>(userMongoService.saveRole(role), HttpStatus.OK);
    }

    @GetMapping("/{name}")
    public ResponseEntity<UserMongoResponseDto> getUserByUsername(@PathVariable("name") String username) {
        log.info("Retrieving user in mongodb...");
        return new ResponseEntity<>(userMongoService.getUserByUsername(username), HttpStatus.OK);
    }

    @GetMapping("/projection")
    public ResponseEntity<List<NamesOnly>> getFieldsByDbProjection() {
        log.info("Retrieving user in mongodb...");
        return new ResponseEntity<>(userMongoService.getByProjection(), HttpStatus.OK);
    }

    @PostMapping("/request-change-password")
    public ResponseEntity<String> requestResetPassword(@RequestParam("email") String email) {
        log.info("Resetting user password in mongodb...");
        return new ResponseEntity<>(userMongoService.resetPassword(email), HttpStatus.OK);
    }

    @GetMapping("/validate-change-password")
    public ResponseEntity<String> validatePasswordResetToken(@RequestParam("token") String token) {
        log.info("Validating password...");
        return new ResponseEntity<>(userMongoService.validatePasswordResetToken(token), HttpStatus.OK);
    }

    @PostMapping("/save-password")
    public ResponseEntity<String> saveNewPassword(@Valid @RequestBody PasswordDto passwordDto) {
        log.info("Saving new password...");
        return new ResponseEntity<>(userMongoService.saveNewPassword(passwordDto), HttpStatus.OK);
    }
}
