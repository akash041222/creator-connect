package com.creatorconnect.service.impl;

import com.creatorconnect.dto.request.CreatorProfileRequest;
import com.creatorconnect.dto.response.CreatorProfileResponse;
import com.creatorconnect.dto.response.PageResponse;
import com.creatorconnect.entity.Creator;
import com.creatorconnect.exception.ResourceNotFoundException;
import com.creatorconnect.repository.CreatorRepository;
import com.creatorconnect.service.CreatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CreatorServiceImpl implements CreatorService {

    private final CreatorRepository creatorRepository;

    @Override
    public CreatorProfileResponse getProfile(Long userId) {
        return toResponse(creatorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator profile not found.")));
    }

    @Override
    public CreatorProfileResponse getPublicProfile(Long creatorId) {
        return toResponse(creatorRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator not found: " + creatorId)));
    }

    @Override
    @Transactional
    public CreatorProfileResponse updateProfile(Long userId, CreatorProfileRequest request) {
        Creator creator = creatorRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Creator profile not found."));

        creator.setProfilePictureUrl(request.getProfilePictureUrl());
        creator.setCoverPhotoUrl(request.getCoverPhotoUrl());
        creator.setBio(request.getBio());
        creator.setLocation(request.getLocation());
        creator.setLanguages(request.getLanguages());
        creator.setCategory(request.getCategory());
        creator.setExperienceYears(request.getExperienceYears());
        if (request.getFollowerCount() != null) creator.setFollowerCount(request.getFollowerCount());
        if (request.getEngagementRate() != null) creator.setEngagementRate(request.getEngagementRate());
        creator.setInstagramHandle(request.getInstagramHandle());
        creator.setYoutubeHandle(request.getYoutubeHandle());
        creator.setLinkedinHandle(request.getLinkedinHandle());
        creator.setFacebookHandle(request.getFacebookHandle());
        creator.setTiktokHandle(request.getTiktokHandle());
        creator.setPortfolioUrl(request.getPortfolioUrl());
        creator.setSkills(request.getSkills());
        creator.setAchievements(request.getAchievements());
        creator.setProfileCompletionPercent(calculateCompletion(creator));

        return toResponse(creatorRepository.save(creator));
    }

    @Override
    public PageResponse<CreatorProfileResponse> search(String category, String location, Long minFollowers, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "followerCount"));
        Page<Creator> results = creatorRepository.search(
                blankToNull(category), blankToNull(location), minFollowers, pageable);
        return PageResponse.from(results.map(this::toResponse));
    }

    @Override
    public PageResponse<CreatorProfileResponse> leaderboard(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Creator> results = creatorRepository.findAllByOrderByAverageRatingDesc(pageable);
        return PageResponse.from(results.map(this::toResponse));
    }

    private int calculateCompletion(Creator c) {
        int total = 10;
        int filled = 0;
        if (notBlank(c.getProfilePictureUrl())) filled++;
        if (notBlank(c.getBio())) filled++;
        if (notBlank(c.getLocation())) filled++;
        if (notBlank(c.getCategory())) filled++;
        if (c.getFollowerCount() != null && c.getFollowerCount() > 0) filled++;
        if (notBlank(c.getInstagramHandle()) || notBlank(c.getYoutubeHandle())) filled++;
        if (notBlank(c.getPortfolioUrl())) filled++;
        if (notBlank(c.getSkills())) filled++;
        if (notBlank(c.getAchievements())) filled++;
        if (c.getExperienceYears() != null) filled++;
        return Math.round((filled * 100f) / total);
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }

    private String blankToNull(String s) {
        return (s == null || s.isBlank()) ? null : s;
    }

    private CreatorProfileResponse toResponse(Creator c) {
        return CreatorProfileResponse.builder()
                .id(c.getId())
                .fullName(c.getUser().getFullName())
                .email(c.getUser().getEmail())
                .profilePictureUrl(c.getProfilePictureUrl())
                .coverPhotoUrl(c.getCoverPhotoUrl())
                .bio(c.getBio())
                .location(c.getLocation())
                .languages(c.getLanguages())
                .category(c.getCategory())
                .experienceYears(c.getExperienceYears())
                .followerCount(c.getFollowerCount())
                .engagementRate(c.getEngagementRate())
                .instagramHandle(c.getInstagramHandle())
                .youtubeHandle(c.getYoutubeHandle())
                .linkedinHandle(c.getLinkedinHandle())
                .facebookHandle(c.getFacebookHandle())
                .tiktokHandle(c.getTiktokHandle())
                .portfolioUrl(c.getPortfolioUrl())
                .skills(c.getSkills())
                .achievements(c.getAchievements())
                .verified(c.isVerified())
                .averageRating(c.getAverageRating())
                .completedCampaignsCount(c.getCompletedCampaignsCount())
                .successRate(c.getSuccessRate())
                .totalEarnings(c.getTotalEarnings())
                .profileCompletionPercent(c.getProfileCompletionPercent())
                .build();
    }
}
