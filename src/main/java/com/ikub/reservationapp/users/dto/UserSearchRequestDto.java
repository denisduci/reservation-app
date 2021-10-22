package com.ikub.reservationapp.users.dto;

import lombok.Data;

@Data
public class UserSearchRequestDto {
    private String email;
    private String name;
    private Integer pageNumber;
    private Integer pageSize;
}
