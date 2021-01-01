CREATE TABLE ethernet.contracts
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
    PARTITIONED BY
        (
        `block_number` bigint,
        `block_timestamp` timestamp
        )
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');