CREATE TABLE stock_entries
(
    id         BIGSERIAL PRIMARY KEY,
    product_id BIGINT    NOT NULL,
    quantity   INTEGER   NOT NULL CHECK (quantity > 0),
    reason     VARCHAR(255),
    date       TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_stock_entries_product
        FOREIGN KEY (product_id)
            REFERENCES products (id)
            ON DELETE CASCADE
);
