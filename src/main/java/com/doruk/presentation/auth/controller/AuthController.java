package com.doruk.presentation.auth.controller;

import com.doruk.application.app.auth.dto.AuthUpdateResponse;
import com.doruk.application.app.auth.dto.JwtResponse;
import com.doruk.application.app.auth.dto.LoginResponse;
import com.doruk.application.app.auth.dto.SessionDto;
import com.doruk.application.app.auth.service.AuthService;
import com.doruk.domain.shared.enums.MultiAuthType;
import com.doruk.infrastructure.config.AppConfig;
import com.doruk.infrastructure.dto.InfoResponse;
import com.doruk.infrastructure.util.Constants;
import com.doruk.presentation.auth.dto.DeviceInfoRequest;
import com.doruk.presentation.auth.dto.LoginRequest;
import com.doruk.presentation.auth.mappers.DeviceInfoMapper;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.cookie.Cookie;
import io.micronaut.http.cookie.SameSite;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.RequiredArgsConstructor;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Tag(name = "Authentications")
@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService service;
    private final DeviceInfoMapper infoMapper;
    private final AppConfig appConfig;

    private String renderResetFormPage(String magic) {
        var str = """
                <!DOCTYPE html>
                <html lang="en">
                <head>
                    <meta charset="UTF-8">
                    <title>Reset Password</title>
                    <meta name="viewport" content="width=device-width, initial-scale=1">
                    <style>
                        body {
                            font-family: system-ui, -apple-system, BlinkMacSystemFont, sans-serif;
                            background: #f5f6f8;
                            display: flex;
                            justify-content: center;
                            align-items: center;
                            height: 100vh;
                            margin: 0;
                        }
                        .card {
                            background: white;
                            width: 100%;
                            max-width: 420px;
                            padding: 32px;
                            border-radius: 12px;
                            box-shadow: 0 10px 25px rgba(0,0,0,0.08);
                        }
                        h1 {
                            margin: 0 0 16px;
                            font-size: 22px;
                            text-align: center;
                        }
                        p {
                            color: #666;
                            font-size: 14px;
                            text-align: center;
                        }
                        label {
                            display: block;
                            margin-top: 20px;
                            font-weight: 500;
                        }
                        input {
                            width: 100%;
                            padding: 12px;
                            margin-top: 8px;
                            border-radius: 8px;
                            border: 1px solid #ccc;
                            font-size: 14px;
                        }
                        button {
                            width: 100%;
                            margin-top: 24px;
                            padding: 12px;
                            border: none;
                            border-radius: 8px;
                            background: #1f2937;
                            color: white;
                            font-size: 15px;
                            cursor: pointer;
                        }
                        button:hover {
                            background: #111827;
                        }
                    </style>
                </head>
                <body>
                <div class="card">
                    <h1>Reset your password</h1>
                    <p>Please enter a new password for your account.</p>
                
                    <form method="post" action="/forgot-password/verify-update-magic/%s"
                          onsubmit="return validatePasswords()">
                
                        <label for="password">New password</label>
                        <input type="password" id="password" name="password" minlength="8" required>
                
                        <label for="confirmPassword">Confirm password</label>
                        <input type="password" id="confirmPassword" name="confirmPassword" minlength="8" required>
                
                        <button type="submit">Update password</button>
                    </form>
                </div>
                
                <script>
                    function validatePasswords() {
                        const p1 = document.getElementById('password').value;
                        const p2 = document.getElementById('confirmPassword').value;
                        if (p1 !== p2) {
                            alert("Passwords do not match.");
                            return false;
                        }
                        return true;
                    }
                </script>
                </body>
                </html>
                """;
        return String.format(str, magic);
    }

    @ApiResponse(responseCode = "201", description = "Login successful")
    @ApiResponse(responseCode = "202", description = "Multi factor authentication required")
    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post("/login")
    HttpResponse<LoginResponse> login(
            @Valid @Body LoginRequest request,
            @Valid @RequestBean DeviceInfoRequest info) {
        var response = service.performLogin(request.identifier(),
                request.password(),
                infoMapper.toDeviceInfo(info)
        );

        if (response.mfaRequired())
            return HttpResponse.status(202, "MFA Required").body(response);

        // now set cookies to the response
        return HttpResponse.created(response)
                .cookie(Cookie.of(Constants.SESSION_COOKIE_HEADER, response.refreshToken())
                        .httpOnly()
                        .secure(appConfig.cookieSecure())
                        .maxAge(Duration.ofDays(appConfig.sessionExpiration()))
                        .path("/app/auth/session")
                        .sameSite(SameSite.Lax));
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Post("/mfa/verify/{mfaToken}")
    LoginResponse verifyMfa(
            @Valid @RequestBean DeviceInfoRequest info,
            String mfaToken,
            @Max(value = 999999, message = "OTP must be 6 digits")
            @Min(value = 100000, message = "OTP must be 6 digits")
            int otp
    ) {
        return service.performMfa(mfaToken, otp, infoMapper.toDeviceInfo(info));
    }

    @ApiResponse(responseCode = "200", description = "Access token refreshed successfully")
    @ApiResponse(responseCode = "401", description = "Invalid or expired session. Please login again.")
    @Secured(SecurityRule.IS_ANONYMOUS)
    @Get("/session/refresh")
    HttpResponse<JwtResponse> refreshAccessToken(@CookieValue(Constants.SESSION_COOKIE_HEADER) String info) {
        return HttpResponse.ok().body(service.refreshAccessToken(info));
    }
// #####################################################################################

    @Operation(description = "View the list of logged in devices")
    @Get("/session/list")
    HttpResponse<List<SessionDto>> listSessions(Authentication auth) {
        return HttpResponse.ok()
                .body(service.listSessions(auth.getName()));
    }

    @Operation(description = "Sign Out from current device")
    @Delete("/session/logout")
    HttpResponse<?> logout(@CookieValue(Constants.SESSION_COOKIE_HEADER) String sessionId) {
        service.logoutCurrent(sessionId);
        return HttpResponse.noContent();
    }

    @Operation(description = "Sign Out from all devices")
    @Delete("/session/logout-all{?biometric}")
    HttpResponse<?> logoutAll(Authentication auth,
                              @Parameter(description = "Also removes the biometrics data, if set to true")
                              @QueryValue(defaultValue = "false")
                              boolean biometric) {
        service.logoutAll(auth.getName(), biometric);
        return HttpResponse.noContent();
    }

    @Operation(description = "Sign Out from other logged in devices, except the current one.")
    @Delete("/session/logout-others{?biometric}")
    HttpResponse<?> logoutOthers(Authentication auth,
                                 @CookieValue(Constants.SESSION_COOKIE_HEADER)
                                 String sessionId,
                                 @Parameter(description = "Also removes the biometrics data, if set to true")
                                 @QueryValue(defaultValue = "false")
                                 boolean biometric) {
        service.logoutOthers(auth.getName(), sessionId, biometric);
        return HttpResponse.noContent();
    }

    @Operation(description = "Change Password")
    @Put("/password")
    HttpResponse<?> changePassword(Authentication auth,
                                   @Body
                                   @Size(min = 8, max = 50)
                                   String newPassword,
                                   @Body
                                   @Size(min = 8, max = 50)
                                   String password) {
        service.updatePassword(auth.getName(), password, newPassword);
        return HttpResponse.noContent();
    }

    @Status(HttpStatus.ACCEPTED)
    @Post("/email/init-verification")
    public Map<String, String> initEmailVerification(Authentication auth) {
        return service.initEmailVerification(auth.getName());
    }

    @Post("/email/verify-otp/{transactionId}")
    public InfoResponse verifyEmailOtp(Authentication auth,
                                       String transactionId,
                                       @Body
                                       @NotBlank
                                       @Min(value = 100000)
                                       @Max(value = 999999)
                                       int otp) {

        service.verifyEmail(auth.getName(), transactionId, otp);
        return new InfoResponse("Email verified successfully");
    }

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Get("/email/verify-magic/{magicLink}")
    public InfoResponse verifyEmailViaMagicLink(String magicLink) {
        service.verifyEmail(magicLink);
        return new InfoResponse("Email verified successfully");
    }

    @Status(HttpStatus.ACCEPTED)
    @Post("/phone/init-verification")
    public Map<String, String> initPhoneVerification(Authentication auth) {
        return service.initPhoneVerification(auth.getName());
    }

    @Post("/phone/verify-otp/{transactionId}")
    public InfoResponse verifyPhoneNumber(
            String transactionId,
            @Body
            @NotBlank
            @Min(value = 100000)
            @Max(value = 999999)
            int otp) {
        service.verifyPhone(transactionId, otp);
        return new InfoResponse("Phone number verified successfully");
    }

    @Operation(description = "Update the existing email address")
    @ApiResponse(
            description = "When existing email address is not verified.",
            responseCode = "200"
    )
    @ApiResponse(
            description = "When existing email address is verified.",
            responseCode = "202"
    )
    @Put("/update/email")
    public HttpResponse<AuthUpdateResponse> updateEmail(Authentication auth,
                                                        @Email
                                                        @NotBlank
                                                        String email) {
        var res = service.updateEmail(auth.getName(), email);
        if (res.otpRequired())
            return HttpResponse.accepted().body(res);
        return HttpResponse.ok().body(res);
    }

    @Operation(description = "Update existing or add new phone number")
    @ApiResponse(
            description = "When existing phone number is not verified",
            responseCode = "200"
    )
    @ApiResponse(
            description = "When existing phone number is verified",
            responseCode = "202"
    )
    @Put("/update/phone")
    public HttpResponse<AuthUpdateResponse> updatePhone(Authentication auth,
                                                        @NotBlank
                                                        @Pattern(regexp = "^\\+?\\d{6,14}$\n")
                                                        String phone) {
        var res = service.updatePhone(auth.getName(), phone);
        if (res.otpRequired())
            return HttpResponse.accepted().body(res);
        return HttpResponse.ok().body(res);
    }

    @Operation(description = "Verify the update phone transaction. Second step of the route /update/phone")
    @Put("/update/phone/verify")
    public InfoResponse verifyUpdatePhone(Authentication auth,
                                          @Size(max = 80)
                                          @Body
                                          String tid,
                                          @Body
                                          @Max(999999)
                                          @Min(1)
                                          int otp
    ) {
        service.verifyUpdatePhoneTransaction(auth.getName(), tid, otp);
        return new InfoResponse("Phone number updated successfully");
    }

    @Operation(description = "Verify the update email transaction. Second step of the route /update/email")
    @Put("/update/email/verify")
    public InfoResponse verifyUpdateEmail(Authentication auth,
                                          @Size(max = 80)
                                          @Body
                                          String tid,
                                          @Body
                                          @Max(999999)
                                          @Min(1)
                                          int otp) {
        service.verifyUpdateEmailTransaction(auth.getName(), tid, otp);
        return new InfoResponse("Email address updated successfully");
    }

    @Operation(description = "Initiate password reset")
    @Post("/forgot-password/init{?usePhone}")
    public Map<String, String> initPasswordReset(@Body @Size(max = 500) String identifier,
                                                 @QueryValue(defaultValue = "false") boolean usePhone) {
        return service.initPasswordReset(identifier, usePhone);
    }

    @Operation(description = "Verify the otp and update the password")
    @Put("/forgot-password/verify-update-otp/{tid}")
    public InfoResponse verifyAndUpdatePassword(@PathVariable String tid,
                                                @Body
                                                @Min(0)
                                                @Max(999999)
                                                int otp,
                                                @Body
                                                @Size(min = 8, max = 35)
                                                String password) {

        service.verifyAndResetPasswordOtp(tid, otp, password);
        return new InfoResponse("Password updated successfully");
    }

    @Operation(description = "Returns the password reset page for given magic link")
    @Get(value = "/forgot-password/verify-update-magic/{magic}",
            produces = MediaType.TEXT_HTML)
    public HttpResponse<String> getMagicPasswordResetPage(
            @PathVariable
            String magic
    ) {
        return HttpResponse.ok(renderResetFormPage(magic))
                .contentType(MediaType.TEXT_HTML);
    }

    @Operation(description = "Update password via given reset link")
    @Post("/forgot-password/verify-update-magic/{magic}")
    public InfoResponse verifyAndUpdatePasswordMagic(
            @PathVariable
            String magic,
            @Body
            @Size(min = 8, max = 25)
            String password) {
        service.verifyAndResetPasswordMagic(magic, password);
        return new InfoResponse("Password updated successfully");
    }

    @Operation(description = "Resend Email/Phone verification OTP")
    @CustomHttpMethod(value = "/otp/resend-verification/{tid}", method = "repeat")
    public InfoResponse resendOtp(String tid) {
        service.resendVerificationOtp(tid);
        return new InfoResponse("OTP sent successfully");
    }

    @Operation(description = "Resend MFA Phone/Email OTP")
    @CustomHttpMethod(value = "/otp/resend-mfa", method = "repeat")
    public InfoResponse resendMfaOtp(String tid) {
        service.resendMfaOtp(tid);
        return new InfoResponse("MFA otp sent successfully");
    }

    @Operation(description = "Resend Email or Phone update OTP")
    @CustomHttpMethod(value = "/otp/resend-auth-update", method = "repeat")
    public InfoResponse resendAuthUpdateOtp(String tid) {
        service.resendAuthUpdateOtp(tid);
        return new InfoResponse("OTP sent successfully");
    }

    @Operation(description = "Resend password reset OTP to Phone or Email")
    @CustomHttpMethod(value = "/otp/resend-pw-reset", method = "repeat")
    public InfoResponse resendPasswordResetOtp(String tid) {
        service.resendPasswordResetOtp(tid);
        return new InfoResponse("Password reset OTP sent successfully");
    }

    @Operation(description = "Enable Multi Factor Authorixation (MFA)")
    @Post("/mfa/enable/{auth}")
    public InfoResponse enableMultiFactorAuthorization(Authentication user, MultiAuthType auth) {
        service.enableMfa(user.getName(), auth);
        return new InfoResponse("MFA enabled successfully");
    }

    @Operation(description = "Disable MFA")
    @Post("/fa/disable")
    public InfoResponse disableMultiFactorAuthorization(Authentication user,
                                                        @Body
                                                        @Size(max = 50)
                                                        String password) {
        service.disableMfa(user.getName(), password);
        return new InfoResponse("MFA disabled successfully");
    }
}
