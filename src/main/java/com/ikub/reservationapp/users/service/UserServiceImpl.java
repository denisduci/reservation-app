package com.ikub.reservationapp.users.service;

import java.util.*;
import java.util.stream.Collectors;
import com.ikub.reservationapp.common.enums.Role;
import com.ikub.reservationapp.common.exception.BadRequest;
import com.ikub.reservationapp.common.exception.NotFound;
import com.ikub.reservationapp.common.exception.PasswordNotValidException;
import com.ikub.reservationapp.common.exception.ReservationAppException;
import com.ikub.reservationapp.common.model.AuthToken;
import com.ikub.reservationapp.security.TokenProvider;
import com.ikub.reservationapp.users.constants.Constants;
import com.ikub.reservationapp.users.dto.UserDto;
import com.ikub.reservationapp.users.dto.UserResponseDto;
import com.ikub.reservationapp.users.dto.UserUpdateDto;
import com.ikub.reservationapp.users.entity.RoleEntity;
import com.ikub.reservationapp.users.entity.UserEntity;
import com.ikub.reservationapp.common.model.LoginUser;
import com.ikub.reservationapp.users.exception.UserNotFoundException;
import com.ikub.reservationapp.users.mapper.RoleMapper;
import com.ikub.reservationapp.users.mapper.UserMapper;
import com.ikub.reservationapp.users.repository.UserRepository;
import com.ikub.reservationapp.users.dto.UserSearchRequestDto;
import com.ikub.reservationapp.users.specifications.UserSpecification;
import com.ikub.reservationapp.users.validators.PasswordValidationUtil;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
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
    private UserSpecification userSpecification;

    public UserDetails loadUserByUsername(String username) {
        UserEntity user = userMapper.toEntity(findByUsername(username));
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
    public UserDto save(UserDto userDto) throws PasswordNotValidException, ReservationAppException {
        val userEntity = userMapper.toEntity(userDto);
        //1 - CHECK IF USER EXISTS | THROW EXCEPTION
        userRepository.findByUsername(userDto.getUsername()).ifPresent(user -> {
            throw new ReservationAppException(BadRequest.USER_EXISTS.getMessage());
        });

        //2 - CHECK PASSWORD VALIDATION | THROW EXCEPTION
        if (!PasswordValidationUtil.isValid(userDto.getPassword())) {
            throw new PasswordNotValidException(Arrays.asList(BadRequest.PASSWORD_SECURITY_FAIL.getMessage()));
        }

        //3 - CHECK PASSWORD MATCH | THROW EXCEPTION
        if (!PasswordValidationUtil.isPasswordMatch(userDto.getPassword(), userDto.getConfirmPassword())) {
            throw new PasswordNotValidException(Arrays.asList(BadRequest.PASSWORD_MATCH_FAIL.getMessage()));
        }
        userEntity.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        RoleEntity role = roleService.findByName(Role.PATIENT.name());
        Set<RoleEntity> roles = new HashSet<>();
        roles.add(role);
        userEntity.setRoles(roles);
        return userMapper.toDto(userRepository.save(userEntity));
    }

    @Override
    public List<UserResponseDto> findAll(UserSearchRequestDto userRequest) {
        if (!Optional.ofNullable(userRequest).isPresent())
            return userRepository.findAll().stream().map(userMapper::toResponseDto)
                    .collect(Collectors.toList());
        if (userRequest.getPageSize() == null)
            userRequest.setPageSize(Constants.DEFAULT_PAGE_SIZE);
        if (userRequest.getPageNumber() == null)
            userRequest.setPageNumber(Constants.DEFAULT_PAGE_NUMBER);
        Pageable pageRequest = PageRequest.of(userRequest.getPageNumber() - 1, userRequest.getPageSize());

        return userRepository.findAll(pageRequest).stream().map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long id) throws UserNotFoundException {
        return userMapper.toDto(userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(NotFound.USER.getMessage())));
    }

    @Override
    public UserUpdateDto updateUser(UserUpdateDto userDto) {
        val user = userMapper.toEntity(findById(userDto.getId()));
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
                (userMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public UserDto findByIdAndRole(Long id, String roleName) throws ReservationAppException {
        return userMapper.toDto(userRepository.findByIdAndRolesName(id, roleName)
                .orElseThrow(() -> new ReservationAppException(NotFound.USER_WITH_ROLE.getMessage())));
    }

    @Override
    public UserDto findByUsername(String username) throws UserNotFoundException {
        return userMapper.toDto(userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(NotFound.USERNAME.getMessage())));
    }

    @Override
    public String getUsernameFromContext() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        return userDetails.getUsername();
    }

    @Override
    public List<UserResponseDto> getUserList(UserSearchRequestDto userRequest) {
        List<UserEntity> usersMatched;
        Page<UserEntity> pages;
        if (userRequest.getPageNumber() == null) {
            pages = new PageImpl<>(userRepository.findAll(userSpecification.getUsers(userRequest)));
        } else {
            if (userRequest.getPageSize() == null) {
                userRequest.setPageSize(10);
            }
            Pageable paging = PageRequest.of(userRequest.getPageNumber() - 1, userRequest.getPageSize());
            pages = userRepository.findAll(userSpecification.getUsers(userRequest), paging);
        }
        if (pages != null && pages.getContent() != null) {
            usersMatched = pages.getContent();
            if (usersMatched != null && usersMatched.size() > 0) {
                List<UserResponseDto> responseDtos = usersMatched.stream().map(userMapper::toResponseDto)
                        .collect(Collectors.toList());
                return responseDtos;
            }
        }
        throw new UserNotFoundException(NotFound.USER.getMessage());
    }
}