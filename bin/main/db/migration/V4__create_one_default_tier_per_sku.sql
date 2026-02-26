CREATE UNIQUE INDEX ux_one_default_tier_per_sku
    ON tiers (sku_id)
    WHERE is_default = true;