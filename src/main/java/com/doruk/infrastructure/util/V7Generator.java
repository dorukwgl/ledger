package com.doruk.infrastructure.util;

import com.fasterxml.uuid.Generators;
import org.babyfish.jimmer.sql.meta.UserIdGenerator;

import java.util.UUID;

public class V7Generator implements UserIdGenerator<UUID> {
    @Override
    public UUID generate(Class<?> entityType) {
        return Generators.timeBasedEpochGenerator().generate();
    }
}
