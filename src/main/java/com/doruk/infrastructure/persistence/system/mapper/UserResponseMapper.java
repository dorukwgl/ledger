package com.doruk.infrastructure.persistence.system.mapper;

import com.doruk.application.app.system.dto.UserResponse;
import com.doruk.application.dto.PageResponse;
import com.doruk.application.dto.StoredObject;
import com.doruk.infrastructure.persistence.entity.Permission;
import com.doruk.infrastructure.persistence.entity.Role;
import com.doruk.infrastructure.persistence.entity.User;
import org.babyfish.jimmer.Page;

import java.util.Objects;

public class UserResponseMapper {
    public static PageResponse<UserResponse> toUserPageResponse(Page<User> userPage) {
        return PageResponse.<UserResponse>builder()
                .totalPageCount(userPage.getTotalPageCount())
                .totalRowCount(userPage.getTotalRowCount())
                .data(userPage.getRows().stream().map(
                        r -> {
                            var p = Objects.requireNonNull(r.profile()).profileIcon();
                            return UserResponse.builder()
                                    .id(r.id())
                                    .email(r.email())
                                    .username(r.username())
                                    .roles(r.roles().stream().map(Role::name).toList())
                                    .permissions(r.roles().stream().flatMap(role ->
                                            role.permissions().stream().map(Permission::name)).toList())
                                    .profileIcon(StoredObject.builder()
                                            .mimeType(p.mimeType())
                                            .size(p.size())
                                            .objectKey(p.objectKey())
                                            .visibility(p.visibility())
                                            .build())
                                .build();
                        }
                ).toList())
                .build();
    }
}
