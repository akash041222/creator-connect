package com.creatorconnect.controller;

import com.creatorconnect.dto.request.CampaignRequest;
import com.creatorconnect.dto.response.ApiResponse;
import com.creatorconnect.dto.response.CampaignResponse;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.entity.enums.CampaignStatus;
import com.creatorconnect.entity.enums.Platform;
import com.creatorconnect.service.CampaignService;
import com.creatorconnect.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Public browsing endpoints (GET) live under /api/campaigns and are open to
 * everyone. Mutating endpoints require an authenticated COMPANY account and
 * are additionally guarded in the service layer to ensure a company can only
 * touch its own campaigns.
 */
@RestController
@RequestMapping("/api/campaigns")
@RequiredArgsConstructor
@Tag(name = "Campaigns", description = "Browse, create, and manage marketing campaigns")
public class CampaignController {

    private final CampaignService campaignService;

    @GetMapping
    public ResponseEntity<PageResponse<CampaignResponse>> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Platform platform,
            @RequestParam(required = false) BigDecimal minBudget,
            @RequestParam(required = false) BigDecimal maxBudget,
            @RequestParam(required = false) Long minFollowers,
            @RequestParam(required = false) LocalDate deadlineBefore,
            @RequestParam(required = false, defaultValue = "OPEN") CampaignStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String direction) {
        return ResponseEntity.ok(campaignService.search(keyword, category, platform, minBudget, maxBudget,
                minFollowers, deadlineBefore, status, page, size, sortBy, direction));
    }

    @GetMapping("/trending")
    public ResponseEntity<PageResponse<CampaignResponse>> trending(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "8") int size) {
        return ResponseEntity.ok(campaignService.getTrending(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CampaignResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(campaignService.getById(id, true));
    }

    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CampaignResponse> create(@Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(campaignService.create(SecurityUtil.currentUserId(), request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CampaignResponse> update(@PathVariable Long id, @Valid @RequestBody CampaignRequest request) {
        return ResponseEntity.ok(campaignService.update(SecurityUtil.currentUserId(), id, request));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<CampaignResponse> updateStatus(@PathVariable Long id, @RequestParam CampaignStatus status) {
        return ResponseEntity.ok(campaignService.updateStatus(SecurityUtil.currentUserId(), id, status));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ApiResponse> delete(@PathVariable Long id) {
        campaignService.delete(SecurityUtil.currentUserId(), id);
        return ResponseEntity.ok(ApiResponse.ok("Campaign deleted."));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<PageResponse<CampaignResponse>> mine(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(campaignService.getByCompany(SecurityUtil.currentUserId(), page, size));
    }
}
