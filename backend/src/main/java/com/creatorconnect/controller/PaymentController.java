package com.creatorconnect.controller;

import com.creatorconnect.dto.request.PaymentActionRequest;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.dto.response.PaymentResponse;
import com.creatorconnect.service.PaymentService;
import com.creatorconnect.util.SecurityUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@Tag(name = "Payments", description = "Internal payment ledger, invoices, and payment history (no live gateway)")
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<PaymentResponse> initiate(@RequestParam Long applicationId, @RequestParam BigDecimal amount) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(paymentService.initiate(SecurityUtil.currentUserId(), applicationId, amount));
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasAnyRole('COMPANY','ADMIN')")
    public ResponseEntity<PaymentResponse> updateStatus(@PathVariable Long id, @Valid @RequestBody PaymentActionRequest request) {
        return ResponseEntity.ok(paymentService.updateStatus(SecurityUtil.currentUserId(), id, request));
    }

    @GetMapping("/mine")
    @PreAuthorize("hasRole('CREATOR')")
    public ResponseEntity<PageResponse<PaymentResponse>> mine(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(paymentService.getByCreator(SecurityUtil.currentUserId(), page, size));
    }

    @GetMapping("/company")
    @PreAuthorize("hasRole('COMPANY')")
    public ResponseEntity<PageResponse<PaymentResponse>> company(
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(paymentService.getByCompany(SecurityUtil.currentUserId(), page, size));
    }
}
