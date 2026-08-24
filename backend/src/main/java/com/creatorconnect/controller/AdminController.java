package com.creatorconnect.controller;

import com.creatorconnect.dto.response.ApiResponse;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.dto.response.UserResponse;
import com.creatorconnect.entity.enums.Role;
import com.creatorconnect.service.AdminService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Admin", description = "Platform administration: user & campaign moderation, verification")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/users")
    public ResponseEntity<PageResponse<UserResponse>> listUsers(
            @RequestParam(required = false) Role role,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(adminService.listUsers(role, page, size));
    }

    @PatchMapping("/users/{id}/suspend")
    public ResponseEntity<ApiResponse> suspend(@PathVariable Long id) {
        adminService.suspendUser(id);
        return ResponseEntity.ok(ApiResponse.ok("User suspended."));
    }

    @PatchMapping("/users/{id}/reactivate")
    public ResponseEntity<ApiResponse> reactivate(@PathVariable Long id) {
        adminService.reactivateUser(id);
        return ResponseEntity.ok(ApiResponse.ok("User reactivated."));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        adminService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok("User deleted."));
    }

    @PatchMapping("/companies/{id}/verify")
    public ResponseEntity<ApiResponse> verifyCompany(@PathVariable Long id) {
        adminService.verifyCompany(id);
        return ResponseEntity.ok(ApiResponse.ok("Company verified."));
    }

    @PatchMapping("/creators/{id}/verify")
    public ResponseEntity<ApiResponse> verifyCreator(@PathVariable Long id) {
        adminService.verifyCreator(id);
        return ResponseEntity.ok(ApiResponse.ok("Creator verified."));
    }

    @DeleteMapping("/campaigns/{id}")
    public ResponseEntity<ApiResponse> deleteCampaign(@PathVariable Long id) {
        adminService.deleteCampaign(id);
        return ResponseEntity.ok(ApiResponse.ok("Campaign deleted."));
    }
}
