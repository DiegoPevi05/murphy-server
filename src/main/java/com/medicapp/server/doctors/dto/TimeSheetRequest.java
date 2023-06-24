package com.medicapp.server.doctors.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeSheetRequest {
    private Integer doctor_id;
    private LocalDateTime date;
}
