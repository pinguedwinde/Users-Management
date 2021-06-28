package org.sliga.usersmanagement.event.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sliga.usersmanagement.service.LoginAttemptsService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationFailureListener implements ApplicationListener<AbstractAuthenticationFailureEvent> {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFailureListener.class);

    public final LoginAttemptsService loginAttemptsService;

    public AuthenticationFailureListener(LoginAttemptsService loginAttemptsService) {
        this.loginAttemptsService = loginAttemptsService;
    }

    @Override
    public void onApplicationEvent(AbstractAuthenticationFailureEvent authFailureBadCredentialsEvent) {
        Object principal = authFailureBadCredentialsEvent.getAuthentication().getPrincipal();
        if(principal instanceof String){
            String username = principal.toString();
            log.info("********* login failed for user {} ", username);
            loginAttemptsService.addUserToLoginAttemptsCache(username);
        }
    }
}
