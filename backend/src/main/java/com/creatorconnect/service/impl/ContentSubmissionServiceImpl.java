package com.creatorconnect.service.impl;

import com.creatorconnect.dto.request.ContentSubmissionRequest;
import com.creatorconnect.dto.request.SubmissionReviewRequest;
import com.creatorconnect.dto.response.ContentSubmissionResponse;
import com.creatorconnect.entity.Application;
import com.creatorconnect.entity.ContentSubmission;
import com.creatorconnect.entity.User;
import com.creatorconnect.entity.enums.ApplicationStatus;
import com.creatorconnect.entity.enums.NotificationType;
import com.creatorconnect.entity.enums.SubmissionStatus;
import com.creatorconnect.exception.DuplicateResourceException;
import com.creatorconnect.exception.InvalidRequestException;
import com.creatorconnect.exception.ResourceNotFoundException;
import com.creatorconnect.exception.UnauthorizedActionException;
import com.creatorconnect.repository.ApplicationRepository;
import com.creatorconnect.repository.ContentSubmissionRepository;
import com.creatorconnect.repository.CreatorRepository;
import com.creatorconnect.repository.CompanyRepository;
import com.creatorconnect.service.ContentSubmissionService;
import com.creatorconnect.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentSubmissionServiceImpl implements ContentSubmissionService {

    private final ContentSubmissionRepository submissionRepository;
    private final ApplicationRepository applicationRepository;
    private final CreatorRepository creatorRepository;
    private final CompanyRepository companyRepository;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public ContentSubmissionResponse submit(Long creatorUserId, ContentSubmissionRequest request) {
        var creator = creatorRepository.findByUserId(creatorUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator profile not found."));

        Application application = applicationRepository.findById(request.getApplicationId())
                .orElseThrow(() -> new ResourceNotFoundException("Application not found."));

        if (!application.getCreator().getId().equals(creator.getId())) {
            throw new UnauthorizedActionException("You do not own this application.");
        }
        if (application.getStatus() != ApplicationStatus.ACCEPTED) {
            throw new InvalidRequestException("You can only submit work for an ACCEPTED application.");
        }
        submissionRepository.findByApplicationId(application.getId())
                .ifPresent(s -> { throw new DuplicateResourceException("Work has already been submitted for this application."); });

        ContentSubmission submission = ContentSubmission.builder()
                .application(application)
                .instagramReelLink(request.getInstagramReelLink())
                .youtubeLink(request.getYoutubeLink())
                .tiktokLink(request.getTiktokLink())
                .driveLink(request.getDriveLink())
                .comments(request.getComments())
                .status(SubmissionStatus.SUBMITTED)
                .build();
        submission = submissionRepository.save(submission);

        User companyOwner = application.getCampaign().getCompany().getUser();
        notificationService.send(companyOwner, NotificationType.SUBMISSION_UPLOADED,
                "New content submitted",
                creator.getUser().getFullName() + " submitted work for \"" + application.getCampaign().getTitle() + "\"",
                "/company-dashboard.html?tab=submissions&application=" + application.getId());

        return toResponse(submission);
    }

    @Override
    @Transactional
    public ContentSubmissionResponse review(Long companyUserId, Long submissionId, SubmissionReviewRequest request) {
        var company = companyRepository.findByUserId(companyUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found."));

        ContentSubmission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Submission not found."));

        if (!submission.getApplication().getCampaign().getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedActionException("You do not own the campaign this submission belongs to.");
        }

        submission.setStatus(request.getStatus());
        submission.setReviewComments(request.getReviewComments());
        submission.setReviewedAt(java.time.LocalDateTime.now());
        submission = submissionRepository.save(submission);

        User creatorUser = submission.getApplication().getCreator().getUser();
        NotificationType type = request.getStatus() == SubmissionStatus.APPROVED
                ? NotificationType.SUBMISSION_APPROVED : NotificationType.SUBMISSION_REJECTED;
        notificationService.send(creatorUser, type,
                "Submission " + request.getStatus().name().toLowerCase().replace('_', ' '),
                "Your submission for \"" + submission.getApplication().getCampaign().getTitle() + "\" was " + request.getStatus() + ".",
                "/creator-dashboard.html?tab=submissions");

        return toResponse(submission);
    }

    @Override
    public ContentSubmissionResponse getByApplication(Long applicationId) {
        return toResponse(submissionRepository.findByApplicationId(applicationId)
                .orElseThrow(() -> new ResourceNotFoundException("No submission found for this application.")));
    }

    private ContentSubmissionResponse toResponse(ContentSubmission s) {
        return ContentSubmissionResponse.builder()
                .id(s.getId())
                .applicationId(s.getApplication().getId())
                .instagramReelLink(s.getInstagramReelLink())
                .youtubeLink(s.getYoutubeLink())
                .tiktokLink(s.getTiktokLink())
                .driveLink(s.getDriveLink())
                .comments(s.getComments())
                .submissionDate(s.getSubmissionDate())
                .status(s.getStatus())
                .reviewComments(s.getReviewComments())
                .build();
    }
}
