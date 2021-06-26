package org.sliga.usersmanagement.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sliga.usersmanagement.controller.response.HttpResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.Http403ForbiddenEntryPoint;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

import static org.sliga.usersmanagement.security.SecurityConstants.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;


public class JwtAuthenticationEntryPoint extends Http403ForbiddenEntryPoint {
    private static final Log logger = LogFactory.getLog(Http403ForbiddenEntryPoint.class);

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        logger.debug("Pre-authenticated : JwtAuthenticationEntryPoint called. Rejecting access");
        HttpResponse httpResponse = new HttpResponse.Builder()
                .withStatusCode(FORBIDDEN.value())
                .withHttpStatus(FORBIDDEN)
                .withReason(FORBIDDEN.getReasonPhrase())
                .withMessage(FORBIDDEN_MESSAGE)
                .build();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(FORBIDDEN.value());
        OutputStream outputStream = response.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();

    }
}
