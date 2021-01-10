CREATE TABLE ethernet.logs
(
    `log_index`         int,
    `transaction_hash`  string,
    `transaction_index` int,
    `address`           string,
    `data`              string,
    `topics`            string,
    `block_hash`        string

)
    PARTITIONED BY
        (
        `block_number` bigint,
        `block_timestamp` timestamp
        )
    STORED AS ORC
    TBLPROPERTIES ('ORC.COMPRESS' = 'ZLIB');