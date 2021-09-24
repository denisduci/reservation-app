package com.ikub.reservationapp.service;

import java.util.*;
import java.util.stream.Collectors;
import com.ikub.reservationapp.config.TokenProvider;
import com.ikub.reservationapp.dto.UserDto;
import com.ikub.reservationapp.entity.Role;
import com.ikub.reservationapp.entity.User;
import com.ikub.reservationapp.mapper.MapStructMapper;
import com.ikub.reservationapp.model.LoginUser;
import com.ikub.reservationapp.repository.UserRepository;
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
    private MapStructMapper mapStructMapper;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenProvider jwtTokenUtil;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = Optional.of(userRepository.findByUsername(username))
                .orElseThrow(()-> new UsernameNotFoundException("Invalid username or password"));

        //User user = userRepository.findByUsername(username);
//        if(user == null){
//            throw new UsernameNotFoundException("Invalid username or password.");
//        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), getAuthority(user));
    }

    private Set<SimpleGrantedAuthority> getAuthority(User user) {
        Set<SimpleGrantedAuthority> authorities = new HashSet<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.getName()));
        });
        return authorities;
    }

    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream().map(user -> mapStructMapper.userToUserDto(user))
                .collect(Collectors.toList());
    }

    @Override
    public User findOne(String username) {
        return userRepository.findByUsername(username);
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
        val user = mapStructMapper.userDtoToUser(userDto);
        user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        Role role = roleService.findByName(com.ikub.reservationapp.enums.Role.USER.name());
        Set<Role> roleSet = new HashSet<>();
        roleSet.add(role);
//        if (user.getEmail().split("@")[1].equals("admin")) {
//            role = roleService.findByName(com.ikub.reservationapp.enums.Role.ADMIN.name());
//            roleSet.add(role);
//        }
        user.setRoles(roleSet);
        return mapStructMapper.userToUserDto(userRepository.save(user));
    }
}