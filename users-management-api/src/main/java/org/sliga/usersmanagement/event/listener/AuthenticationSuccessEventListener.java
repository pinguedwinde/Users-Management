package org.sliga.usersmanagement.event.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sliga.usersmanagement.security.UserPrincipal;
import org.sliga.usersmanagement.service.LoginAttemptsService;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.InteractiveAuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationSuccessEventListener implements ApplicationListener<AuthenticationSuccessEvent> {
    private static final Logger log = LoggerFactory.getLogger(AuthenticationFailureListener.class);

    public final LoginAttemptsService loginAttemptsService;

    public AuthenticationSuccessEventListener(LoginAttemptsService loginAttemptsService) {
        this.loginAttemptsService = loginAttemptsService;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent authSuccessEvent) {
        Object principal = authSuccessEvent.getAuthentication().getPrincipal();
        if(principal instanceof UserPrincipal){
            String username = ((UserPrincipal) principal).getUsername();
            log.info("Login succeeded for user {} ", username);
            loginAttemptsService.evictUserFromLoginAttemptsCache(username);
        }
    }
}
