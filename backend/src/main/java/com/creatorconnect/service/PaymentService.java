package com.creatorconnect.service;

import com.creatorconnect.dto.request.PaymentActionRequest;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse initiate(Long companyUserId, Long applicationId, java.math.BigDecimal amount);
    PaymentResponse updateStatus(Long actorUserId, Long paymentId, PaymentActionRequest request);
    PageResponse<PaymentResponse> getByCreator(Long creatorUserId, int page, int size);
    PageResponse<PaymentResponse> getByCompany(Long companyUserId, int page, int size);
}
