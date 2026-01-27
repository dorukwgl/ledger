package com.doruk.presentation.users.controller;

import com.doruk.application.app.users.dto.CurrentUserDto;
import com.doruk.application.app.users.dto.ProfileDto;
import com.doruk.application.app.users.dto.UserResponseDto;
import com.doruk.application.app.users.service.UserService;
import com.doruk.application.files.FileService;
import com.doruk.presentation.users.dto.ProfileUpdateRequest;
import com.doruk.presentation.users.dto.RegistrationRequest;
import com.doruk.presentation.users.mapper.ProfileMapper;
import com.doruk.presentation.users.mapper.RegistrationMapper;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.StreamingFileUpload;
import io.micronaut.security.annotation.Secured;
import io.micronaut.security.authentication.Authentication;
import io.micronaut.security.rules.SecurityRule;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.Map;

@Tag(name = "User Management, Registrations & Profiles")
@RequiredArgsConstructor
@Controller("users")
@Secured(SecurityRule.IS_AUTHENTICATED)
public class UserController {
    private final UserService service;
    private final RegistrationMapper registrationMapper;
    private final ProfileMapper profileMapper;
    private final FileService fileService;

    @Secured(SecurityRule.IS_ANONYMOUS)
    @Status(HttpStatus.CREATED)
    @Post("/register")
    public UserResponseDto registerUser(@Valid @Body RegistrationRequest req) {
        return service.registerUser(registrationMapper.toUserCmdDto(req));
    }

    @Operation(summary = "Get information about current user")
    @CustomHttpMethod(method = "info")
    public CurrentUserDto test(Authentication auth) {
        return service.getCurrentUser(auth.getName());
    }

    @Put("/profile")
    public ProfileDto updateProfile(Authentication auth, @Valid @Body ProfileUpdateRequest req) {
        return service.updateProfile(auth.getName(), profileMapper.toProfileDto(req));
    }

    @Put("/profile/icon")
    public Map<String, String> updateProfileIcon(Authentication auth,
                                                 @Part("profile-icon")
                                                 StreamingFileUpload file) {
        var uploaded = fileService.imageUploadPublic(file);
        return service.updateProfileIcon(auth.getName(), uploaded);
    }
}
