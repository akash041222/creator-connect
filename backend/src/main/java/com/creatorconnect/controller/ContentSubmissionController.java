package com.creatorconnect.controller;

import com.creatorconnect.dto.request.ContentSubmissionRequest;
import com.creatorconnect.dto.request.SubmissionReviewRequest;
import com.creatorconnect.dto.response.ContentSubmissionResponse;
import com.creatorconnect.service.ContentSubmissionService;
import com.creatorconnect.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/submissions")
@RequiredArgsConstructor
@Tag(name = "Content Submissions", description = "Creator work uploads and company review")
public class ContentSubmissionController {

    private final ContentSubmissionService submissionService;

    @PostMapping
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<ContentSubmissionResponse> submit(@Valid @RequestBody ContentSubmissionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(submissionService.submit(SecurityUtil.currentUserId(), request));
    }

    @PatchMapping("/{id}/review")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<ContentSubmissionResponse> review(@PathVariable Long id, @Valid @RequestBody SubmissionReviewRequest request) {
        return ResponseEntity.ok(submissionService.review(SecurityUtil.currentUserId(), id, request));
    }

    @GetMapping("/application/{applicationId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ContentSubmissionResponse> byApplication(@PathVariable Long applicationId) {
        return ResponseEntity.ok(submissionService.getByApplication(applicationId));
    }
}
