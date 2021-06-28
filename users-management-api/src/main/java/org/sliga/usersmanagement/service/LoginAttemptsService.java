package org.sliga.usersmanagement.service;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.sliga.usersmanagement.utils.AuthConstants.MAXIMUM_NUMBER_OF_ATTEMPTS;

@Service
public class LoginAttemptsService {
    public LoadingCache<String, Integer> loginAttemptsCache;

    public LoginAttemptsService() {
        this.loginAttemptsCache = CacheBuilder.newBuilder()
                .expireAfterWrite(1, TimeUnit.HOURS)
                .maximumSize(200)
                .build(
                        new CacheLoader<>() {
                            @Override
                            public Integer load(String key) {
                                return 0;
                            }
                        }
                );
    }

    public void evictUserFromLoginAttemptsCache(String username){
        loginAttemptsCache.invalidate(username);
    }

    public void addUserToLoginAttemptsCache(String username){
        int attempts = 0;
        try {
            attempts = loginAttemptsCache.get(username);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        loginAttemptsCache.put(username, ++attempts);
    }

    public boolean hasExceededMaxAttempts(String username) {
        try {
            return loginAttemptsCache.get(username) >= MAXIMUM_NUMBER_OF_ATTEMPTS;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return false;
    }
}
