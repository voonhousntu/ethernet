CREATE TABLE ethernet.contracts
(
    `log_index`         int,
    `transaction_hash`  string,
    `transaction_index` int,
    `address`           string,
    `data`              string,
    `topics`            string,
    `block_timestamp`   timestamp,
    `block_number`      bigint,
    `block_hash`        string

)
    PARTITIONED BY
        (
        `block_number` bigint,
        `block_timestamp` timestamp
        )
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');