package com.doruk.infrastructure.config;

import io.micronaut.context.annotation.Factory;
import jakarta.inject.Singleton;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

@Factory
public class UserAgentParserFactory {
    @Singleton
    public UserAgentAnalyzer createUserAgentAnalyzer() {
        return UserAgentAnalyzer
                .newBuilder()
                .hideMatcherLoadStats()
                .withCache(10000)
                // Only request the essentials — avoids loading hacker rules
                .withField(UserAgent.AGENT_NAME)
                .withField(UserAgent.AGENT_VERSION_MAJOR)
                .withField(UserAgent.OPERATING_SYSTEM_NAME)
                .build();
    }
}
