package com.microservices.demo.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.IdGenerator;
import org.springframework.util.JdkIdGenerator;

/**
 * IdGeneratorConfig
 * <p>
 * UUID generation should be handled (in code) before database operations take place, so batch inserts can work
 * properly
 * <p>
 * That's why we have created bean idGenerator and did not let database create uuids automatically via
 * auto-insert
 * <p>
 * For batch inserting, UUIDs must exist, and if they are not preprovided batch insert will fail
 */

@Configuration
public class IdGeneratorConfig
{
    @Bean
    public IdGenerator idGenerator()
    {
        return new JdkIdGenerator();
    }
}

