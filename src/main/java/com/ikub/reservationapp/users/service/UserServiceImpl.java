package com.ikub.reservationapp.users.service;

import java.util.*;
import java.util.stream.Collectors;
import com.ikub.reservationapp.common.enums.Role;
import com.ikub.reservationapp.common.exception.PasswordNotValidException;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.common.model.AuthToken;
import com.ikub.reservationapp.security.TokenProvider;
import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.users.dto.UserUpdateDto;
import com.ikub.reservationapp.users.entity.RoleEntity;
import com.ikub.reservationapp.users.entity.UserEntity;
import com.ikub.reservationapp.common.model.LoginUser;
import com.ikub.reservationapp.users.exception.UserNotFoundException;
import com.ikub.reservationapp.users.mapper.RoleMapper;
import com.ikub.reservationapp.users.mapper.UserMapper;
import com.ikub.reservationapp.users.repository.UserRepository;
import com.ikub.reservationapp.users.utils.PasswordValidationUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

@Slf4j
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

    public UserDetails loadUserByUsername(String username) {
        UserEntity user = userMapper.userDtoToUser(findByUsername(username));
        log.info("Inside loadUserByUsername, found user {}", user);
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(), user.getPassword(), getAuthorities(user));
    }

    private Set<SimpleGrantedAuthority> getAuthorities(UserEntity user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        return authorities;
    }

    @Override
    public AuthToken authenticate(LoginUser loginUser) throws AuthenticationException {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUser.getUsername(),
                        loginUser.getPassword()
                ));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return jwtTokenUtil.generateTokenWithAuthentication(authentication);
    }

    @Override
    public AuthToken generateRefreshToken(HttpServletRequest request) {
        return jwtTokenUtil.generateRefreshToken(request);
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
        RoleEntity role = roleService.findByName(Role.PATIENT.name());
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        userEntity.setRoles(roles);
        return userMapper.userToUserDto(userRepository.save(userEntity));
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream().map(user -> userMapper.userToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long id) {
        return userMapper.userToUserDto(userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found exception!")));
    }

    @Override
    public UserUpdateDto updateUser(UserUpdateDto userDto) {
        val user = userRepository.findById(userDto.getId())
                .orElseThrow(() -> new UsernameNotFoundException("User not found exception! "));
        Set<RoleEntity> currentRoles = user.getRoles();
        Set<RoleEntity> newRoles = userDto.getRoles().stream().map(roleDto -> {
            return roleService.findByName(roleDto.getName());
        }).collect(Collectors.toSet());
        currentRoles.addAll(newRoles);
        user.setRoles(currentRoles);

        return userMapper.userToUserUpdateDto(userRepository.save(user));
    }

    @Override
    public List<UserDto> findUsersByRole(String roleName) {
        return userRepository.findByRolesName(Role.DOCTOR.name()).stream().map
                (userEntity -> userMapper.userToUserDto(userEntity))
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findByIdAndRole(Long id, String roleName) {
        return userMapper.userToUserDto(userRepository.findByIdAndRolesName(id, roleName)
                .orElseThrow(() -> new ReservationAppException("No user was found with this id and role")));
    }

    @Override
    public UserDto findByUsername(String username) {
        return userMapper.userToUserDto(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("No user found with this username")));
    }
}