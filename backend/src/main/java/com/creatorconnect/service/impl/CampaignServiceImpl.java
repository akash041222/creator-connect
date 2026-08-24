package com.creatorconnect.service.impl;

import com.creatorconnect.dto.request.CampaignRequest;
import com.creatorconnect.dto.response.CampaignResponse;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.entity.Campaign;
import com.creatorconnect.entity.Company;
import com.creatorconnect.entity.enums.CampaignStatus;
import com.creatorconnect.entity.enums.Platform;
import com.creatorconnect.exception.ResourceNotFoundException;
import com.creatorconnect.exception.UnauthorizedActionException;
import com.creatorconnect.repository.ApplicationRepository;
import com.creatorconnect.repository.CampaignRepository;
import com.creatorconnect.repository.CompanyRepository;
import com.creatorconnect.service.CampaignService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class CampaignServiceImpl implements CampaignService {

    private final CampaignRepository campaignRepository;
    private final CompanyRepository companyRepository;
    private final ApplicationRepository applicationRepository;

    @Override
    @Transactional
    public CampaignResponse create(Long companyUserId, CampaignRequest request) {
        Company company = companyOf(companyUserId);

        Campaign campaign = Campaign.builder()
                .company(company)
                .title(request.getTitle())
                .description(request.getDescription())
                .budget(request.getBudget())
                .minFollowers(request.getMinFollowers())
                .preferredPlatform(request.getPreferredPlatform())
                .category(request.getCategory())
                .deadline(request.getDeadline())
                .bannerUrl(request.getBannerUrl())
                .guidelines(request.getGuidelines())
                .referenceFilesUrl(request.getReferenceFilesUrl())
                .deliverables(request.getDeliverables())
                .creatorsRequired(request.getCreatorsRequired() == null ? 1 : request.getCreatorsRequired())
                .status(CampaignStatus.OPEN)
                .build();
        campaign = campaignRepository.save(campaign);

        company.setTotalCampaigns(company.getTotalCampaigns() + 1);
        companyRepository.save(company);

        return toResponse(campaign, 0);
    }

    @Override
    @Transactional
    public CampaignResponse update(Long companyUserId, Long campaignId, CampaignRequest request) {
        Campaign campaign = campaignOwnedBy(companyUserId, campaignId);

        campaign.setTitle(request.getTitle());
        campaign.setDescription(request.getDescription());
        campaign.setBudget(request.getBudget());
        campaign.setMinFollowers(request.getMinFollowers());
        campaign.setPreferredPlatform(request.getPreferredPlatform());
        campaign.setCategory(request.getCategory());
        campaign.setDeadline(request.getDeadline());
        campaign.setBannerUrl(request.getBannerUrl());
        campaign.setGuidelines(request.getGuidelines());
        campaign.setReferenceFilesUrl(request.getReferenceFilesUrl());
        campaign.setDeliverables(request.getDeliverables());
        if (request.getCreatorsRequired() != null) {
            campaign.setCreatorsRequired(request.getCreatorsRequired());
        }
        campaign = campaignRepository.save(campaign);

        long appCount = applicationRepository.findByCampaignId(campaign.getId(), Pageable.unpaged()).getTotalElements();
        return toResponse(campaign, appCount);
    }

    @Override
    @Transactional
    public void delete(Long companyUserId, Long campaignId) {
        Campaign campaign = campaignOwnedBy(companyUserId, campaignId);
        campaign.setDeleted(true);
        campaign.setStatus(CampaignStatus.CANCELLED);
        campaignRepository.save(campaign);
    }

    @Override
    @Transactional
    public CampaignResponse getById(Long campaignId, boolean incrementView) {
        Campaign campaign = campaignRepository.findById(campaignId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found: " + campaignId));
        if (incrementView) {
            campaign.setViewCount(campaign.getViewCount() + 1);
            campaign = campaignRepository.save(campaign);
        }
        long appCount = applicationRepository.findByCampaignId(campaign.getId(), Pageable.unpaged()).getTotalElements();
        return toResponse(campaign, appCount);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CampaignResponse> search(String keyword, String category, Platform platform,
                                                  BigDecimal minBudget, BigDecimal maxBudget, Long minFollowers,
                                                  LocalDate deadlineBefore, CampaignStatus status,
                                                  int page, int size, String sortBy, String direction) {
        Sort.Direction dir = "asc".equalsIgnoreCase(direction) ? Sort.Direction.ASC : Sort.Direction.DESC;
        String sortField = (sortBy == null || sortBy.isBlank()) ? "createdAt" : sortBy;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sortField));

        Page<Campaign> results = campaignRepository.search(
                blankToNull(keyword), blankToNull(category), platform,
                minBudget, maxBudget, minFollowers, deadlineBefore, status, pageable);

        return PageResponse.from(results.map(c -> toResponse(c,
                applicationRepository.findByCampaignId(c.getId(), Pageable.unpaged()).getTotalElements())));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CampaignResponse> getByCompany(Long companyUserId, int page, int size) {
        Company company = companyOf(companyUserId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Campaign> results = campaignRepository.findByCompanyId(company.getId(), pageable);
        return PageResponse.from(results.map(c -> toResponse(c,
                applicationRepository.findByCampaignId(c.getId(), Pageable.unpaged()).getTotalElements())));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<CampaignResponse> getTrending(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Campaign> results = campaignRepository.findTrending(pageable);
        return PageResponse.from(results.map(c -> toResponse(c,
                applicationRepository.findByCampaignId(c.getId(), Pageable.unpaged()).getTotalElements())));
    }

    @Override
    @Transactional
    public CampaignResponse updateStatus(Long companyUserId, Long campaignId, CampaignStatus status) {
        Campaign campaign = campaignOwnedBy(companyUserId, campaignId);
        campaign.setStatus(status);
        campaign = campaignRepository.save(campaign);
        long appCount = applicationRepository.findByCampaignId(campaign.getId(), Pageable.unpaged()).getTotalElements();
        return toResponse(campaign, appCount);
    }

    // ---------- helpers ----------

    private Company companyOf(Long userId) {
        return companyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found for this account."));
    }

    private Campaign campaignOwnedBy(Long companyUserId, Long campaignId) {
        Company company = companyOf(companyUserId);
        Campaign campaign = campaignRepository.findById(campaignId)
                .filter(c -> !c.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Campaign not found: " + campaignId));
        if (!campaign.getCompany().getId().equals(company.getId())) {
            throw new UnauthorizedActionException("You do not own this campaign.");
        }
        return campaign;
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private CampaignResponse toResponse(Campaign c, long applicationCount) {
        Company company = c.getCompany();
        return CampaignResponse.builder()
                .id(c.getId())
                .companyId(company.getId())
                .companyName(company.getCompanyName())
                .companyLogoUrl(company.getLogoUrl())
                .companyVerified(company.isVerified())
                .title(c.getTitle())
                .description(c.getDescription())
                .budget(c.getBudget())
                .minFollowers(c.getMinFollowers())
                .preferredPlatform(c.getPreferredPlatform())
                .category(c.getCategory())
                .deadline(c.getDeadline())
                .bannerUrl(c.getBannerUrl())
                .guidelines(c.getGuidelines())
                .deliverables(c.getDeliverables())
                .creatorsRequired(c.getCreatorsRequired())
                .status(c.getStatus())
                .viewCount(c.getViewCount())
                .applicationCount(applicationCount)
                .createdAt(c.getCreatedAt())
                .build();
    }
}
