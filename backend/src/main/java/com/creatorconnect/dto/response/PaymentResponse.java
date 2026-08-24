package com.creatorconnect.dto.response;

import com.creatorconnect.entity.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long applicationId;
    private String campaignTitle;
    private Long creatorId;
    private String creatorName;
    private Long companyId;
    private String companyName;
    private BigDecimal amount;
    private PaymentStatus status;
    private String invoiceNumber;
    private LocalDateTime paidAt;
}
