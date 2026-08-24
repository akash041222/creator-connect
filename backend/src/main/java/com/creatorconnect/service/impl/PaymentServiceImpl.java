package com.creatorconnect.service.impl;

import com.creatorconnect.dto.request.PaymentActionRequest;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.dto.response.PaymentResponse;
import com.creatorconnect.entity.Application;
import com.creatorconnect.entity.Company;
import com.creatorconnect.entity.Creator;
import com.creatorconnect.entity.Payment;
import com.creatorconnect.entity.User;
import com.creatorconnect.entity.enums.NotificationType;
import com.creatorconnect.entity.enums.PaymentStatus;
import com.creatorconnect.exception.InvalidRequestException;
import com.creatorconnect.exception.ResourceNotFoundException;
import com.creatorconnect.exception.UnauthorizedActionException;
import com.creatorconnect.repository.ApplicationRepository;
import com.creatorconnect.repository.CompanyRepository;
import com.creatorconnect.repository.CreatorRepository;
import com.creatorconnect.repository.PaymentRepository;
import com.creatorconnect.service.EmailService;
import com.creatorconnect.service.NotificationService;
import com.creatorconnect.service.PaymentService;
import com.creatorconnect.util.InvoiceGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final ApplicationRepository applicationRepository;
    private final CompanyRepository companyRepository;
    private final CreatorRepository creatorRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    @Transactional
    public PaymentResponse initiate(Long companyUserId, Long applicationId, BigDecimal amount) {
        Company company = companyRepository.findByUserId(companyUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found."));
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found."));
        if (!application.getCampaign().getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedActionException("You do not own the campaign this application belongs to.");
        }
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidRequestException("Payment amount must be greater than zero.");
        }

        Payment payment = Payment.builder()
                .application(application)
                .creator(application.getCreator())
                .company(company)
                .amount(amount)
                .status(PaymentStatus.PENDING)
                .invoiceNumber(InvoiceGenerator.next())
                .build();
        return toResponse(paymentRepository.save(payment));
    }

    @Override
    @Transactional
    public PaymentResponse updateStatus(Long actorUserId, Long paymentId, PaymentActionRequest request) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResourceNotFoundException("Payment not found."));

        // Only the owning company (or an admin, enforced at controller level) may progress payment status.
        Company company = companyRepository.findByUserId(actorUserId).orElse(null);
        if (company != null && !payment.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedActionException("You do not own this payment record.");
        }

        payment.setStatus(request.getStatus());
        payment.setPaymentNotes(request.getPaymentNotes());
        if (request.getStatus() == PaymentStatus.PAID) {
            payment.setPaidAt(java.time.LocalDateTime.now());
            Creator creator = payment.getCreator();
            creator.setTotalEarnings(creator.getTotalEarnings().add(payment.getAmount()));
            creatorRepository.save(creator);

            Company payingCompany = payment.getCompany();
            payingCompany.setTotalSpending(payingCompany.getTotalSpending().add(payment.getAmount()));
            companyRepository.save(payingCompany);

            User creatorUser = creator.getUser();
            notificationService.send(creatorUser, NotificationType.PAYMENT_RELEASED,
                    "Payment released",
                    "You received a payment of ₹" + payment.getAmount() + " (Invoice " + payment.getInvoiceNumber() + ").",
                    "/creator-dashboard.html?tab=earnings");
            emailService.sendPaymentReleasedEmail(creatorUser, "₹" + payment.getAmount(), payment.getInvoiceNumber());
        }

        return toResponse(paymentRepository.save(payment));
    }

    @Override
    public PageResponse<PaymentResponse> getByCreator(Long creatorUserId, int page, int size) {
        Creator creator = creatorRepository.findByUserId(creatorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator profile not found."));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Payment> results = paymentRepository.findByCreatorId(creator.getId(), pageable);
        return PageResponse.from(results.map(this::toResponse));
    }

    @Override
    public PageResponse<PaymentResponse> getByCompany(Long companyUserId, int page, int size) {
        Company company = companyRepository.findByUserId(companyUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found."));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Payment> results = paymentRepository.findByCompanyId(company.getId(), pageable);
        return PageResponse.from(results.map(this::toResponse));
    }

    private PaymentResponse toResponse(Payment p) {
        return PaymentResponse.builder()
                .id(p.getId())
                .applicationId(p.getApplication().getId())
                .campaignTitle(p.getApplication().getCampaign().getTitle())
                .creatorId(p.getCreator().getId())
                .creatorName(p.getCreator().getUser().getFullName())
                .companyId(p.getCompany().getId())
                .companyName(p.getCompany().getCompanyName())
                .amount(p.getAmount())
                .status(p.getStatus())
                .invoiceNumber(p.getInvoiceNumber())
                .paidAt(p.getPaidAt())
                .build();
    }
}
