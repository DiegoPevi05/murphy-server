package com.medicapp.server.authentication.dto;

import com.medicapp.server.authentication.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserResponse {

    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String sex;
    private String profile_url;
    private Role role;
}
