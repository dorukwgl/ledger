package com.doruk.presentation.auth.controller;

import com.doruk.application.app.auth.dto.DeviceInfoObject;
import com.doruk.application.app.auth.dto.LoginResponse;
import com.doruk.application.app.auth.service.BiometricService;
import com.doruk.infrastructure.dto.InfoResponse;
import com.doruk.presentation.auth.dto.BiometricEnrollRequest;
import com.doruk.presentation.auth.dto.BiometricVerifyRequest;
import io.micronaut.http.HttpRequest;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.RequestBean;
import io.micronaut.http.server.binding.RequestArgumentSatisfier;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Controller("biometrics")
@Secured(SecurityRule.IS_AUTHENTICATED)
@RequiredArgsConstructor
public class BiometricController {
    private final BiometricService service;
    private final RequestArgumentSatisfier requestArgumentSatisfier;

    @Operation(description = "Setup the biometric authentication")
    @Post("/enroll")
    public InfoResponse enrollBiometrics(Authentication auth, @Valid @Body BiometricEnrollRequest biometrics) {
        service.enrollBiometrics(biometrics.deviceId(), auth.getName(), biometrics.publicKey());
        return new InfoResponse("Biometrics is now enabled for this device...");
    }

    @Operation(description = "Initiate the biometric verification challenge")
    @Post("/perform/init")
    public HttpResponse<Map<String, String>> initBiometrics(Authentication auth,
                                                            @Parameter(description = "Unique device identifier, the one sent during enrollment")
                                                            @Body
                                                            @Size(min = 64, max = 72)
                                                            String deviceId,
                                                            HttpRequest<?> request) {
        String ip = request.getRemoteAddress()
                .getAddress()
                .getHostAddress();
        var challenge = service.initBiometrics(deviceId, ip);
        return HttpResponse.status(202, "Signature Required").body(Map.of("challenge", challenge));
    }

    @Operation(description = "Second step to the biometric, sign the challenge and submit the signature here to verify")
    @Post("/perform/verify")
    public LoginResponse performBiometrics(HttpRequest<?> request,
                                           @Valid
                                           @RequestBean
                                           BiometricVerifyRequest dto
    ) {
        var ip = request.getRemoteAddress()
                .getAddress()
                .getHostAddress();

        return service.performBiometrics(
                dto.deviceId(),
                dto.notifId(),
                dto.challenge(),
                dto.signature(),
                ip,
                new DeviceInfoObject(dto.notifId(), dto.userAgent()));
    }
}
