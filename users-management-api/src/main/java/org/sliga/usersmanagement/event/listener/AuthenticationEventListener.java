package org.sliga.usersmanagement.event.listener;

import org.sliga.usersmanagement.model.User;
import org.sliga.usersmanagement.service.AuthService;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

import java.util.concurrent.ExecutionException;

@Component
public class AuthenticationEventListener {
    public final AuthService authService;

    public AuthenticationEventListener(AuthService authService) {
        this.authService = authService;
    }

    @EventListener
    public void onAuthenticationFailureEvent(AuthenticationFailureBadCredentialsEvent authFailureBadCredentialsEvent) throws ExecutionException {
        Object principal = authFailureBadCredentialsEvent.getAuthentication().getPrincipal();
        if(principal instanceof String){
            String username = principal.toString();
            authService.addUserToLoginAttemptsCache(username);
        }
    }

    @EventListener
    public void onAuthenticationSuccessEvent(AuthenticationSuccessEvent authSuccessEvent){
        Object principal = authSuccessEvent.getAuthentication().getPrincipal();
        if(principal instanceof User){
            String username = ((User) principal).getUsername();
            authService.evictUserFromLoginAttemptsCache(username);
        }
    }
}
