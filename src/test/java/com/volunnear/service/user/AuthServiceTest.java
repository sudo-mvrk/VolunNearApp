package com.volunnear.service.user;

import com.volunnear.dto.request.VolunteerRegistrationRequestDto;
import com.volunnear.entity.enums.Role;
import com.volunnear.entity.user.AppUser;
import com.volunnear.exception.UserAlreadyExistsException;
import com.volunnear.repository.AppUserRepository;
import com.volunnear.repository.VolunteerRepository;
import com.volunnear.service.VolunteerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {
    @Mock
    private AppUserRepository appUserRepository;
    @Mock
    private VolunteerRepository volunteerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private VolunteerService volunteerService;

    @InjectMocks
    private AuthService authService;
    @Captor
    private ArgumentCaptor<AppUser> userCaptor;

    private VolunteerRegistrationRequestDto request;

    @BeforeEach
    void prepareData() {
        this.request = new VolunteerRegistrationRequestDto(
                "test",
                "test",
                "test@gmail.com",
                "test",
                "test",
                LocalDate.of(2003,3,2));
    }

    @Test
    void shouldThrowExceptionWhenEmailAlreadyExists() {
        when(appUserRepository.existsByEmail(request.email())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.registerVolunteer(request));

        verify(appUserRepository, never()).save(any());
        verify(volunteerRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenUsernameAlreadyExists() {
        when(appUserRepository.existsByUsername(request.username())).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> authService.registerVolunteer(request));

        verify(appUserRepository, never()).save(any());
        verify(volunteerRepository, never()).save(any());
    }

    @Test
    void shouldSuccessfullyCreateVolunteer() {
        String encodedPassMock = "encodedPassword!@#";
        when(appUserRepository.existsByEmail(request.email())).thenReturn(false);
        when(appUserRepository.existsByUsername(request.username())).thenReturn(false);
        when(passwordEncoder.encode(request.password())).thenReturn(encodedPassMock);

        AppUser savedUserMock = new AppUser();
        savedUserMock.setId(42L);
        when(appUserRepository.save(any(AppUser.class))).thenReturn(savedUserMock);

        authService.registerVolunteer(request);
        verify(appUserRepository).save(userCaptor.capture());
        AppUser capturedUser = userCaptor.getValue();

        assertEquals(request.email(), capturedUser.getEmail());
        assertEquals(encodedPassMock, capturedUser.getPassword());
        assertTrue(capturedUser.getRoles().contains(Role.ROLE_VOLUNTEER));
        verify(volunteerService).createVolunteerProfile(request, savedUserMock);
    }

}