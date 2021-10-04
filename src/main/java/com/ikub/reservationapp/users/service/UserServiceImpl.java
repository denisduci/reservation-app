package com.ikub.reservationapp.users.service;

import java.util.*;
import java.util.stream.Collectors;
import com.ikub.reservationapp.common.enums.Role;
import com.ikub.reservationapp.common.exception.PasswordNotValidException;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.security.TokenProvider;
import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.users.dto.UserUpdateDto;
import com.ikub.reservationapp.users.entity.RoleEntity;
import com.ikub.reservationapp.users.entity.UserEntity;
import com.ikub.reservationapp.common.model.LoginUser;
import com.ikub.reservationapp.users.mapper.RoleMapper;
import com.ikub.reservationapp.users.mapper.UserMapper;
import com.ikub.reservationapp.users.repository.UserRepository;
import com.ikub.reservationapp.users.utils.PasswordValidationUtil;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service(value = "userService")
public class UserServiceImpl implements UserDetailsService, UserService {

    @Autowired
    private RoleService roleService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RoleMapper roleMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider jwtTokenUtil;

    @Autowired
    private PasswordValidationUtil passwordValidation;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = Optional.of(userRepository.findByUsername(username))
                .orElseThrow(() -> new UsernameNotFoundException("Invalid username or password"));
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(UserEntity user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        return authorities;
    }

    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream().map(user -> userMapper.userToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserEntity findOne(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public UserUpdateDto updateUser(UserUpdateDto userDto) {
        val user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new ReservationAppException("user not found "));
        Set<RoleEntity> currentRoles = user.getRoles();
        Set<RoleEntity> newRoles = userDto.getRoles().stream().map(roleDto -> {
            return roleService.findByName(roleDto.getName());
        }).collect(Collectors.toSet());
        currentRoles.addAll(newRoles);
        user.setRoles(currentRoles);

        return userMapper.userToUserUpdateDto(userRepository.save(user));
    }

    @Override
    public String authenticate(LoginUser loginUser) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenUtil.generateToken(authentication);
    }

    @Override
    public UserDto save(UserDto userDto) {
        val userEntity = userMapper.userDtoToUser(userDto);
        //1 - CHECK IF USER EXISTS | THROW EXCEPTION
        Optional.ofNullable(userRepository.findByUsername(userDto.getUsername()))
                .ifPresent(user -> {
                    throw new ReservationAppException("User with username already exists!");
                });
        //2 - CHECK PASSWORD VALIDATION | THROW EXCEPTION
        if (!passwordValidation.isValid(userDto.getPassword())) {
            throw new PasswordNotValidException(Arrays.asList("Password doesn't meet security!"));
        }
        //        Optional.ofNullable(passwordUtil.checkPasswordValidation(userDto.getPassword()))
//                .ifPresent(password -> {
//                    throw new PasswordNotValidException(password);
//                });

        //3 - CHECK PASSWORD MATCH | THROW EXCEPTION
        if (!passwordValidation.isPasswordMatch(userDto.getPassword(), userDto.getConfirmPassword())) {
            throw new PasswordNotValidException(Arrays.asList("Passwords do not match!"));
        }
        userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        RoleEntity role = roleService.findByName(Role.USER.name());
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
//        if (user.getEmail().split("@")[1].equals("admin")) {
//            role = roleService.findByName(Role.ADMIN.name());
//            roleSet.add(role);
//        }
        userEntity.setRoles(roles);
        return userMapper.userToUserDto(userRepository.save(userEntity));
    }
}