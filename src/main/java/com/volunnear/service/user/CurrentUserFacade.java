package com.volunnear.service.user;

import com.volunnear.entity.users.AppUser;
import com.volunnear.exception.BadUserCredentialsException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
public class CurrentUserFacade {
    private final UserService userService;

    public AppUser getUserFromPrincipal(Principal principal) {
        return userService.findAppUserByUsername(principal.getName())
                .orElseThrow(() -> new BadUserCredentialsException("User with username " + principal.getName() + " not found"));
    }

    public void deleteAppUserByPrincipal(Principal principal) {
        userService.deleteAppUser(principal.getName());
    }

    public boolean hasVolunteerProfile(AppUser user) {
        return user.getVolunteerProfile() != null;
    }

    public boolean hasOrganizationProfile(AppUser user) {
        return user.getOrganizationProfile() != null;
    }
}
