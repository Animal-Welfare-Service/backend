package com.clova.anifriends.base;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.clova.anifriends.base.BaseControllerTest.WebMvcTestConfig;
import com.clova.anifriends.base.config.RestDocsConfig;
import com.clova.anifriends.domain.auth.authentication.JwtAuthenticationProvider;
import com.clova.anifriends.domain.auth.jwt.JwtProvider;
import com.clova.anifriends.domain.auth.service.AuthService;
import com.clova.anifriends.domain.auth.support.AuthFixture;
import com.clova.anifriends.global.config.SecurityConfig;
import com.clova.anifriends.global.config.WebMvcConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

@WebMvcTest
@Import({SecurityConfig.class, WebMvcConfig.class, RestDocsConfig.class, WebMvcTestConfig.class})
@ExtendWith(RestDocumentationExtension.class)
public abstract class BaseControllerTest {

    protected static final String AUTHORIZATION = "Authorization";

    @TestConfiguration
    static class WebMvcTestConfig {

        @Bean
        public JwtProvider jwtProvider() {
            return AuthFixture.jwtProvider();
        }

        @Bean
        public JwtAuthenticationProvider jwtAuthenticationProvider(JwtProvider jwtProvider) {
            return new JwtAuthenticationProvider(jwtProvider);
        }
    }

    protected MockMvc mockMvc;

    @Autowired
    protected ObjectMapper objectMapper;

    @Autowired
    protected RestDocumentationResultHandler restDocs;

    @MockBean
    protected AuthService authService;

    @BeforeEach
    void setUp(
        WebApplicationContext applicationContext,
        RestDocumentationContextProvider documentationContextProvider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext)
            .alwaysDo(print())
            .alwaysDo(restDocs)
            .apply(springSecurity())
            .apply(MockMvcRestDocumentation.documentationConfiguration(documentationContextProvider))
            .addFilter(new CharacterEncodingFilter("UTF-8", true))
            .defaultRequest(post("/**").with(csrf().asHeader()))
            .defaultRequest(patch("/**").with(csrf().asHeader()))
            .defaultRequest(delete("/**").with(csrf().asHeader()))
            .build();
    }
}
