package com.creatorconnect.service;

import com.creatorconnect.dto.request.ApplicationRequest;
import com.creatorconnect.entity.*;
import com.creatorconnect.entity.enums.ApplicationStatus;
import com.creatorconnect.entity.enums.CampaignStatus;
import com.creatorconnect.entity.enums.Role;
import com.creatorconnect.exception.DuplicateResourceException;
import com.creatorconnect.exception.InvalidRequestException;
import com.creatorconnect.repository.*;
import com.creatorconnect.service.impl.ApplicationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the two business rules that matter most in the application
 * flow: (1) a creator cannot apply twice to the same campaign, and (2) a
 * creator cannot apply to a campaign that isn't OPEN. Repositories are
 * mocked so these run in milliseconds with no database.
 */
@ExtendWith(MockitoExtension.class)
class ApplicationServiceImplTest {

    @Mock private ApplicationRepository applicationRepository;
    @Mock private CampaignRepository campaignRepository;
    @Mock private CreatorRepository creatorRepository;
    @Mock private CompanyRepository companyRepository;
    @Mock private NotificationService notificationService;
    @Mock private EmailService emailService;

    @InjectMocks
    private ApplicationServiceImpl applicationService;

    private Creator creator;
    private Campaign campaign;

    @BeforeEach
    void setUp() {
        User creatorUser = User.builder().id(5L).fullName("Ananya Rao").role(Role.CREATOR).build();
        creator = Creator.builder().id(1L).user(creatorUser).build();

        User companyUser = User.builder().id(2L).fullName("Nova Beauty").role(Role.COMPANY).build();
        Company company = Company.builder().id(1L).user(companyUser).companyName("Nova Beauty").build();
        campaign = Campaign.builder().id(10L).company(company).title("Glow Serum Launch")
                .budget(BigDecimal.valueOf(15000)).status(CampaignStatus.OPEN).build();
    }

    @Test
    void apply_throwsDuplicate_whenCreatorAlreadyApplied() {
        ApplicationRequest request = new ApplicationRequest();
        request.setCampaignId(10L);
        request.setMessage("I'd love to work on this!");

        when(creatorRepository.findByUserId(5L)).thenReturn(Optional.of(creator));
        when(campaignRepository.findById(10L)).thenReturn(Optional.of(campaign));
        when(applicationRepository.findByCampaignIdAndCreatorId(10L, 1L))
                .thenReturn(Optional.of(Application.builder().id(99L).build()));

        assertThatThrownBy(() -> applicationService.apply(5L, request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessageContaining("already applied");

        verify(applicationRepository, never()).save(any());
    }

    @Test
    void apply_throwsInvalidRequest_whenCampaignNotOpen() {
        campaign.setStatus(CampaignStatus.CLOSED);
        ApplicationRequest request = new ApplicationRequest();
        request.setCampaignId(10L);
        request.setMessage("Pitch");

        when(creatorRepository.findByUserId(5L)).thenReturn(Optional.of(creator));
        when(campaignRepository.findById(10L)).thenReturn(Optional.of(campaign));

        assertThatThrownBy(() -> applicationService.apply(5L, request))
                .isInstanceOf(InvalidRequestException.class)
                .hasMessageContaining("not accepting applications");
    }

    @Test
    void apply_succeeds_andNotifiesTheCompany() {
        ApplicationRequest request = new ApplicationRequest();
        request.setCampaignId(10L);
        request.setMessage("I'd love to work on this!");
        request.setPortfolioLink("https://portfolio.example.com");

        when(creatorRepository.findByUserId(5L)).thenReturn(Optional.of(creator));
        when(campaignRepository.findById(10L)).thenReturn(Optional.of(campaign));
        when(applicationRepository.findByCampaignIdAndCreatorId(10L, 1L)).thenReturn(Optional.empty());
        when(applicationRepository.save(any(Application.class))).thenAnswer(inv -> {
            Application a = inv.getArgument(0);
            a.setId(100L);
            return a;
        });

        var response = applicationService.apply(5L, request);

        assertThat(response.getId()).isEqualTo(100L);
        assertThat(response.getStatus()).isEqualTo(ApplicationStatus.PENDING);
        assertThat(response.getCampaignTitle()).isEqualTo("Glow Serum Launch");
        verify(notificationService, times(1)).send(any(), any(), anyString(), anyString(), anyString());
    }
}
