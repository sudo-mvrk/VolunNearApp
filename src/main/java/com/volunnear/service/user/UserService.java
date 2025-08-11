package com.volunnear.service.user;

import com.volunnear.UserRole;
import com.volunnear.dto.request.user.RegisterAppUserDTO;
import com.volunnear.entity.users.AppUser;
import com.volunnear.exception.UserAlreadyExistsException;
import com.volunnear.mapper.user.AppUserMapper;
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
    public Long registerAppUser(RegisterAppUserDTO requestDto, UserRole role) {
        if (appUserRepository.existsByUsernameOrEmail(requestDto.getUsername(), requestDto.getEmail())) {
            throw new UserAlreadyExistsException("User with username " + requestDto.getUsername() + " already exists");
        }
        AppUser user = appUserMapper.toEntity(requestDto, role == UserRole.VOLUNTEER ? "VOLUNTEER" : "ORGANIZATION");

        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        appUserRepository.save(user);
        return user.getId();
    }
    @Transactional
    public void deleteAppUser(String username) {
        appUserRepository.deleteByUsername(username);
    }
}
