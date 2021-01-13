CREATE TABLE ethernet.tokens
(
    `address`         string,
    `symbol`          string,
    `name`            string,
    `decimals`        string,
    `total_supply`    string,
    `block_hash`      string,
    `block_number`    bigint,
    `block_timestamp` timestamp
)
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');