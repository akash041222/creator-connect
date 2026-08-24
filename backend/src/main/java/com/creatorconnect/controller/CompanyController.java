package com.creatorconnect.controller;

import com.creatorconnect.dto.request.CompanyProfileRequest;
import com.creatorconnect.dto.response.CompanyProfileResponse;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.service.CompanyService;
import com.creatorconnect.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
@Tag(name = "Companies", description = "Company profile management and public directory")
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyProfileResponse> myProfile() {
        return ResponseEntity.ok(companyService.getProfile(SecurityUtil.currentUserId()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CompanyProfileResponse> updateProfile(@Valid @RequestBody CompanyProfileRequest request) {
        return ResponseEntity.ok(companyService.updateProfile(SecurityUtil.currentUserId(), request));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<CompanyProfileResponse> publicProfile(@PathVariable Long id) {
        return ResponseEntity.ok(companyService.getPublicProfile(id));
    }

    @GetMapping("/public/search")
    public ResponseEntity<PageResponse<CompanyProfileResponse>> search(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(companyService.search(name, page, size));
    }
}
