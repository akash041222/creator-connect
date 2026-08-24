package com.creatorconnect.dto.response;

import com.creatorconnect.entity.enums.Role;
import com.creatorconnect.entity.enums.UserStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String email;
    private String fullName;
    private String phone;
    private Role role;
    private UserStatus status;
    private boolean emailVerified;
    private LocalDateTime createdAt;
}
