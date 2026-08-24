package com.creatorconnect.dto.request;

import com.creatorconnect.entity.enums.PaymentStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PaymentActionRequest {
    @NotNull
    private PaymentStatus status; // APPROVED, PAID, REJECTED
    private String paymentNotes;
}
