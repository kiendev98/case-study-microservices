DROP TABLE IF EXISTS LICENSE;
CREATE TABLE LICENSE (
    license_id bigserial PRIMARY KEY,
    organization_id BIGINT,
    description VARCHAR(255),
    product_name VARCHAR(255),
    license_type VARCHAR(255)
);
