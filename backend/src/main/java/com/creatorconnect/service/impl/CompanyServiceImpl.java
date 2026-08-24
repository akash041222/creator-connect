package com.creatorconnect.service.impl;

import com.creatorconnect.dto.request.CompanyProfileRequest;
import com.creatorconnect.dto.response.CompanyProfileResponse;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.entity.Company;
import com.creatorconnect.exception.ResourceNotFoundException;
import com.creatorconnect.repository.CompanyRepository;
import com.creatorconnect.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CompanyServiceImpl implements CompanyService {

    private final CompanyRepository companyRepository;

    @Override
    public CompanyProfileResponse getProfile(Long userId) {
        return toResponse(companyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found.")));
    }

    @Override
    public CompanyProfileResponse getPublicProfile(Long companyId) {
        return toResponse(companyRepository.findById(companyId)
                .orElseThrow(() -> new ResourceNotFoundException("Company not found: " + companyId)));
    }

    @Override
    @Transactional
    public CompanyProfileResponse updateProfile(Long userId, CompanyProfileRequest request) {
        Company company = companyRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Company profile not found."));

        company.setCompanyName(request.getCompanyName());
        company.setLogoUrl(request.getLogoUrl());
        company.setWebsite(request.getWebsite());
        company.setIndustry(request.getIndustry());
        company.setDescription(request.getDescription());
        company.setLocation(request.getLocation());
        company.setInstagramHandle(request.getInstagramHandle());
        company.setLinkedinHandle(request.getLinkedinHandle());
        company.setTwitterHandle(request.getTwitterHandle());

        return toResponse(companyRepository.save(company));
    }

    @Override
    public PageResponse<CompanyProfileResponse> search(String name, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "totalCampaigns"));
        Page<Company> results = (name == null || name.isBlank())
                ? companyRepository.findByVerified(true, pageable)
                : companyRepository.findByCompanyNameContainingIgnoreCase(name, pageable);
        return PageResponse.from(results.map(this::toResponse));
    }

    private CompanyProfileResponse toResponse(Company c) {
        return CompanyProfileResponse.builder()
                .id(c.getId())
                .companyName(c.getCompanyName())
                .email(c.getUser().getEmail())
                .logoUrl(c.getLogoUrl())
                .website(c.getWebsite())
                .industry(c.getIndustry())
                .description(c.getDescription())
                .location(c.getLocation())
                .verified(c.isVerified())
                .totalCampaigns(c.getTotalCampaigns())
                .totalSpending(c.getTotalSpending())
                .averageRating(c.getAverageRating())
                .build();
    }
}
