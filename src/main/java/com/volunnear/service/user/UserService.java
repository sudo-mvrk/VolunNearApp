package com.volunnear.service.user;

import com.volunnear.UserRole;
import com.volunnear.dto.request.user.RegisterAppUserDTO;
import com.volunnear.dto.request.user.RegisterOrganizationUserProfileRequest;
import com.volunnear.dto.request.user.RegisterVolunteerUserProfileRequest;
import com.volunnear.entity.profile.OrganizationProfile;
import com.volunnear.entity.profile.VolunteerProfile;
import com.volunnear.entity.users.AppUser;
import com.volunnear.exception.UserAlreadyExistsException;
import com.volunnear.mapper.profile.OrganizationProfileMapper;
import com.volunnear.mapper.profile.VolunteerProfileMapper;
import com.volunnear.mapper.user.AppUserMapper;
import com.volunnear.repository.profile.OrganizationProfileRepository;
import com.volunnear.repository.profile.VolunteerProfileRepository;
import com.volunnear.repository.user.AppUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final AppUserMapper appUserMapper;
    private final AppUserRepository appUserRepository;

    private final VolunteerProfileMapper volunteerProfileMapper;
    private final VolunteerProfileRepository volunteerProfileRepository;

    private final OrganizationProfileMapper organizationProfileMapper;
    private final OrganizationProfileRepository organizationProfileRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser appUser = appUserRepository.findAppUserByUsername(username).orElseThrow(() -> new UsernameNotFoundException(
                String.format("User with " + username + " not found")
        ));
        return User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .roles(appUser.getRoles().toArray(new String[0]))
                .build();
    }

    @Transactional(readOnly = true)
    public Optional<AppUser> findAppUserByUsername(String username) {
        return appUserRepository.findAppUserByUsername(username);
    }

    @Transactional
    public void registerVolunteer(RegisterVolunteerUserProfileRequest registerVolunteerUserProfileRequest, UserRole role) {
        AppUser savedUser = registerAppUser(registerVolunteerUserProfileRequest, role);
        VolunteerProfile volunteerProfile = volunteerProfileMapper.toEntity(registerVolunteerUserProfileRequest, savedUser);
        volunteerProfileRepository.save(volunteerProfile);
    }

    @Transactional
    public void registerOrganization(RegisterOrganizationUserProfileRequest registerOrganizationUserProfileRequest, UserRole role) {
        AppUser savedUser = registerAppUser(registerOrganizationUserProfileRequest, role);
        OrganizationProfile organizationProfile = organizationProfileMapper.toEntity(registerOrganizationUserProfileRequest, savedUser);
        organizationProfileRepository.save(organizationProfile);
    }


    private AppUser registerAppUser(RegisterAppUserDTO requestDto, UserRole role) {
        if (appUserRepository.existsByUsernameOrEmail(requestDto.getUsername(), requestDto.getEmail())) {
            throw new UserAlreadyExistsException("User with username " + requestDto.getUsername() + " already exists");
        }
        AppUser user = appUserMapper.toEntity(requestDto, role == UserRole.VOLUNTEER ? "VOLUNTEER" : "ORGANIZATION");

        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        appUserRepository.save(user);
        return user;
    }

    @Transactional
    public void deleteAppUser(String username) {
        appUserRepository.deleteByUsername(username);
    }
}
