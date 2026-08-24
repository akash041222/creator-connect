package com.creatorconnect.service.impl;

import com.creatorconnect.dto.request.ApplicationRequest;
import com.creatorconnect.dto.request.ApplicationReviewRequest;
import com.creatorconnect.dto.response.ApplicationResponse;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.entity.*;
import com.creatorconnect.entity.enums.ApplicationStatus;
import com.creatorconnect.entity.enums.CampaignStatus;
import com.creatorconnect.entity.enums.NotificationType;
import com.creatorconnect.exception.DuplicateResourceException;
import com.creatorconnect.exception.InvalidRequestException;
import com.creatorconnect.exception.ResourceNotFoundException;
import com.creatorconnect.exception.UnauthorizedActionException;
import com.creatorconnect.repository.*;
import com.creatorconnect.service.ApplicationService;
import com.creatorconnect.service.EmailService;
import com.creatorconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ApplicationServiceImpl implements ApplicationService {

    private final ApplicationRepository applicationRepository;
    private final CampaignRepository campaignRepository;
    private final CreatorRepository creatorRepository;
    private final CompanyRepository companyRepository;
    private final NotificationService notificationService;
    private final EmailService emailService;

    @Override
    @Transactional
    public ApplicationResponse apply(Long creatorUserId, ApplicationRequest request) {
        Creator creator = creatorRepository.findByUserId(creatorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator profile not found."));

        Campaign campaign = campaignRepository.findById(request.getCampaignId())
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found: " + request.getCampaignId()));

        if (campaign.getStatus() != CampaignStatus.OPEN) {
            throw new InvalidRequestException("This campaign is not accepting applications.");
        }
        applicationRepository.findByCampaignIdAndCreatorId(campaign.getId(), creator.getId())
                .ifPresent(a -> { throw new DuplicateResourceException("You have already applied to this campaign."); });

        Application application = Application.builder()
                .campaign(campaign)
                .creator(creator)
                .message(request.getMessage())
                .portfolioLink(request.getPortfolioLink())
                .expectedTimeline(request.getExpectedTimeline())
                .status(ApplicationStatus.PENDING)
                .build();
        application = applicationRepository.save(application);

        User companyOwner = campaign.getCompany().getUser();
        notificationService.send(companyOwner, NotificationType.APPLICATION_RECEIVED,
                "New application received",
                creator.getUser().getFullName() + " applied to \"" + campaign.getTitle() + "\"",
                "/company-dashboard.html?tab=applications&campaign=" + campaign.getId());

        return toResponse(application);
    }

    @Override
    @Transactional
    public ApplicationResponse review(Long companyUserId, Long applicationId, ApplicationReviewRequest request) {
        Application application = applicationOwnedByCompany(companyUserId, applicationId);

        if (request.getStatus() != ApplicationStatus.SHORTLISTED
                && request.getStatus() != ApplicationStatus.ACCEPTED
                && request.getStatus() != ApplicationStatus.REJECTED) {
            throw new InvalidRequestException("Status must be SHORTLISTED, ACCEPTED, or REJECTED.");
        }

        application.setStatus(request.getStatus());
        application.setRejectionReason(request.getRejectionReason());
        application.setReviewedAt(java.time.LocalDateTime.now());
        application = applicationRepository.save(application);

        User creatorUser = application.getCreator().getUser();
        NotificationType type = switch (request.getStatus()) {
            case ACCEPTED -> NotificationType.APPLICATION_ACCEPTED;
            case REJECTED -> NotificationType.APPLICATION_REJECTED;
            default -> NotificationType.APPLICATION_SHORTLISTED;
        };
        notificationService.send(creatorUser, type,
                "Application " + request.getStatus().name().toLowerCase(),
                "Your application to \"" + application.getCampaign().getTitle() + "\" is now " + request.getStatus() + ".",
                "/creator-dashboard.html?tab=applications");
        emailService.sendApplicationStatusEmail(creatorUser, application.getCampaign().getTitle(), request.getStatus().name());

        return toResponse(application);
    }

    @Override
    @Transactional
    public ApplicationResponse withdraw(Long creatorUserId, Long applicationId) {
        Creator creator = creatorRepository.findByUserId(creatorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator profile not found."));
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found."));
        if (!application.getCreator().getId().equals(creator.getId())) {
            throw new UnauthorizedActionException("You do not own this application.");
        }
        application.setStatus(ApplicationStatus.WITHDRAWN);
        return toResponse(applicationRepository.save(application));
    }

    @Override
    public PageResponse<ApplicationResponse> getByCreator(Long creatorUserId, int page, int size) {
        Creator creator = creatorRepository.findByUserId(creatorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator profile not found."));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Application> results = applicationRepository.findByCreatorId(creator.getId(), pageable);
        return PageResponse.from(results.map(this::toResponse));
    }

    @Override
    public PageResponse<ApplicationResponse> getByCampaign(Long companyUserId, Long campaignId, int page, int size) {
        Company company = companyRepository.findByUserId(companyUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found."));
        Campaign campaign = campaignRepository.findById(campaignId)
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found."));
        if (!campaign.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedActionException("You do not own this campaign.");
        }
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Application> results = applicationRepository.findByCampaignId(campaignId, pageable);
        return PageResponse.from(results.map(this::toResponse));
    }

    @Override
    public PageResponse<ApplicationResponse> getByCompany(Long companyUserId, int page, int size) {
        Company company = companyRepository.findByUserId(companyUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found."));
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Application> results = applicationRepository.findByCampaignCompanyId(company.getId(), pageable);
        return PageResponse.from(results.map(this::toResponse));
    }

    @Override
    @Transactional
    public ApplicationResponse markCompleted(Long companyUserId, Long applicationId) {
        Application application = applicationOwnedByCompany(companyUserId, applicationId);
        application.setStatus(ApplicationStatus.COMPLETED);
        application = applicationRepository.save(application);

        Creator creator = application.getCreator();
        creator.setCompletedCampaignsCount(creator.getCompletedCampaignsCount() + 1);
        long total = applicationRepository.findByCreatorId(creator.getId(), Pageable.unpaged()).getTotalElements();
        if (total > 0) {
            creator.setSuccessRate(Math.round((creator.getCompletedCampaignsCount() * 100.0 / total) * 100.0) / 100.0);
        }
        creatorRepository.save(creator);

        return toResponse(application);
    }

    // ---------- helpers ----------

    private Application applicationOwnedByCompany(Long companyUserId, Long applicationId) {
        Company company = companyRepository.findByUserId(companyUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found."));
        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("Application not found."));
        if (!application.getCampaign().getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedActionException("You do not own the campaign this application belongs to.");
        }
        return application;
    }

    private ApplicationResponse toResponse(Application a) {
        return ApplicationResponse.builder()
                .id(a.getId())
                .campaignId(a.getCampaign().getId())
                .campaignTitle(a.getCampaign().getTitle())
                .creatorId(a.getCreator().getId())
                .creatorName(a.getCreator().getUser().getFullName())
                .creatorProfilePicUrl(a.getCreator().getProfilePictureUrl())
                .creatorFollowers(a.getCreator().getFollowerCount())
                .message(a.getMessage())
                .portfolioLink(a.getPortfolioLink())
                .expectedTimeline(a.getExpectedTimeline())
                .status(a.getStatus())
                .rejectionReason(a.getRejectionReason())
                .createdAt(a.getCreatedAt())
                .build();
    }
}
