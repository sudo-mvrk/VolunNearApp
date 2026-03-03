package com.volunnear.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

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
}