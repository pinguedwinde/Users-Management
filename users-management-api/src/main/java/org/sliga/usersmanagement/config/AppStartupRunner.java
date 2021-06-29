package org.sliga.usersmanagement.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;

import static org.sliga.usersmanagement.utils.FileConstants.USER_FOLDER;

@Component
public class AppStartupRunner implements ApplicationRunner {
    private static final Logger logger = LoggerFactory.getLogger(AppStartupRunner.class);

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if( new File(USER_FOLDER).mkdirs() )
            logger.info("Created USER_FODER : " + USER_FOLDER);
    }
}