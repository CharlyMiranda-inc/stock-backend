-- Eliminamos campos que ya no se usan
ALTER TABLE products
DROP COLUMN IF EXISTS category,
DROP COLUMN IF EXISTS provider,
DROP COLUMN IF EXISTS price;

-- Agregamos los campos nuevos
ALTER TABLE products
    ADD COLUMN size VARCHAR(20),
ADD COLUMN color VARCHAR(50),
ADD COLUMN net_price DOUBLE PRECISION,
ADD COLUMN list_price DOUBLE PRECISION,
ADD COLUMN transfer_price DOUBLE PRECISION,
ADD COLUMN cash_price DOUBLE PRECISION;