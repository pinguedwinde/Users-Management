package org.sliga.usersmanagement.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sliga.usersmanagement.controller.response.HttpResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Locale;

import static org.sliga.usersmanagement.utils.AuthConstants.ACCESS_DENIED_MESSAGE;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private static final Log logger = LogFactory.getLog(JwtAccessDeniedHandler.class);

    @Override
    public void handle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, AccessDeniedException e) throws IOException, ServletException {
        logger.debug("Pre-authenticated : AccessDeniedHandler called. Rejecting access");
        HttpResponse httpResponse = new HttpResponse.Builder()
                .withStatusCode(FORBIDDEN.value())
                .withHttpStatus(FORBIDDEN)
                .withReason(FORBIDDEN.getReasonPhrase().toUpperCase(Locale.ROOT))
                .withMessage(ACCESS_DENIED_MESSAGE)
                .build();
        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
        httpServletResponse.setStatus(FORBIDDEN.value());
        OutputStream outputStream = httpServletResponse.getOutputStream();
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(outputStream, httpResponse);
        outputStream.flush();
    }
}
