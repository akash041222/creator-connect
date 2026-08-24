package com.creatorconnect.dto.request;

import com.creatorconnect.entity.enums.Role;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank @Email
    private String email;

    @NotBlank @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;

    @NotBlank
    private String fullName;

    private String phone;

    @NotNull
    private Role role; // COMPANY or CREATOR (ADMIN created via seed/admin tools only)

    // Optional role-specific fields at signup time
    private String companyName; // required if role == COMPANY
}
