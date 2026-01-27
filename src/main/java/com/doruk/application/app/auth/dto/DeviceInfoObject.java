package com.doruk.application.app.auth.dto;

import com.doruk.application.exception.SuspiciousIntrusionException;
import io.micronaut.core.annotation.Introspected;
import lombok.AllArgsConstructor;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;

import java.util.Locale;
import java.util.Optional;
import java.util.Set;

@Introspected
@AllArgsConstructor
public class DeviceInfoObject {
    private String deviceId;
    private String userAgent;

    public Optional<String> deviceInfo(UserAgentAnalyzer uaa) {
        var agent = uaa.parse(userAgent);
        var name = agent.getValue(UserAgent.AGENT_NAME);
        var version = agent.getValue(UserAgent.AGENT_VERSION_MAJOR);
        var platform = agent.getValue(UserAgent.OPERATING_SYSTEM_NAME);

        if (Set.of("robot", "mobile robot", "hacker").contains(name.toLowerCase(Locale.ROOT)))
            throw new SuspiciousIntrusionException();

        return Optional.of(String.format("%s %s on %s", name, version, platform));
    }

    public Optional<String> deviceId() {
        return Optional.ofNullable(this.deviceId);
    }
}
