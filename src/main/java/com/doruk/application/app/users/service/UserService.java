package com.doruk.application.app.users.service;

import com.doruk.application.app.users.dto.*;
import com.doruk.application.dto.StoredObject;
import com.doruk.application.dto.UploadedFile;
import com.doruk.application.events.ProfileImageUploadEvent;
import com.doruk.application.exception.ConflictingArgumentException;
import com.doruk.application.interfaces.EventPublisher;
import com.doruk.application.interfaces.ObjectStorage;
import com.doruk.application.security.PasswordEncoder;
import com.doruk.infrastructure.persistence.files.FileRepository;
import com.doruk.infrastructure.persistence.users.UserRepository;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.util.Map;
import java.util.Optional;

@Singleton
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepo;
    private final EventPublisher event;
    private final PasswordEncoder passwordEncoder;
    private final FileRepository fileRepo;
    private final ObjectStorage storage;

    private void checkConflict(UserUniqueFields src, CreateUserCmd req) {
        if (req.email().equals(src.email()))
            throw new ConflictingArgumentException("Email address already taken");
        // its username, as only to samples are there
        throw new ConflictingArgumentException("Username already taken");
    }

    public UserResponseDto registerUser(CreateUserCmd userDto) {
        var existing = userRepo.findByUsernameOrEmail(userDto.username(), userDto.email());
        existing.ifPresent(src -> this.checkConflict(src, userDto));

        // create the user
        return userRepo.createUser(userDto, passwordEncoder.encode(userDto.password()));
    }

    public ProfileDto updateProfile(String userId, ProfileDto cmd) {
        return userRepo.updateProfile(userId, cmd);
    }

    public Map<String, String> updateProfileIcon(String userId, UploadedFile icon) {
        var previous = userRepo.updateProfileIconReturningOld(userId, icon);
        var stored = icon.storedObject();

        event.publish(new ProfileImageUploadEvent(
                stored.objectKey(),
                stored.mimeType(),
                previous
        ));

        return Map.of("newProfilePicture", storage.resolveUrl(stored));
    }

    public CurrentUserDto getCurrentUser(String userId) {
        return userRepo.getCurrentUser(userId);
    }
}
