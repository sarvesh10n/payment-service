CREATE TABLE payment
(
    id             BIGINT AUTO_INCREMENT NOT NULL,
    created_at     datetime NULL,
    updated_at     datetime NULL,
    order_id       VARCHAR(255) NULL,
    payment_id     VARCHAR(255) NULL,
    refund_id      VARCHAR(255) NULL,
    status         VARCHAR(255) NULL,
    amount DOUBLE NULL,
    currency       VARCHAR(255) NULL,
    invoice_number VARCHAR(255) NULL,
    CONSTRAINT pk_payment PRIMARY KEY (id)
);