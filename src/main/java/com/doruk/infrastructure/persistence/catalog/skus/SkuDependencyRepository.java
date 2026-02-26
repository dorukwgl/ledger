package com.doruk.infrastructure.persistence.catalog.skus;

import com.doruk.application.app.catalog.skus.dto.Dependency;
import com.doruk.application.app.catalog.skus.dto.SkuDependency;
import com.doruk.infrastructure.persistence.entity.SkuDependenciesDraft;
import com.doruk.infrastructure.persistence.entity.SkuDependenciesTable;
import com.doruk.infrastructure.persistence.entity.SkusTable;
import com.doruk.jooq.tables.SkuDependencies;
import com.doruk.jooq.tables.Skus;
import com.doruk.jooq.tables.records.SkuDependenciesRecord;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.jooq.DSLContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor
public class SkuDependencyRepository {
    private final JSqlClient client;
    private final DSLContext dsl;

    public Dependency save(SkuDependency dto) {
        var d = SkuDependencies.SKU_DEPENDENCIES;
        return dsl.insertInto(d)
                .set(
                        new SkuDependenciesRecord()
                                .setSkuId(dto.skuId())
                                .setTargetSkuId(dto.depsId())
                                .setDependencyType(dto.dependencyType())
                                .setEnforcedAt(dto.enforcedAt())
                )
                .returning()
                .fetchOne(rec -> Dependency.builder()
                        .id(rec.getId())
                        .depsId(rec.getTargetSkuId().toString())
                        .dependencyType(rec.getDependencyType())
                        .enforcedAt(rec.getEnforcedAt())
                        .createdAt(rec.getCreatedAt())
                        .build()
                );
    }

    public List<Dependency> getDependencies(UUID skuId) {
        var d = SkuDependencies.SKU_DEPENDENCIES;
        var s = Skus.SKUS;

        return dsl.select(
                        d.ID,
                        d.TARGET_SKU_ID,
                        d.DEPENDENCY_TYPE,
                        d.ENFORCED_AT,
                        d.CREATED_AT,
                        s.CODE
                )
                .from(d)
                .innerJoin(s).on(d.TARGET_SKU_ID.eq(s.ID))
                .where(d.SKU_ID.eq(skuId))
                .fetch(rec -> Dependency.builder()
                        .id(rec.value1())
                        .depsId(rec.value2().toString())
                        .dependencyType(rec.value3())
                        .enforcedAt(rec.value4())
                        .createdAt(rec.value5())
                        .depsCode(rec.value6())
                        .build()
                );
    }

    public Optional<Dependency> update(long id, SkuDependency dto) {
        var draft = SkuDependenciesDraft.$.produce(d -> d
                .setId(id)
                .setTargetSkuId(dto.depsId())
                .setDependencyType(dto.dependencyType())
                .setEnforcedAt(dto.enforcedAt())
        );
        var rs = client.saveCommand(draft).setMode(SaveMode.UPDATE_ONLY).execute();
        return rs.getTotalAffectedRowCount() < 1 ?
                Optional.empty() :
                Optional.of(rs.getModifiedEntity())
                        .map(d -> Dependency.builder()
                                .id(d.id())
                                .depsId(d.targetSku().id().toString())
                                .dependencyType(d.dependencyType())
                                .enforcedAt(d.enforcedAt())
                                .createdAt(d.createdAt())
                                .depsCode(d.targetSku().code())
                                .build()
                        );
    }

    public void delete(long id) {
        client.deleteById(SkuDependenciesTable.class, id);
    }

    public boolean skuExists(UUID skuId) {
        var t = SkusTable.$;
        return client.createQuery(t)
                .where(t.id().eq(skuId))
                .exists();
    }
}
