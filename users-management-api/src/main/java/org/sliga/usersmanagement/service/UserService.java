package org.sliga.usersmanagement.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.sliga.usersmanagement.model.User;
import org.sliga.usersmanagement.repository.UserRepository;
import org.sliga.usersmanagement.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Objects;

@Service
@Transactional
@Qualifier("UserDetailsService")
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private static final Log logger = LogFactory.getLog(UserService.class);

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = this.userRepository.findUserByUsername(username);
        if (Objects.isNull(user)){
            String errorMessage = "User not found by username : " + username;
            logger.error(errorMessage);
            throw new UsernameNotFoundException(errorMessage);
        }else {
            user.setLastLoginDateDisplay(user.getLastLoginDate());
            user.setLastLoginDate(new Date());
            this.userRepository.save(user);
            logger.info("Returning found User by username : " + username);
            return new UserPrincipal(user);
        }
    }
}
