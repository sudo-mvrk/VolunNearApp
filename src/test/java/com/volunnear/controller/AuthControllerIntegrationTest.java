package com.volunnear.controller;

import com.volunnear.config.TestSessionConfig;
import com.volunnear.entity.enums.Role;
import com.volunnear.entity.user.AppUser;
import com.volunnear.repository.AppUserRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Stream;

import static org.springframework.security.test.web.servlet.response.SecurityMockMvcResultMatchers.unauthenticated;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@Transactional
@AutoConfigureMockMvc
@SpringBootTest(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration",
        "management.health.redis.enabled=false"
})
@Import(TestSessionConfig.class)
class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;


    static Stream<String> provideInvalidOrganizationRequests() {
        return Stream.of(
                // First case empty email
                """
                        {
                            "username": "testOrg",
                            "password": "testOrg",
                            "email": "",
                            "organizationName": "Test org"
                        }
                        """,
                // Bad email format
                """
                        {
                            "username": "testOrg",
                            "password": "testOrg",
                            "email": "test-bademail",
                            "organizationName": "Test org"
                        }
                        """,
                // Empty username
                """
                        {
                            "username": "",
                            "password": "testOrg",
                            "email": "testOrg@gmail.com",
                            "organizationName": "Test org"
                        }
                        """
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidOrganizationRequests")
    void shouldReturn400WhenRegistrationOrganizationDataInvalid(String invalidJson) throws Exception {
        mockMvc.perform(post("/api/v1/auth/register/organization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest());

    }

    @Test
    void shouldSuccessfullyRegisterVolunteer() throws Exception {
        String requestJson = """
                {
                    "username": "test",
                    "password": "test",
                    "email": "test@gmail.com",
                    "firstName": "test",
                    "lastName": "test",
                    "dateOfBirth": "2003-08-15"
                }
                """;

        mockMvc.perform(post("/api/v1/auth/register/volunteer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("test@gmail.com"));

    }

    @Test
    void shouldSuccessfullyRegisterOrganization() throws Exception {
        String requestJson = """
                {
                    "username": "testOrg",
                    "password": "testOrg",
                    "email": "testOrg@gmail.com",
                    "organizationName": "Test org"
                }
                """;
        mockMvc.perform(post("/api/v1/auth/register/organization")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("testOrg@gmail.com"));
    }

    @Nested
    class LoginLogoutTests {
        @Autowired
        private PasswordEncoder passwordEncoder;
        @Autowired
        private AppUserRepository appUserRepository;

        @BeforeEach
        void setUpDatabase() {
            AppUser testUserOrg = AppUser.builder()
                    .username("testOrg")
                    .email("testOrg@gmail.com")
                    .password(passwordEncoder.encode("superSecret123Org"))
                    .roles(Set.of(Role.ROLE_ORGANIZATION))
                    .build();

            AppUser testUserVolunteer = AppUser.builder()
                    .username("testVol")
                    .email("testVol@gmail.com")
                    .password(passwordEncoder.encode("superSecret123Vol"))
                    .roles(Set.of(Role.ROLE_ORGANIZATION))
                    .build();


            appUserRepository.save(testUserOrg);
            appUserRepository.save(testUserVolunteer);
        }

        static Stream<String> provideInvalidLoginCredentials() {
            return Stream.of(
                    // First case: User exists, but password wrong
                    """
                    {
                        "username": "testOrg",
                        "password": "wrongPassword123"
                    }
                    """,
                    // Second case: There is no user in DB with username "ghostUser"
                    """
                    {
                        "username": "ghostUser",
                        "password": "superSecret123"
                    }
                    """
            );
        }

        @ParameterizedTest
        @MethodSource("provideInvalidLoginCredentials")
        void shouldReturn403WhenLoginCredentialsAreInvalid(String invalidLoginJson) throws Exception {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidLoginJson))
                    .andExpect(status().isForbidden())
                    .andExpect(unauthenticated());
        }

        static Stream<String> provideLoginRequestForOrganizationAndVolunteer() {
            return Stream.of(
                    """
                            {
                                "username": "testOrg",
                                "password": "superSecret123Org"
                            }
                            """,
                    """
                            {
                                "username": "testVol",
                                "password": "superSecret123Vol"
                            }
                            """
            );
        }

        @ParameterizedTest
        @MethodSource("provideLoginRequestForOrganizationAndVolunteer")
        void shouldSuccessfullyLoginAndReturnSessionCookie(String loginJson) throws Exception {
            mockMvc.perform(post("/api/v1/auth/login")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(loginJson))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(cookie().exists("SESSION"));
        }

        @Test
        void shouldSuccessfullyLogout() throws Exception {
            Cookie accessCookie = new Cookie("SESSION", "dummy-session-id");

            mockMvc.perform(post("/api/v1/auth/logout")
                    .cookie(accessCookie))
                    .andExpect(status().isOk())
                    .andExpect(cookie().maxAge("SESSION",0));
        }
    }

}