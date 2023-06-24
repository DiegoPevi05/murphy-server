package com.medicapp.server.doctors.utils;

import com.medicapp.server.doctors.dto.DoctorResponse;
import com.medicapp.server.doctors.dto.TimeSheetResponse;
import com.medicapp.server.doctors.model.Doctor;
import com.medicapp.server.doctors.model.TimeSheet;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimeSheetHelper {

    public TimeSheetResponse mapToTimeSheetResponse(TimeSheet timeSheet){
        return TimeSheetResponse.builder()
                .id(timeSheet.getId())
                .doctor_id(timeSheet.getDoctor().getId())
                .date(timeSheet.getDate())
                .build();
    }
}
