package com.volunnear.service;

import com.volunnear.dto.request.VolunteerUpdateProfileRequestDto;
import com.volunnear.dto.response.profile.VolunteerProfileResponseDto;
import com.volunnear.entity.profile.Volunteer;
import com.volunnear.entity.user.AppUser;
import com.volunnear.mapper.VolunteerMapper;
import com.volunnear.repository.VolunteerRepository;
import com.volunnear.security.detail.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VolunteerServiceTest {
    @InjectMocks
    private VolunteerService service;
    @Mock
    private VolunteerRepository repository;
    @Mock
    private VolunteerMapper mapper;
    private AppUser appUser;
    private CustomUserDetails userDetails;
    private Volunteer baseVolunteer;
    private VolunteerUpdateProfileRequestDto requestDto;

    @BeforeEach
    void prepareData() {
        appUser = AppUser.builder()
                .id(1L)
                .username("username")
                .email("test@mail.com")
                .build();

        userDetails = new CustomUserDetails(appUser);

        baseVolunteer = Volunteer.builder()
                .id(100L)
                .appUser(appUser)
                .dateOfBirth(LocalDate.of(2000, 1, 1))
                .build();

        requestDto = VolunteerUpdateProfileRequestDto.builder()
                .lat(50.034)
                .lon(14.353)
                .locationName("Pardubice, CZ")
                .build();
    }

    @Test
    void shouldUpdateProfileAndSetPointCorrectly() {
        when(repository.findByAppUser_Username(appUser.getUsername()))
                    .thenReturn(Optional.of(baseVolunteer));

        when(mapper.updateEntity(requestDto, baseVolunteer))
                .thenReturn(baseVolunteer);

        VolunteerProfileResponseDto expectedResponse = VolunteerProfileResponseDto.builder()
                .bio("New Bio")
                .build();
        when(mapper.toDto(any(Volunteer.class)))
                .thenReturn(expectedResponse);

        VolunteerProfileResponseDto actualResponse = service.updateVolunteerProfile(requestDto, userDetails);

        assertNotNull(actualResponse);
        assertEquals("New Bio", actualResponse.bio());

        verify(mapper, times(1)).updateEntity(requestDto, baseVolunteer);
    }
}