package com.doruk.infrastructure.config;

import com.doruk.infrastructure.annotataions.ValidUserAgent;
import io.micronaut.context.annotation.Factory;
import io.micronaut.validation.validator.constraints.ConstraintValidator;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

import java.util.Locale;
import java.util.Set;

@Factory
@RequiredArgsConstructor
public class UserAgentValidationFactory {
    private final UserAgentAnalyzer uaa;

    @Singleton
    ConstraintValidator<ValidUserAgent, String> validUserAgentValidator() {
        return (value, annotationMetadata, context) -> {
            if (value == null || value.isBlank())
                return false;  // @NotBlank already handles this

            UserAgent agent = uaa.parse(value);
            String browser = agent.getValue(UserAgent.AGENT_NAME);
            String browserVersion = agent.getValue(UserAgent.AGENT_VERSION_MAJOR);
            String os = agent.getValue(UserAgent.OPERATING_SYSTEM_NAME);

            // Check if it confidently identified key parts
            return !"Unknown".equalsIgnoreCase(browser) &&
                    !"??".equalsIgnoreCase(browser) &&
                    !"Unknown".equalsIgnoreCase(os) &&
                    !"??".equalsIgnoreCase(os) &&
                    browserVersion != null && // Has some version
                    !"Unknown".equalsIgnoreCase(browserVersion) &&
                    !"??".equalsIgnoreCase(browserVersion);
        };
    }
}
