-- Create Accounts table
CREATE TABLE accounts (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    account_type VARCHAR(50) NOT NULL CHECK (account_type IN ('USER', 'MERCHANT', 'SYSTEM'))
);

-- Insert Default Accounts
INSERT INTO accounts (id, name, account_type)
VALUES ('00000000-0000-0000-0000-000000000001', 'System Account', 'SYSTEM');
INSERT INTO accounts (id, name, account_type)
VALUES ('00000000-0000-0000-0000-000000000002', 'Testing User Account', 'USER');
INSERT INTO accounts (id, name, account_type)
VALUES ('00000000-0000-0000-0000-000000000003', 'Testing Merchant Account', 'MERCHANT');

-- Create Transactions table
CREATE TABLE transactions (
    id UUID PRIMARY KEY,
    from_account_id UUID NOT NULL,
    to_account_id UUID NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    timestamp TIMESTAMP NOT NULL,
    trx_type VARCHAR(50) NOT NULL CHECK (trx_type IN ('RECHARGE', 'PURCHASE')),
    CONSTRAINT fk_from_account FOREIGN KEY (from_account_id) REFERENCES accounts(id),
    CONSTRAINT fk_to_account FOREIGN KEY (to_account_id) REFERENCES accounts(id)
);

-- Create Products table
CREATE TABLE products (
    sku VARCHAR(50) PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Insert default products
INSERT INTO products (sku, name)
VALUES ('tesla-model-x', 'Tesla Model X');
INSERT INTO products (sku, name)
VALUES ('tesla-model-s', 'Tesla Model S');

-- Create Merchant Products table
CREATE TABLE merchant_products (
    merchant_id UUID NOT NULL,
    sku VARCHAR(50) NOT NULL,
    price DECIMAL(19,2) NOT NULL,
    stock INT NOT NULL,
    PRIMARY KEY (merchant_id, sku),
    CONSTRAINT fk_merchant FOREIGN KEY (merchant_id) REFERENCES accounts(id),
    CONSTRAINT fk_product FOREIGN KEY (sku) REFERENCES products(sku)
);

-- Insert default stock to merchant's inventory
INSERT INTO merchant_products (merchant_id, sku, price, stock)
VALUES ('00000000-0000-0000-0000-000000000003', 'tesla-model-x', 99990, 10);