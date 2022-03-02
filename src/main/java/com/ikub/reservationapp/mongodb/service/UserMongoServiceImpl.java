package com.ikub.reservationapp.mongodb.service;

import com.ikub.reservationapp.common.exception.NotFound;
import com.ikub.reservationapp.common.exception.ReservationApp500Exception;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.mongodb.dto.PasswordDto;
import com.ikub.reservationapp.mongodb.dto.UserMongoDto;
import com.ikub.reservationapp.mongodb.dto.UserMongoResponseDto;
import com.ikub.reservationapp.mongodb.mappers.UserMongoMapper;
import com.ikub.reservationapp.mongodb.model.NamesOnly;
import com.ikub.reservationapp.mongodb.model.PasswordResetToken;
import com.ikub.reservationapp.mongodb.model.Role;
import com.ikub.reservationapp.mongodb.model.UserMongo;
import com.ikub.reservationapp.mongodb.repository.PasswordTokenRepository;
import com.ikub.reservationapp.mongodb.repository.RoleMongoRepository;
import com.ikub.reservationapp.mongodb.repository.UserMongoRepository;
import com.ikub.reservationapp.users.exception.UserNotFoundException;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.*;

@Service
public class UserMongoServiceImpl implements UserMongoService {

    @Autowired
    private UserMongoRepository userMongoRepository;
    @Autowired
    private RoleMongoRepository roleMongoRepository;
    @Autowired
    private UserMongoMapper userMongoMapper;
    @Autowired
    private RoleService roleService;
    @Autowired
    private PasswordTokenRepository passwordTokenRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Resource(name = "bcrypt")
    private PasswordEncoder bCryptPasswordEncoder;

    @Override
    public UserMongoResponseDto saveUser(UserMongoDto userMongoDto) {
        if (userMongoRepository.existsByUsername(userMongoDto.getUsername()))
            throw new ReservationAppException("User already exists");
        if (!userMongoDto.getPassword().equalsIgnoreCase(userMongoDto.getConfirmPassword()))
            throw new ReservationAppException("Passwords do not match!");
        UserMongo userMongo = userMongoMapper.toUserMongo(userMongoDto);
        userMongo.setPassword(bCryptPasswordEncoder.encode(userMongoDto.getPassword()));
        Role role = roleService.getRoleByName(com.ikub.reservationapp.common.enums.Role.PATIENT.getRole());
        Set<String> roleSet = new HashSet<>();
        roleSet.add(role.getId());
        userMongo.setRoles(roleSet);
        return userMongoMapper.toResponseDto(userMongoRepository.save(userMongo));
    }

    @Override
    public UserMongoResponseDto saveDoctor(UserMongoDto userMongoDto) {
        if (userMongoRepository.existsByUsername(userMongoDto.getUsername()))
            throw new ReservationAppException("User already exists");
        if (!userMongoDto.getPassword().equalsIgnoreCase(userMongoDto.getConfirmPassword()))
            throw new ReservationAppException("Passwords do not match!");
        UserMongo userMongo = userMongoMapper.toUserMongo(userMongoDto);
        userMongo.setPassword(bCryptPasswordEncoder.encode(userMongoDto.getPassword()));
        Role role = roleService.getRoleByName(com.ikub.reservationapp.common.enums.Role.DOCTOR.getRole());
        Set<String> roleSet = new HashSet<>();
        roleSet.add(role.getId());
        userMongo.setRoles(roleSet);
        return userMongoMapper.toResponseDto(userMongoRepository.save(userMongo));
    }

    @Override
    public Role saveRole(Role role) {
        String javaId = UUID.randomUUID().toString();
        role.setId(javaId);
        return roleMongoRepository.save(role);
    }

    @Override
    public UserMongoResponseDto getUserByUsername(String username) {
        return userMongoMapper.toResponseDto(userMongoRepository.findByUsername(username));
    }

    @Override
    public List<NamesOnly> getByProjection() {
        return userMongoRepository.findCustomValuesAndExcludeId();
    }

    @Override
    public String resetPassword(String email) {
        val userMongo = userMongoRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(NotFound.USER.getMessage()));
        String token = UUID.randomUUID().toString();
        createPasswordResetTokenForUser(userMongo, token);
        mailSender.send(constructResetTokenEmail(token, userMongo));
        return "Email Sent...!";
    }

    public void createPasswordResetTokenForUser(UserMongo user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken();
        passwordResetToken.setToken(token);
        passwordResetToken.setUser(user.getId());
        passwordResetToken.setExpiryDate(LocalDate.now().plusDays(1));
        passwordTokenRepository.save(passwordResetToken);
    }

    private SimpleMailMessage constructResetTokenEmail(String token, UserMongo user) {
        String url = "http://localhost:8080/dental-app/user-mongo/change-password?token=" + token;
        String message = "Click the below url to reset your password...";
        SimpleMailMessage email = new SimpleMailMessage();
        email.setSubject("Reset your password");
        email.setText(message + "\r\n" + url);
        email.setTo(user.getEmail());
        return email;
    }

    @Override
    public String validatePasswordResetToken(String token) {
        final PasswordResetToken passToken = passwordTokenRepository.findByToken(token);
        if (passToken == null || isTokenExpired(passToken))
            throw new ReservationAppException("No user found or time to reset expired");
        return passToken.getToken();
    }

    private boolean isTokenExpired(PasswordResetToken passToken) {
        return passToken.getExpiryDate().isBefore(LocalDate.now());
    }

    @Override
    public String saveNewPassword(PasswordDto passwordDto) {
        val validToken = validatePasswordResetToken(passwordDto.getToken());
        val response = passwordTokenRepository.findByToken(passwordDto.getToken());
        val user = userMongoRepository.findById(response.getUser())
                .orElseThrow(() -> new UserNotFoundException(NotFound.USER.getMessage()));
        user.setPassword(bCryptPasswordEncoder.encode(passwordDto.getNewPassword()));
        userMongoRepository.save(user);
        return "Finish! Password is changed..";
    }
}
