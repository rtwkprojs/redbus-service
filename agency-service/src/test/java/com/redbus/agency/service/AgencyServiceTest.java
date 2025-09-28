package com.redbus.agency.service;

import com.redbus.agency.dto.AgencyRequestDto;
import com.redbus.agency.dto.AgencyResponseDto;
import com.redbus.agency.entity.Agency;
import com.redbus.agency.repository.AgencyRepository;
import com.redbus.agency.service.impl.AgencyServiceImpl;
import com.redbus.common.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgencyServiceTest {

    @Mock
    private AgencyRepository agencyRepository;

    @InjectMocks
    private AgencyServiceImpl agencyService;

    private AgencyRequestDto requestDto;
    private Agency agency;
    private AgencyResponseDto responseDto;

    @BeforeEach
    void setUp() {
        requestDto = new AgencyRequestDto();
        requestDto.setAgencyName("Test Agency");
        requestDto.setContactEmail("test@agency.com");
        requestDto.setContactPhone("1234567890");
        requestDto.setAddress("Test Address");

        agency = new Agency();
        agency.setReferenceId(UUID.randomUUID());
        agency.setAgencyName("Test Agency");
        agency.setContactEmail("test@agency.com");
        agency.setContactPhone("1234567890");
        agency.setAddress("Test Address");
        agency.setIsActive(true);

        responseDto = new AgencyResponseDto();
        responseDto.setReferenceId(agency.getReferenceId());
        responseDto.setAgencyName(agency.getAgencyName());
        responseDto.setContactEmail(agency.getContactEmail());
        responseDto.setContactPhone(agency.getContactPhone());
        responseDto.setAddress(agency.getAddress());
        responseDto.setIsActive(true);
    }

    @Test
    void createAgency_Success() {
        when(agencyRepository.existsByContactEmail(anyString())).thenReturn(false);
        when(agencyRepository.existsByContactPhone(anyString())).thenReturn(false);
        when(agencyRepository.save(any(Agency.class))).thenReturn(agency);

        AgencyResponseDto result = agencyService.createAgency(requestDto, "owner-id");

        assertThat(result).isNotNull();
        assertThat(result.getAgencyName()).isEqualTo("Test Agency");
        verify(agencyRepository).save(any(Agency.class));
    }

    @Test
    void createAgency_EmailExists_ThrowsException() {
        when(agencyRepository.existsByContactEmail(anyString())).thenReturn(true);

        assertThatThrownBy(() -> agencyService.createAgency(requestDto, "owner-id"))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining("email already exists");
    }

    @Test
    void getAgencyByReferenceId_Success() {
        UUID referenceId = UUID.randomUUID();
        when(agencyRepository.findByReferenceId(any(UUID.class))).thenReturn(Optional.of(agency));

        AgencyResponseDto result = agencyService.getAgencyByReferenceId(referenceId);

        assertThat(result).isNotNull();
        assertThat(result.getAgencyName()).isEqualTo("Test Agency");
    }
}
