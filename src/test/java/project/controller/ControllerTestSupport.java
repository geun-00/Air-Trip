package project.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import project.infrastructure.jwt.JwtProvider;
import project.auth.application.out.command.ManageBlacklistedTokenPort;
import project.auth.adapter.in.security.jwt.JwtAuthenticationResolver;

@Disabled
public abstract class ControllerTestSupport {

    @Autowired protected ObjectMapper objectMapper;
    @Autowired protected MockMvc mockMvc;

    @MockitoBean protected JwtProvider jwtProvider;
    @MockitoBean protected ManageBlacklistedTokenPort manageBlacklistedTokenPort;
    @MockitoBean protected JwtAuthenticationResolver jwtAuthenticationResolver;

    protected String creatJson(Object dto) throws JsonProcessingException {
        return objectMapper.writeValueAsString(dto);
    }
}
