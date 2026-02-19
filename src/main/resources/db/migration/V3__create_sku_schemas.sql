CREATE TYPE product_kind_enum AS ENUM ('SOFTWARE', 'SERVICE', 'HOSTING');
CREATE TYPE ownership_enum AS ENUM ('OWNED', 'MANAGED', 'RESELL');
CREATE TYPE status_enum AS ENUM ('active', 'inactive', 'deprecated');

CREATE TYPE delivery_model_enum AS ENUM ('SAAS', 'SELF_HOSTED', 'MANAGED_INSTANCE');
CREATE TYPE infra_model_enum AS ENUM ('SHARED', 'DEDICATED', 'CUSTOMER_OWNED');
CREATE TYPE operational_owner_enum AS ENUM ('COMPANY', 'CUSTOMER', 'HYBRID');

CREATE TYPE sku_category_enum AS ENUM (
    'BASE', 'ADDON', 'PLUGIN', 'MAINTENANCE', 'SUPPORT', 'HOSTING'
    );

CREATE TYPE sku_scope_enum AS ENUM ('ACCOUNT', 'INSTANCE', 'USER', 'SERVER');

CREATE TYPE feature_value_type_enum AS ENUM ('boolean', 'number', 'string');

CREATE TYPE dependency_type_enum AS ENUM (
    'REQUIRES', 'EXCLUDES', 'UPGRADES_FROM', 'CONVERTS_FROM'
    );

CREATE TYPE enforced_at_enum AS ENUM ('PURCHASE', 'ACTIVATION');

-- CREATE TABLES

CREATE TABLE products
(
    id           BIGSERIAL PRIMARY KEY,
    code         TEXT                     NOT NULL UNIQUE,
    name         TEXT                     NOT NULL,
    description  TEXT,

    product_kind product_kind_enum        NOT NULL,
    ownership    ownership_enum           NOT NULL,
    status       status_enum              NOT NULL,

    created_at   TIMESTAMP with time zone NOT NULL DEFAULT NOW(),
    updated_at   TIMESTAMP with time zone NOT NULL DEFAULT NOW()
);

CREATE TABLE offerings
(
    id                BIGSERIAL PRIMARY KEY,
    product_id        BIGINT                   NOT NULL REFERENCES products (id),

    code              TEXT                     NOT NULL UNIQUE,
    name              TEXT                     NOT NULL,

    delivery_model    delivery_model_enum      NOT NULL,
    infra_model       infra_model_enum         NOT NULL,
    operational_owner operational_owner_enum   NOT NULL,

    status            status_enum              NOT NULL,

    created_at        TIMESTAMP with time zone NOT NULL DEFAULT NOW(),
    updated_at        TIMESTAMP with time zone NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_offering_product_id ON offerings (product_id);

CREATE TABLE skus
(
    id               UUID PRIMARY KEY                  default uuidv7(),
    offering_id      BIGINT                   NOT NULL REFERENCES offerings (id),

    code             TEXT                     NOT NULL UNIQUE,
    name             TEXT                     NOT NULL,

    category         sku_category_enum        NOT NULL,
    scope            sku_scope_enum           NOT NULL,

    is_freemium      BOOLEAN                  NOT NULL DEFAULT FALSE,
    trial_days       INTEGER CHECK (trial_days IS NULL OR trial_days >= 0),
    term_hint_months INTEGER,

    status           status_enum              NOT NULL,
    published_at     TIMESTAMP with time zone,

    created_at       TIMESTAMP with time zone NOT NULL DEFAULT NOW(),
    updated_at       TIMESTAMP with time zone NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_sku_offering_id ON skus (offering_id);
CREATE INDEX idx_sku_category ON skus (category);

CREATE TABLE tiers
(
    id         BIGSERIAL PRIMARY KEY,
    sku_id     UUID                     NOT NULL REFERENCES skus (id),

    code       TEXT                     NOT NULL,
    name       TEXT                     NOT NULL,
    is_default BOOLEAN                  NOT NULL DEFAULT FALSE,

    status     status_enum              NOT NULL,

    created_at TIMESTAMP with time zone NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP with time zone NOT NULL DEFAULT NOW(),

    UNIQUE (sku_id, code)
);

CREATE INDEX idx_tier_sku_id ON tiers (sku_id);

CREATE TABLE features
(
    id          BIGSERIAL PRIMARY KEY,
    code        TEXT                     NOT NULL UNIQUE,
    name        TEXT                     NOT NULL,
    description TEXT,

    value_type  feature_value_type_enum  NOT NULL,
    unit        TEXT,

    created_at  TIMESTAMP with time zone NOT NULL DEFAULT NOW()
);

CREATE TABLE tier_features
(
    tid        BIGSERIAL primary key,
    tier_id    BIGINT NOT NULL REFERENCES tiers (id) on delete cascade,
    feature_id BIGINT NOT NULL REFERENCES features (id) on delete cascade,
    value      TEXT   NOT NULL
);
CREATE unique index idx_tier_feature ON tier_features (tier_id, feature_id);
CREATE INDEX idx_tier ON tier_features (tier_id);

CREATE TABLE sku_dependencies
(
    id              BIGSERIAL PRIMARY KEY,

    sku_id          UUID                     NOT NULL REFERENCES skus (id) on delete cascade,
    target_sku_id   UUID                     NOT NULL REFERENCES skus (id) on delete cascade,

    dependency_type dependency_type_enum     NOT NULL,
    enforced_at     enforced_at_enum         NOT NULL,

    created_at      TIMESTAMP with time zone NOT NULL DEFAULT NOW(),

    UNIQUE (sku_id, target_sku_id, dependency_type)
);

CREATE INDEX idx_sku_dependency_sku ON sku_dependencies (sku_id);
CREATE INDEX idx_sku_dependency_target ON sku_dependencies (target_sku_id);
