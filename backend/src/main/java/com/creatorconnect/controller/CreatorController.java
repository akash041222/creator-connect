package com.creatorconnect.controller;

import com.creatorconnect.dto.request.CreatorProfileRequest;
import com.creatorconnect.dto.response.CreatorProfileResponse;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.service.CreatorService;
import com.creatorconnect.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/creators")
@RequiredArgsConstructor
@Tag(name = "Creators", description = "Creator profile management, public search, leaderboard")
public class CreatorController {

    private final CreatorService creatorService;

    @GetMapping("/me")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<CreatorProfileResponse> myProfile() {
        return ResponseEntity.ok(creatorService.getProfile(SecurityUtil.currentUserId()));
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<CreatorProfileResponse> updateProfile(@RequestBody CreatorProfileRequest request) {
        return ResponseEntity.ok(creatorService.updateProfile(SecurityUtil.currentUserId(), request));
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<CreatorProfileResponse> publicProfile(@PathVariable Long id) {
        return ResponseEntity.ok(creatorService.getPublicProfile(id));
    }

    @GetMapping("/public/search")
    public ResponseEntity<PageResponse<CreatorProfileResponse>> search(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) Long minFollowers,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size) {
        return ResponseEntity.ok(creatorService.search(category, location, minFollowers, page, size));
    }

    @GetMapping("/public/leaderboard")
    public ResponseEntity<PageResponse<CreatorProfileResponse>> leaderboard(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(creatorService.leaderboard(page, size));
    }
}
