package com.ikub.reservationapp.common.model;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ExceptionMessage {
    private String message;
    private List<String> details;
}