package com.creatorconnect.controller;

import com.creatorconnect.dto.request.ApplicationRequest;
import com.creatorconnect.dto.request.ApplicationReviewRequest;
import com.creatorconnect.dto.response.ApplicationResponse;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.service.ApplicationService;
import com.creatorconnect.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Creator applications to campaigns and company review actions")
public class ApplicationController {

    private final ApplicationService applicationService;

    @PostMapping
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<ApplicationResponse> apply(@Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(applicationService.apply(SecurityUtil.currentUserId(), request));
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApplicationResponse> review(@PathVariable Long id, @Valid @RequestBody ApplicationReviewRequest request) {
        return ResponseEntity.ok(applicationService.review(SecurityUtil.currentUserId(), id, request));
    }

    @PatchMapping("/{id}/withdraw")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<ApplicationResponse> withdraw(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.withdraw(SecurityUtil.currentUserId(), id));
    }

    @PatchMapping("/{id}/complete")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApplicationResponse> complete(@PathVariable Long id) {
        return ResponseEntity.ok(applicationService.markCompleted(SecurityUtil.currentUserId(), id));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<PageResponse<ApplicationResponse>> mine(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(applicationService.getByCreator(SecurityUtil.currentUserId(), page, size));
    }

    @GetMapping("/campaign/{campaignId}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<PageResponse<ApplicationResponse>> byCampaign(
            @PathVariable Long campaignId,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(applicationService.getByCampaign(SecurityUtil.currentUserId(), campaignId, page, size));
    }

    @GetMapping("/company")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<PageResponse<ApplicationResponse>> byCompany(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(applicationService.getByCompany(SecurityUtil.currentUserId(), page, size));
    }
}
