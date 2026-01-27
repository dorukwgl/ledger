package com.doruk.infrastructure.config;

import com.doruk.infrastructure.logging.LoggingService;
import io.micronaut.context.annotation.Factory;
import io.micronaut.context.annotation.Primary;
import jakarta.inject.Singleton;
import org.babyfish.jimmer.meta.ImmutableProp;
import org.babyfish.jimmer.sql.EnumType;
import org.babyfish.jimmer.sql.JSqlClient;
import org.babyfish.jimmer.sql.dialect.Dialect;
import org.babyfish.jimmer.sql.dialect.H2Dialect;
import org.babyfish.jimmer.sql.dialect.MySqlDialect;
import org.babyfish.jimmer.sql.dialect.PostgresDialect;
import org.babyfish.jimmer.sql.fetcher.ReferenceFetchType;
import org.babyfish.jimmer.sql.runtime.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.sql.DataSource;
import java.sql.Connection;


@Factory
public class JSqlClientFactory {
    @Singleton
    @Primary
    public JSqlClient create(JimmerConfig config, DataSource dataSource) {

        JSqlClient.Builder builder = JSqlClient.newBuilder();

        builder.setConnectionManager(ConnectionManager.simpleConnectionManager(dataSource));

        builder.setDefaultEnumStrategy(EnumType.Strategy.NAME);
        builder.setDefaultReferenceFetchType(ReferenceFetchType.JOIN_IF_NO_CACHE);

        builder.setDialect(config != null && config.dialect() != null ?
                createDialect(config.dialect()) : new PostgresDialect());

        if (config == null)
            return builder.build();

        if (config.showSql())
            builder.setExecutor(createLoggingExecutor(config));
        else
            builder.setExecutor(DefaultExecutor.INSTANCE);

        return builder.build();
    }

    private Executor createLoggingExecutor(JimmerConfig config) {
        return new Executor() {
            @Override
            public <R> R execute(Args<R> args) {
                if (config.showSql()) {
                    LoggingService.logInfo(args.sql);
                }

                return DefaultExecutor.INSTANCE.execute(args);
            }

            @Override
            public BatchContext executeBatch(@NotNull Connection con, @NotNull String sql, @Nullable ImmutableProp generatedIdProp, @NotNull ExecutionPurpose purpose, @NotNull JSqlClientImplementor sqlClient) {
                if (config.showSql()) {
                    LoggingService.logInfo(sql);
                }

                return DefaultExecutor.INSTANCE.executeBatch(con, sql, generatedIdProp, purpose, sqlClient);
            }
        };
    }

    private Dialect createDialect(String dialectName) {
        return switch (dialectName.toLowerCase()) {
            case "mysql" -> new MySqlDialect();
            case "postgres" -> new PostgresDialect();
            case "h2" -> new H2Dialect();
            default -> throw new IllegalArgumentException("Unsupported dialect: " + dialectName);
        };
    }
}
