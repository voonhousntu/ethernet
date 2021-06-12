CREATE TABLE IF NOT EXISTS ethernet.tokens
(
    `address`         string,
    `symbol`          string,
    `name`            string,
    `decimals`        string,
    `total_supply`    string,
    `block_timestamp` timestamp,
    `block_number`    bigint,
    `block_hash`      string
)
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');