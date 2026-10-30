package org.projects.book.bookshop.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.projects.book.bookshop.entity.Token;
import org.projects.book.bookshop.repository.TokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Full stack against H2: security filter chain, JWT, controllers, services and JPA.
// Only the mail server is replaced.
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AuthenticationFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private TokenRepository tokenRepository;

    @MockitoBean
    private JavaMailSender mailSender;

    @BeforeEach
    void setUp() {
        when(mailSender.createMimeMessage()).thenReturn(new MimeMessage((Session) null));
    }

    @Test
    void protectedEndpointWithoutTokenReturns401() throws Exception {
        mockMvc.perform(get("/book"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void malformedTokenReturns401() throws Exception {
        mockMvc.perform(get("/book").header("Authorization", "Bearer not-a-jwt"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void unknownActivationCodeReturns400() throws Exception {
        mockMvc.perform(get("/auth/activate-account").param("token", "000000"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void registerActivateLoginAndCreateBook() throws Exception {
        String email = "reader@example.com";
        String password = "s3cure-password";

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"firstName":"Rea","lastName":"Der","email":"%s","password":"%s"}
                                """.formatted(email, password)))
                .andExpect(status().isAccepted());

        // The activation e-mail was rendered from the template and handed to the mail server
        verify(mailSender).send(any(MimeMessage.class));

        String login = """
                {"email":"%s","password":"%s"}
                """.formatted(email, password);

        // Accounts are disabled until activated
        mockMvc.perform(post("/auth/login").contentType(MediaType.APPLICATION_JSON).content(login))
                .andExpect(status().isUnauthorized());

        String code = tokenRepository.findAll().stream()
                .filter(token -> token.getUser().getEmail().equals(email))
                .map(Token::getToken)
                .findFirst()
                .orElseThrow();
        mockMvc.perform(get("/auth/activate-account").param("token", code))
                .andExpect(status().isOk());

        // A code works only once
        mockMvc.perform(get("/auth/activate-account").param("token", code))
                .andExpect(status().isBadRequest());

        String loginResponse = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON).content(login))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode body = objectMapper.readTree(loginResponse);
        String jwt = body.get("token").asText();
        assertThat(jwt).isNotBlank();

        mockMvc.perform(post("/book")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title":"Clean Code","author":"Robert C. Martin","isbn":"9780132350884",
                                 "summary":"A handbook of agile software craftsmanship","shareable":true}
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(get("/book/owner").header("Authorization", "Bearer " + jwt))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].title").value("Clean Code"));
    }
}
