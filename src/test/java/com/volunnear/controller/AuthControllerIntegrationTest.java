package com.volunnear.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
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
}