package com.medicapp.server.doctors.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TimeSheetResponse {
    private Integer id;
    private Integer doctor_id;
    private LocalDateTime date;

}
